package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbReportMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CompareInterval;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorStatusDesc;
import cn.shmedo.monitor.monibotbaseapi.model.param.report.WtQueryReportParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.WtQueryReportInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbReportService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:09
 * @see SensorDataDao#querySensorNewData(List, List, boolean, Integer)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbReportServiceImpl implements ITbReportService {
    private final RedisService redisService;
    private final TbReportMapper tbReportMapper;
    private final SensorDataDao sensorDataDao;

    @Override
    public WtQueryReportInfo queryReport(WtQueryReportParam param) {
        List<Integer> projectIDList = param.getProjectIDList();
        Date startTime = param.getStartTime();
        Date endTime = param.getEndTime();
        WtQueryReportInfo.WtQueryReportInfoBuilder builder = WtQueryReportInfo.builder().period(param.getPeriod())
                .companyName(Optional.of(param.getCompanyID()).map(Object::toString)
                        .map(u -> {
                            Collection<Object> collection = new ArrayList<>();
                            collection.add(u);
                            return collection;
                        }).map(u -> redisService.multiGet(RedisKeys.COMPANY_INFO_KEY, u, Company.class))
                        .filter(CollectionUtil::isNotEmpty).map(u -> u.get(0)).map(Company::getShortName)
                        .orElse("余姚市水务局"))
                .startTime(startTime).endTime(endTime);
        List<TbBaseReportInfo> tbBaseReportInfoList = tbReportMapper.queryBaseReportInfo(param.getCompanyID(),
                startTime, endTime);
        Map<Integer, Map<String, Object>> sensorIDResMap = querySensorNewData(tbBaseReportInfoList);
        Collection<Object> areaCodeList = tbBaseReportInfoList.stream().map(TbBaseReportInfo::getAreaCode).distinct()
                .map(u -> (Object) u).toList();
        Map<String, String> areaCodeNameMap = queryAreaData(areaCodeList);
        Map<String, List<TbBaseReportInfo>> typeClassInfoMap = tbBaseReportInfoList.stream()
                .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorTypeClass));
        builder.total(tbBaseReportInfoList.size()).monitorTypeClassList(typeClassInfoMap.keySet().stream().toList())
                .dataList(dealDataList(typeClassInfoMap, sensorIDResMap, areaCodeNameMap));
        if (CollectionUtil.isNotEmpty(projectIDList)) {
            builder.projectDataList(dealProjectData(projectIDList, startTime, endTime));
        }
        return builder.build();
    }

    private List<WtReportProjectInfo> dealProjectData(List<Integer> projectIDList, Date startTime, Date endTime) {
        Map<String, List<TbBaseReportInfo>> projectNameInfoMap = tbReportMapper.queryProjectReportInfo(
                projectIDList, startTime, endTime).stream().collect(Collectors.groupingBy(TbBaseReportInfo::getProjectName));
        return projectNameInfoMap.values().stream().map(u -> {    // level - project
            Map<String, List<TbBaseReportInfo>> monitorTypeMap = u.stream().collect(Collectors
                    .groupingBy(TbBaseReportInfo::getMonitorTypeName));
            WtReportProjectInfo.WtReportProjectInfoBuilder builder = WtReportProjectInfo.builder().total(u.size())
                    .projectName(u.get(0).getProjectName()).monitorTypeList(monitorTypeMap.keySet().stream().toList());
            List<WtReportMonitorTypeCountInfo> monitorTypeCountList = monitorTypeMap.entrySet().stream().map(w -> { //level - monitorType
                List<TbBaseReportInfo> wValue = w.getValue();
                WtReportMonitorTypeCountInfo build = WtReportMonitorTypeCountInfo.builder().monitorTypeName(w.getKey())
                        .noData((int) wValue.stream().filter(s -> s.getStatus() == -1).count()).total(wValue.size()).build();
                List<WtReportWarn> wtReportWarns = dealWarnList(wValue);
                build.addWarnCountList(wtReportWarns);
                return build;
            }).toList();
            return builder.monitorTypeCountList(monitorTypeCountList).build();
        }).toList();
    }

    private Map<String, String> queryAreaData(final Collection<Object> areaCodeList) {
        return Optional.of(areaCodeList).filter(CollectionUtil::isNotEmpty)
                .map(u -> redisService.multiGet(RedisKeys.REGION_AREA_KEY, u).stream()
                        .map(w -> {
                            JSONObject jsonObject = JSONUtil.parseObj(w);
                            return Optional.ofNullable(jsonObject.getStr("areaCode")).map(s -> {
                                Map<String, String> map = new HashMap<>();
                                map.put(s, jsonObject.getStr("name"));
                                return map;
                            }).orElse(new HashMap<>());
                        }).reduce((map1, map2) -> {
                            map1.putAll(map2);
                            return map1;
                        }).orElse(new HashMap<>())).orElse(new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Map<String, Object>> querySensorNewData(List<TbBaseReportInfo> infoList) {
        Map<Integer, List<TbBaseReportInfo>> monitorTypeGroupingMap = infoList.stream()
                .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorType));
        List<Map<String, Object>> sensorDataParamList = monitorTypeGroupingMap.entrySet().stream().map(u -> {
            List<TbBaseReportInfo> value = Optional.ofNullable(u.getValue()).orElse(new ArrayList<>());
            List<Integer> sensorIDList = value.stream().map(TbBaseReportInfo::getSensorID).filter(Objects::nonNull).toList();
            List<FieldSelectInfo> fieldTokenList = Optional.of(value).filter(CollectionUtil::isNotEmpty)
                    .map(w -> w.get(0)).map(TbBaseReportInfo::getCustomColumn)
                    .map(w -> Arrays.stream(w.replaceAll("[{}]", "").split(","))
                            .map(s -> s.split(":")[1]).filter(ObjectUtil::isNotEmpty).map(s -> {
                                FieldSelectInfo info = new FieldSelectInfo();
                                info.setFieldToken(s);
                                return info;
                            }).distinct().toList()).orElse(new ArrayList<>());
            return Map.of("monitorType", u.getKey(), "sensorIDList", sensorIDList, "fieldTokenList", fieldTokenList);
        }).toList();
        return sensorDataParamList.stream().filter(u -> CollectionUtil.isNotEmpty((List<Integer>) u.get("sensorIDList")))
                .map(u -> sensorDataDao.querySensorNewData((List<Integer>) u.get("sensorIDList"),
                        (List<FieldSelectInfo>) u.get("fieldTokenList"), false, (Integer) u.get("monitorType")))
                .map(u -> u.stream().collect(Collectors.toMap(
                        map -> (Integer) map.get(DbConstant.SENSOR_ID_FIELD_TOKEN), map -> map))).reduce((map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                }).orElse(new HashMap<>());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<WtReportProjectInfo> dealDataList(final Map<String, List<TbBaseReportInfo>> infoMap,
                                                   final Map<Integer, Map<String, Object>> sensorIDResMap,
                                                   final Map<String, String> areaCodeNameMap) {
        // evaluate the O(n) of this nested loop is equaled to outside base list,
        // if poor performance of this,rewrite here.
        List<WtReportProjectInfo> res = new ArrayList<>();
        infoMap.entrySet().stream().map(u -> {  //level - typeClass
            List<TbBaseReportInfo> uValue = u.getValue();
            Map<String, List<TbBaseReportInfo>> monitorTypeNameInfoMap = uValue.stream().collect(
                    Collectors.groupingBy(TbBaseReportInfo::getMonitorTypeName));
            WtReportProjectInfo info = getReportProjectInfo(u.getKey(), uValue.size(), monitorTypeNameInfoMap);
            monitorTypeNameInfoMap.entrySet().stream().peek(w -> {  //level - monitorType
                List<TbBaseReportInfo> value = w.getValue();
                Map<String, List<TbBaseReportInfo>> collect = value.stream()
                        .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorItemName));
                List<WtReportMonitorItemInfo> monitorItemList = collect.entrySet().stream().map(s -> {  //level - monitorItem
                    List<TbBaseReportInfo> sValue = s.getValue();
                    Map<Integer, List<TbBaseReportInfo>> statusInfoMap = sValue.stream()
                            .collect(Collectors.groupingBy(TbBaseReportInfo::getStatus));
                    WtReportMonitorItemInfo build = WtReportMonitorItemInfo.builder().monitorItemName(s.getKey())
                            .warnCountList(dealWarnList(sValue)).total(sValue.size())
                            .noData(Optional.ofNullable(statusInfoMap.get(SensorStatusDesc.NO_DATA.getStatus()))
                                    .map(List::size).orElse(0)).build();
                    build.setFormList(getFormList(sValue, sensorIDResMap, areaCodeNameMap));
                    return build;
                }).toList();
                info.addMonitorTypeDetail(w.getKey(), monitorItemList);
            }).toList();
            return info;
        }).map(res::add).toList();
        return res;
    }

    private List<WtReportWarn> dealWarnList(final List<TbBaseReportInfo> infoList) {
        List<String> warnNameList = infoList.stream().map(k -> {
            Integer monitorType = k.getMonitorType();
            String upperName = k.getUpperName();
            Map<String, String> customFieldColumnMap = getCustomFieldColumnMap(k.getCustomColumn());
            return customFieldColumnMap.keySet().stream()
                    .map(v -> CompareInterval.getValue(monitorType, v, upperName)).filter(Objects::nonNull)
                    .map(CompareInterval::getDesc).toList();
        }).flatMap(Collection::stream).toList();
        return CollUtil.countMap(warnNameList).entrySet().stream().map(ss -> WtReportWarn.builder().warnName(ss.getKey())
                .total(ss.getValue()).build()).toList();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<WtReportFormDataInfo> getFormList(final List<TbBaseReportInfo> infoList,
                                                   final Map<Integer, Map<String, Object>> sensorIDResMap,
                                                   final Map<String, String> areaCodeNameMap) {
        return infoList.stream().map(n -> {
                    Map<String, String> customFieldColumnMap = getCustomFieldColumnMap(n.getCustomColumn());
                    return Optional.ofNullable(n.getSensorID()).map(sensorIDResMap::get).map(m -> {
                        WtReportFormDataInfo formDataInfo = WtReportFormDataInfo.builder()
                                .monitorPointName(n.getMonitorPointName())
                                .monitorTypeName(n.getMonitorTypeName())
                                .monitorItemName(n.getMonitorItemName())
                                .projectTypeName(n.getProjectTypeName())
                                .projectName(n.getProjectName())
                                .areaName(Optional.ofNullable(n.getAreaCode()).map(areaCodeNameMap::get)
                                        .orElse("--"))
                                .time(DateUtil.parse(m.get(DbConstant.TIME_FIELD).toString(),
                                        "yyyy-MM-dd HH:mm:ss.SSS")).build();
                        m.entrySet().stream().filter(t -> !(DbConstant.TIME_FIELD.equals(t.getKey()) ||
                                        DbConstant.SENSOR_ID_FIELD_TOKEN.equals(t.getKey())))
                                .filter(t -> customFieldColumnMap.containsKey(t.getKey()))
                                .peek(t -> formDataInfo.addFieldDataList(
                                        customFieldColumnMap.get(t.getKey()), t.getValue())).toList();
                        return formDataInfo;
                    }).orElse(null);
                }
        ).filter(Objects::nonNull).toList();
    }

    private Map<String, String> getCustomFieldColumnMap(String customColumn) {
        return Optional.ofNullable(customColumn).map(u -> u.replaceAll("[{}]", "").split(","))
                .map(u -> Arrays.stream(u).map(w -> {
                    Map<String, String> map = new HashMap<>();
                    String[] split = w.split(":");
                    map.put(split[1], split[0]);
                    return map;
                }).reduce((map, map2) -> {
                    map.putAll(map2);
                    return map;
                }).orElse(new HashMap<>())).orElse(new HashMap<>());
    }

    private WtReportProjectInfo getReportProjectInfo(final String monitorTypeName, final Integer total,
                                                     final Map<String, List<TbBaseReportInfo>> monitorTypeNameInfoMap) {
        return WtReportProjectInfo.builder().monitorTypeName(monitorTypeName).total(total)
                .monitorTypeList(monitorTypeNameInfoMap.keySet().stream().toList())
                .monitorTypeCountList(monitorTypeNameInfoMap.entrySet().stream().map(w -> {
                    List<TbBaseReportInfo> wValue = w.getValue();
                    WtReportMonitorTypeCountInfo.WtReportMonitorTypeCountInfoBuilder infoBuilder =
                            WtReportMonitorTypeCountInfo.builder().monitorTypeName(w.getKey()).total(wValue.size());
                    Map<Integer, List<TbBaseReportInfo>> statusInfoMap = wValue.stream()
                            .collect(Collectors.groupingBy(TbBaseReportInfo::getStatus));
                    WtReportMonitorTypeCountInfo build = infoBuilder.noData(Optional.ofNullable(statusInfoMap
                            .get(SensorStatusDesc.NO_DATA.getStatus())).map(List::size).orElse(0)).build();
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL1));
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL2));
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL3));
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL4));
                    return build;
                }).toList()).build();
    }

    private WtReportWarn dealWarnCount(Map<Integer, List<TbBaseReportInfo>> statusInfoMap, SensorStatusDesc desc) {
        return WtReportWarn.builder().total(Optional.ofNullable(statusInfoMap.get(desc.getStatus())).map(List::size)
                .orElse(0)).warnLevel(desc.getWarnLevel()).warnName(desc.getDesc()).build();
    }
}
