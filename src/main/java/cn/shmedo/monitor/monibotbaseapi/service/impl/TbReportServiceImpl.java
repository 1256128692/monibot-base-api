package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbReportMapper;
import cn.shmedo.monitor.monibotbaseapi.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorStatusDesc;
import cn.shmedo.monitor.monibotbaseapi.model.param.report.WtQueryReportParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.WtQueryReportInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbReportService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:09
 * @see SensorDataDao#querySensorNewData(List, List, boolean, Integer)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbReportServiceImpl implements ITbReportService {
    private final RedisService redisService;
    private final TbReportMapper tbReportMapper;
    private final SensorDataDao sensorDataDao;

    @Override
    public WtQueryReportInfo queryReport(WtQueryReportParam param) {
        WtQueryReportInfo.WtQueryReportInfoBuilder builder = WtQueryReportInfo.builder().period(param.getPeriod())
                .companyName(Optional.of(param.getCompanyID()).map(Object::toString)
                        .map(u -> {
                            Collection<Object> collection = new ArrayList<>();
                            collection.add(u);
                            return collection;
                        }).map(u -> redisService.multiGet(RedisKeys.COMPANY_INFO_KEY, u, Company.class))
                        .filter(CollectionUtil::isNotEmpty).map(u -> u.get(0)).map(Company::getShortName).orElse(""))
                .startTime(param.getStartTime()).endTime(param.getEndTime());
        List<TbBaseReportInfo> tbBaseReportInfoList = tbReportMapper.queryBaseReportInfo(param.getCompanyID(),
                param.getStartTime(), param.getEndTime());
        Map<Integer, Map<String, Object>> sensorIDResMap = querySensorNewData(tbBaseReportInfoList);
        Map<String, List<TbBaseReportInfo>> typeClassInfoMap = tbBaseReportInfoList.stream()
                .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorTypeClass));
        builder.total(tbBaseReportInfoList.size()).monitorTypeClassList(typeClassInfoMap.keySet().stream().toList())
                .dataList(dealDataList(typeClassInfoMap, sensorIDResMap));
        if (CollectionUtil.isNotEmpty(param.getProjectIDList())) {
            //TODO deal project
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Map<String, Object>> querySensorNewData(List<TbBaseReportInfo> infoList) {
        Map<Integer, List<TbBaseReportInfo>> collect = infoList.stream()
                .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorType));
        List<Map<String, Object>> list = collect.entrySet().stream().map(u -> {
            List<TbBaseReportInfo> value = Optional.ofNullable(u.getValue()).orElse(new ArrayList<>());
            List<Integer> sensorIDList = value.stream().map(TbBaseReportInfo::getSensorID).toList();
            List<FieldSelectInfo> fieldTokenList = Optional.of(value).filter(CollectionUtil::isNotEmpty)
                    .map(w -> w.get(0)).map(TbBaseReportInfo::getCustomColumn)
                    .map(w -> Arrays.stream(w.replaceAll("[{}]", "").split(","))
                            .map(s -> s.split(":")[1]).map(s -> {
                                FieldSelectInfo info = new FieldSelectInfo();
                                info.setFieldToken(s);
                                return info;
                            }).distinct().toList()).orElse(new ArrayList<>());
            return Map.of("monitorType", u.getKey(), "sensorIDList", sensorIDList, "fieldTokenList", fieldTokenList);
        }).toList();
        return list.stream().map(u -> sensorDataDao.querySensorNewData((List<Integer>) u.get("sensorIDList"),
                        (List<FieldSelectInfo>) u.get("fieldTokenList"), false, (Integer) u.get("monitorType")))
                .map(u -> u.stream().collect(Collectors.toMap(
                        map -> (Integer) map.get(DbConstant.SENSOR_ID_FIELD_TOKEN), map -> map))).reduce((map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                }).orElse(new HashMap<>());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<WtReportProjectInfo> dealDataList(final Map<String, List<TbBaseReportInfo>> infoMap,
                                                   final Map<Integer, Map<String, Object>> sensorIDResMap) {
        // evaluate the O(n) of this nested loop is equaled to outer base list,
        // if poor performance of this,rewrite here.
        List<WtReportProjectInfo> res = new ArrayList<>();
        infoMap.entrySet().stream().map(u -> {
            List<TbBaseReportInfo> uValue = u.getValue();
            Map<String, List<TbBaseReportInfo>> monitorTypeNameInfoMap = uValue.stream().collect(
                    Collectors.groupingBy(TbBaseReportInfo::getMonitorTypeName));
            WtReportProjectInfo info = WtReportProjectInfo.builder().monitorTypeClass(u.getKey()).total(uValue.size())
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
            //TODO monitorTypeDetailList

            // 同一个类型(环境、安全...)下的
            monitorTypeNameInfoMap.entrySet().stream().peek(w -> {
                List<TbBaseReportInfo> value = w.getValue();


                Map<String, List<TbBaseReportInfo>> collect = value.stream()
                        .collect(Collectors.groupingBy(TbBaseReportInfo::getMonitorItemName));
                collect.entrySet().stream().map(s -> {
                    List<TbBaseReportInfo> sValue = s.getValue();
                    Map<Integer, List<TbBaseReportInfo>> statusInfoMap = sValue.stream()
                            .collect(Collectors.groupingBy(TbBaseReportInfo::getStatus));
                    WtReportMonitorItemInfo build = WtReportMonitorItemInfo.builder().monitorItemName(s.getKey())
                            .total(sValue.size()).noData(Optional.ofNullable(statusInfoMap
                                    .get(SensorStatusDesc.NO_DATA.getStatus())).map(List::size).orElse(0)).build();
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL1));
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL2));
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL3));
                    build.addWarnCount(dealWarnCount(statusInfoMap, SensorStatusDesc.WARM_LEVEL4));

                    //TODO add formList


                    return build;   //current Level: monitorItemList
                }).toList();


//              for: info.addMonitorTypeDetail(w.getKey(), );
            }).toList();


            return info;
        }).map(res::add).toList();
        return res;
    }

    private WtReportWarn dealWarnCount(Map<Integer, List<TbBaseReportInfo>> statusInfoMap, SensorStatusDesc desc) {
        return WtReportWarn.builder().total(Optional.ofNullable(statusInfoMap.get(desc.getStatus())).map(List::size)
                .orElse(0)).warnLevel(desc.getWarnLevel()).warnName(desc.getDesc()).build();
    }
}
