package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ThematicPlainMonitorItemEnum;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryDmDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryStDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ConfigBaseResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.DmThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.StThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicMonitorPointPlainInfo;
import cn.shmedo.monitor.monibotbaseapi.service.IThematicDataAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:59
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThematicDataAnalysisServiceImpl implements IThematicDataAnalysisService {
    private final SensorDataDao sensorDataDao;
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    @Override
    public StThematicAnalysisInfo queryStGroupRealData(QueryStDataParam param) {
        return null;
    }

    //all the {@code segmentValue} of the first record in records would be set to zero
    @Override
    public DmThematicAnalysisInfo queryDmAnalysisData(QueryDmDataParam param) {
        Integer monitorPointID = param.getMonitorPointID();
        TbMonitorPoint tbMonitorPoint = param.getTbMonitorPoint();
        Integer monitorType = tbMonitorPoint.getMonitorType();
        List<FieldSelectInfo> fieldSelectInfoList = param.getFieldSelectInfoList();
        DmThematicAnalysisInfo res = DmThematicAnalysisInfo.builder().monitorPointID(monitorPointID).monitorPointName(
                tbMonitorPoint.getName()).build();
        Map<Integer, ConfigBaseResponse> sIDConfigMap = tbProjectInfoMapper.selectMonitorPointRelateSensorConfig(
                monitorPointID, monitorType, param.getGroup(), param.getKey()).stream().collect(Collectors.toMap(
                ConfigBaseResponse::getTargetID, t -> t));
        if (CollectionUtil.isEmpty(sIDConfigMap)) {
            res.setNewData(new Date(), new ArrayList<>());
            res.setHistoryData(new ArrayList<>());
        } else {
            dealDmNewData(res, param, monitorType, sIDConfigMap, fieldSelectInfoList);
            dealDmHistoryData(res, param, monitorType, sIDConfigMap, fieldSelectInfoList, param.getDensity());
        }
        return res;
    }

    /**
     * @see ThematicMonitorPointPlainInfo
     */
    @Override
    public List<ThematicMonitorPointInfo> queryThematicMonitorPointByProjectID(Integer projectID) {
        return tbMonitorPointMapper.selectPointWithItemBaseInfo(
                        List.of(projectID), ThematicPlainMonitorItemEnum.getThematicMonitorTypeIDs()).stream()
                .collect(Collectors.groupingBy(
                        MonitorPointWithItemBaseInfo::getMonitorType)).entrySet().stream().map(u -> {
                    Integer monitorType = u.getKey();
                    return u.getValue().stream().map(w -> {
                        if (ThematicPlainMonitorItemEnum.DISTANCE.getMonitorType().equals(monitorType)) {
                            ThematicPlainMonitorItemEnum pointEnum = (w.getMonitorItemName().contains("浸润线")
                                    || w.getMonitorItemAlias().contains("浸润线")) ? ThematicPlainMonitorItemEnum.WETTING_LINE
                                    : ThematicPlainMonitorItemEnum.DISTANCE;
                            return ThematicMonitorPointPlainInfo.builder().thematicType(pointEnum.getThematicType())
                                    .monitorItemEnum(pointEnum).monitorPointID(w.getID()).monitorPointName(w.getName())
                                    .monitorType(w.getMonitorType()).build();
                        }
                        ThematicPlainMonitorItemEnum pointEnum = ThematicPlainMonitorItemEnum.getByMonitorType(monitorType);
                        return ThematicMonitorPointPlainInfo.builder().thematicType(pointEnum.getThematicType()).monitorItemEnum(
                                pointEnum).monitorPointID(w.getID()).monitorPointName(w.getName())
                                .monitorType(w.getMonitorType()).build();
                    }).toList();
                }).flatMap(Collection::stream).toList().stream()
                .collect(Collectors.groupingBy(ThematicMonitorPointPlainInfo::getThematicType)).entrySet().stream()
                .map(u -> ThematicMonitorPointInfo.builder().thematicType(u.getKey()).thematicDataList(u.getValue().stream()
                        .collect(Collectors.groupingBy(ThematicMonitorPointPlainInfo::getMonitorItemEnum)).entrySet()
                        .stream().map(w -> Map.of("monitorItemDesc", w.getKey().getDesc(), "dataList", w.getValue()
                                .stream().map(s -> Map.of("monitorPointID", s.getMonitorPointID(), "monitorPointName",
                                        s.getMonitorPointName(), "monitorType", s.getMonitorType())).toList()))
                        .toList()).build()).toList();
    }

    private void dealDmNewData(final DmThematicAnalysisInfo res, final QueryDmDataParam param,
                               final Integer monitorType, final Map<Integer, ConfigBaseResponse> configMap,
                               final List<FieldSelectInfo> fieldSelectInfoList) {
        List<Map<String, Object>> maps = sensorDataDao.querySensorNewData(configMap.keySet().stream().toList(), fieldSelectInfoList, false, monitorType);
        //TODO
    }

    private void dealDmHistoryData(final DmThematicAnalysisInfo res, final QueryDmDataParam param,
                                   final Integer monitorType, final Map<Integer, ConfigBaseResponse> configMap,
                                   final List<FieldSelectInfo> fieldSelectInfoList, final Integer density) {
        String fieldToken = param.getFieldToken();
        Timestamp startTime = param.getStartTime();
        Timestamp endTime = param.getEndTime();
        List<Integer> sensorIDList = configMap.keySet().stream().toList();
        if (Objects.nonNull(density)) {
            switch (density) {
                case 0 -> {
                    //TODO 全部
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, null, fieldSelectInfoList, false, monitorType);
                }
                case 1 -> {
                    //TODO 日平均
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, "1d", fieldSelectInfoList, false, monitorType);
                    System.out.println(111);
                }
                case 2 -> {
                    //TODO 月平均
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, "1d", fieldSelectInfoList, false, monitorType);
                    System.out.println(222);
                }
                case 3 -> {
                    //TODO 年平均
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, "1d", fieldSelectInfoList, false, monitorType);
                }
                default -> {
                    log.error("Unsupported density,density: {}", density);
                    res.setHistoryData(new ArrayList<>());
                }
            }
        } else {
            res.setHistoryData(new ArrayList<>());
        }
    }

    /**
     * @param groupFunc acquire the {@code time} which is grouping key according metadata time
     * @return [{"time":${DateTime},"dataList":[{${DmAnalysisData}}]}]
     */
    private List<Map<String, Object>> getDmHistoryData(final List<Map<String, Object>> metadataList,
                                                       final String fieldToken, final Function<Date, Date> groupFunc) {
        // Date time = DateUtil.parse(map.get("time").toString(), "yyyy-MM-dd HH:mm:ss.SSS");
        //TODO grouping by date
        //TODO map keySet for,sort to time list


        return metadataList.stream().collect(Collectors.groupingBy(u -> groupFunc.apply((Date) u.get(DbConstant.TIME_FIELD)))).entrySet().stream().map(u -> {
            List<Map<String, Object>> value = u.getValue();


            Double sum = value.stream().map(w -> w.get(fieldToken).toString()).map(Double::parseDouble).reduce(Double::sum)
                    .orElse(0d);


            return Map.of("time", u.getKey(), "dataList", List.of(1));
        }).toList();
    }

}
