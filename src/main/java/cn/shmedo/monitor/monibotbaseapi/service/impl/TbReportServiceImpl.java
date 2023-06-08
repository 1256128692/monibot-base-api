package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbReportMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Company;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CompareInterval;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorStatusDesc;
import cn.shmedo.monitor.monibotbaseapi.model.param.report.WtQueryReportParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbReportService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.sensor.SensorWarnUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private final TbReportMapper tbReportMapper;
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final SensorDataDao sensorDataDao;

    @SuppressWarnings("all")
    @Resource(name = RedisConstant.IOT_REDIS_SERVICE)
    private final RedisService iotRedisService;

    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private final RedisService monitorRedisService;
    private final Map<String, Integer> dataListOrderMap = Map.of("环境监测", 1, "安全监测", 2, "视频监测", 3);

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
                        }).map(u -> iotRedisService.multiGet(RedisKeys.IOT_COMPANY_INFO_KEY, u, Company.class))
                        .filter(CollectionUtil::isNotEmpty).map(u -> u.get(0)).map(Company::getShortName)
                        .orElse("余姚市水务局")).startTime(startTime).endTime(endTime);
        List<TbBaseReportInfo> tbBaseReportInfoList = tbReportMapper.queryBaseReportInfo(param.getCompanyID(),
                startTime, endTime);
        Map<Integer, Map<String, Object>> sensorIDResMap = querySensorNewData(tbBaseReportInfoList);
        Collection<Object> areaCodeList = tbBaseReportInfoList.stream().map(TbBaseReportInfo::getAreaCode).distinct()
                .map(u -> (Object) u).toList();
        List<TbBaseReportInfo> reduceTbBaseReportInfoList = reduceSensorToPoint(tbBaseReportInfoList, sensorIDResMap);
        Map<String, List<TbBaseReportInfo>> monitorClassInfoMap = reduceTbBaseReportInfoList.stream()
                .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorTypeClass));
        builder.total(reduceTbBaseReportInfoList.size()).monitorClassList(monitorClassInfoMap.keySet().stream()
                        .sorted((o1, o2) -> dataListOrderMap.get(o1) - dataListOrderMap.get(o2)).toList())
                .dataList(dealDataList(monitorClassInfoMap, sensorIDResMap, queryAreaData(areaCodeList)));
        if (CollectionUtil.isNotEmpty(projectIDList)) {
            builder.projectDataList(dealProjectData(projectIDList, sensorIDResMap, startTime, endTime));
        }
        return builder.build();
    }

    private List<WtReportProjectInfo> dealProjectData(List<Integer> projectIDList,
                                                      final Map<Integer, Map<String, Object>> sensorIDResMap,
                                                      Date startTime, Date endTime) {
        List<WtReportProjectInfo> infoList = new ArrayList<>(Optional.of(tbReportMapper.queryProjectReportInfo(
                        projectIDList, startTime, endTime))
                .map(Collection::stream).map(u -> u.collect(Collectors.groupingBy(TbBaseReportInfo::getProjectName)))
                .orElse(new HashMap<>()).values().stream().map(u -> {    // level - project
                    if (u.stream().anyMatch(w -> Objects.isNull(w.getMonitorTypeName()))) {
                        return WtReportProjectInfo.builder().total(0).projectName(u.get(0).getProjectName())
                                .monitorTypeList(new ArrayList<>()).monitorTypeCountList(new ArrayList<>()).build();
                    }
                    List<TbBaseReportInfo> reduceU = Optional.of(u).map(w -> reduceSensorToPoint(w, sensorIDResMap))
                            .orElse(new ArrayList<>());
                    Map<String, List<TbBaseReportInfo>> monitorTypeMap = reduceU.stream().collect(Collectors.groupingBy(
                            TbBaseReportInfo::getMonitorTypeName));
                    WtReportProjectInfo.WtReportProjectInfoBuilder builder = WtReportProjectInfo.builder()
                            .total(reduceU.size()).projectName(reduceU.get(0).getProjectName())
                            .monitorTypeList(monitorTypeMap.keySet().stream().toList());
                    return builder.monitorTypeCountList(monitorTypeMap.entrySet().stream().map(w -> { //level - monitorType
                        List<TbBaseReportInfo> wValue = w.getValue();
                        WtReportMonitorTypeCountInfo build = WtReportMonitorTypeCountInfo.builder()
                                .monitorTypeName(w.getKey()).noData((int) wValue.stream()
                                        .filter(s -> s.getStatus() == -1).count()).total(wValue.size()).build();
                        Map<Integer, List<TbBaseReportInfo>> statusInfoMap = wValue.stream()
                                .collect(Collectors.groupingBy(TbBaseReportInfo::getStatus));
                        List<WtReportWarn> warnList = List.of(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL1),
                                dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL2),
                                dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL3),
                                dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL4));
                        Map<Integer, Integer> levelCountMap = CollUtil.countMap(warnList.stream()
                                .map(WtReportWarn::getWarnLevel).toList());
                        List<WtReportWarn> addtionWarnList = Arrays.stream(SensorStatusDesc.values())
                                .filter(s -> !(s.equals(SensorStatusDesc.NO_DATA) || s.equals(SensorStatusDesc.NORMAL)))
                                .map(SensorStatusDesc::getWarnLevel).filter(s -> !levelCountMap.containsKey(s))
                                .map(s -> WtReportWarn.builder().total(0).warnLevel(s).warnName(
                                        SensorStatusDesc.getByWarnLevel(s).getDesc()).build()).toList();
                        build.addWarnCountList(warnList);
                        build.addWarnCountList(addtionWarnList);
                        return build;
                    }).toList()).build();
                }).toList());
        List<WtReportProjectInfo> addtionList = tbProjectInfoMapper.selectList(new LambdaQueryWrapper<TbProjectInfo>()
                        .in(TbProjectInfo::getID, projectIDList)).stream().map(TbProjectInfo::getProjectName)
                .filter(u -> infoList.stream().noneMatch(v -> u.equalsIgnoreCase(v.getProjectName())))
                .map(u -> WtReportProjectInfo.builder().total(0).projectName(u).monitorClass(null)
                        .monitorTypeList(new ArrayList<>()).monitorTypeCountList(new ArrayList<>()).build()).toList();
        infoList.addAll(addtionList);
        return infoList;
    }

    private Map<String, String> queryAreaData(final Collection<Object> areaCodeList) {
        return Optional.of(areaCodeList).filter(CollectionUtil::isNotEmpty)
                .map(u -> monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY, u).stream()
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
                    .flatMap(w -> w.stream().map(TbBaseReportInfo::getCustomColumn).filter(Objects::nonNull)
                            .reduce((s1, s2) -> s1 + "," + s2))
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
    private List<WtReportProjectInfo> dealDataList(final Map<String, List<TbBaseReportInfo>> monitorClassInfoMap,
                                                   final Map<Integer, Map<String, Object>> sensorIDResMap,
                                                   final Map<String, String> areaCodeNameMap) {
        // evaluate the O(n) of this nested loop wouldn't exceed too more to outside base list,
        // if poor performance of this,rewrite here.
        List<WtReportProjectInfo> res = new ArrayList<>();
        monitorClassInfoMap.entrySet().stream().map(u -> {  //level - typeClass
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
        return res.stream().sorted((o1, o2) -> dataListOrderMap.get(o1.getMonitorClass())
                - dataListOrderMap.get(o2.getMonitorClass())).toList();
    }

    private List<WtReportWarn> dealWarnList(final List<TbBaseReportInfo> infoList) {
        List<String> warnDescList = infoList.stream().map(k -> {
            Integer monitorType = k.getMonitorType();
            String upperName = k.getUpperName();
            return getCustomFieldColumnTupleList(k.getCustomColumn()).stream().map(Tuple::getItem1)
                    .map(v -> CompareInterval.getValue(monitorType, v, upperName)).filter(Objects::nonNull)
                    .map(CompareInterval::getDesc).toList();
        }).flatMap(Collection::stream).toList();
        List<WtReportWarn> nonWarnList = infoList.stream().map(u -> Optional.ofNullable(u.getFieldTokenUpperNames())
                .map(this::getCustomFieldColumnTupleList)
                .map(w -> w.stream().map(s -> CompareInterval.getValue(u.getMonitorType(), s.getItem2(), s.getItem1()))
                        .filter(Objects::nonNull).map(CompareInterval::getDesc).toList()).orElse(new ArrayList<>())
        ).flatMap(Collection::stream).distinct().map(u -> WtReportWarn.builder().warnName(u).total(0).build()).toList();
        List<WtReportWarn> resList = new ArrayList<>(CollUtil.countMap(warnDescList).entrySet().stream().map(u ->
                WtReportWarn.builder().warnName(u.getKey()).total(u.getValue()).build()).toList());
        resList.addAll(nonWarnList);
        return resList;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<WtReportFormDataInfo> getFormList(final List<TbBaseReportInfo> infoList,
                                                   final Map<Integer, Map<String, Object>> sensorIDResMap,
                                                   final Map<String, String> areaCodeNameMap) {
        return infoList.stream().map(n -> {
            Map<String, Object> influxData = Optional.ofNullable(n.getSensorID()).map(sensorIDResMap::get)
                    .orElse(new HashMap<>());
            WtReportFormDataInfo formDataInfo = WtReportFormDataInfo.builder().projectName(n.getProjectName())
                    .monitorPointName(n.getMonitorPointName()).monitorTypeName(n.getMonitorTypeName())
                    .monitorItemName(n.getMonitorItemName()).projectTypeName(n.getProjectTypeName())
                    .areaName(Optional.ofNullable(n.getAreaCode()).map(areaCodeNameMap::get).orElse("-"))
                    .time(getInfluxDataTime(influxData)).build();
            getCustomFieldColumnTupleList(n.getCustomColumn()).stream()
                    .filter(s -> !("video".equalsIgnoreCase(s.getItem1()) || "image".equalsIgnoreCase(s.getItem1())))
                    .peek(s -> formDataInfo.addFieldDataList(
                            s.getItem2(), Optional.ofNullable(influxData.get(s.getItem1())).orElse("-"))).toList();
            return formDataInfo;
        }).toList();
    }

    private List<Tuple<String, String>> getCustomFieldColumnTupleList(String customColumn) {
        return Optional.ofNullable(customColumn).map(u -> u.replaceAll("[{}]", "").split(","))
                .map(u -> Arrays.stream(u).map(w -> {
                    String[] split = w.split(":");
                    return new Tuple<>(split[1], split[0]);
                }).toList()).orElse(new ArrayList<>());
    }

    private WtReportProjectInfo getReportProjectInfo(final String monitorClass, final Integer total,
                                                     final Map<String, List<TbBaseReportInfo>> monitorTypeNameInfoMap) {
        return WtReportProjectInfo.builder().monitorClass(monitorClass).total(total)
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

    private Date getInfluxDataTime(Map<String, Object> influxData) {
        return Optional.ofNullable(influxData).map(u -> u.get(DbConstant.TIME_FIELD))
                .map(m -> DateUtil.parse(m.toString(), "yyyy-MM-dd HH:mm:ss.SSS")).orElse(null);
    }

    /**
     * 一个监测点会绑定多个传感器，这个时候要把多个传感器的数据全部聚合成对应监测点的数据
     * 选取条件排序 存在报警、上行数据时间最新、报警等级最低
     * <p>
     * example:
     * A监测点存在a1,a2,a3,a4四个传感器：
     * 其中a1在 2023/05/11 11:10:15.000 上行了二级报警；
     * a2在 2023/05/11 11:10:15.000 上行了三级报警；
     * a3在 2023/05/11 11:10:00.000 上行了一级报警；
     * a4在 2023/05/11 11:15:00.000 上行了正常
     * 那么此时a1传感器的报警数据将被当成A监测点的报警数据被统计，因为它存在报警、上行时间最新、报警等级最低
     * </p>
     */
    private List<TbBaseReportInfo> reduceSensorToPoint(final List<TbBaseReportInfo> list,
                                                       final Map<Integer, Map<String, Object>> sensorIDResMap) {
        return list.stream().collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorPointID)).values().stream()
                .map(u -> u.stream().reduce((info1, info2) -> {
                    // if only one sensor is warning,return the warning one; or return the latest one
                    Integer info1Status = info1.getStatus();
                    Integer info2Status = info2.getStatus();
                    boolean info1IsWarn = SensorWarnUtils.sensorIsWarn(info1Status);
                    if (info1IsWarn ^ SensorWarnUtils.sensorIsWarn(info2Status)) {
                        return info1IsWarn ? info1 : info2;
                    }
                    Date info1Time = getInfluxDataTime(sensorIDResMap.get(info1.getSensorID()));
                    Date info2Time = getInfluxDataTime(sensorIDResMap.get(info2.getSensorID()));
                    boolean info1TimeIsNull = Objects.isNull(info1Time);
                    boolean info2TimeIsNull = Objects.isNull(info2Time);
                    if (info1TimeIsNull ^ info2TimeIsNull) {
                        return info1TimeIsNull ? info2 : info1;
                    }
                    // if they waring at the same time, return the lower level one
                    if (info1TimeIsNull || DateUtil.isSameTime(info1Time, info2Time)) {
                        return info1Status <= info2Status ? info1 : info2;
                    }
                    return info1Time.after(info2Time) ? info1 : info2;
                }).orElse(null)).filter(Objects::nonNull).toList();
    }
}
