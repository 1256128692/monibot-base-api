package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataEvent;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValueRelation;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.FrequencyEnum;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.StatisticalMethods;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.DeleteBatchDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.UpdateDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.DeleteBatchEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.QueryEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.UpdateEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.QueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.QueryMonitorPointHasDataCountParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeConfigurationParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent.QueryDataEventInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue.EigenValueInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeConfigV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorPointBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorPointBaseInfoV2;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EigenBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EventBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.MonitorPointDataInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorDataService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MonitorDataServiceImpl implements MonitorDataService {

    private final TbEigenValueMapper tbEigenValueMapper;

    private final TbEigenValueRelationMapper tbEigenValueRelationMapper;

    private final TbDataEventMapper tbDataEventMapper;

    private final TbDataEventRelationMapper tbDataEventRelationMapper;

    private final TbMonitorTypeMapper tbMonitorTypeMapper;

    private final TbMonitorPointMapper tbMonitorPointMapper;

    private final TbSensorMapper tbSensorMapper;

    private final TbMonitorItemFieldMapper tbMonitorItemFieldMapper;

    private final TbMonitorItemMapper tbMonitorItemMapper;

    private SensorDataDao sensorDataDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEigenValue(AddEigenValueParam pa) {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbEigenValue tbEigenValue = AddEigenValueParam.toNewVo(pa, subjectID);
        tbEigenValueMapper.insertSelective(tbEigenValue);
        tbEigenValueRelationMapper.insertBatch(pa.getMonitorPointIDList(), tbEigenValue.getId());
    }

    @Override
    @Transactional
    public Object queryEigenValueList(QueryEigenValueParam pa) {

        List<EigenValueInfoV1> eigenValueInfoV1List = tbEigenValueMapper.selectListByCondition(pa.getMonitorItemID(), pa.getProjectID(), pa.getMonitorPointIDList());

        if (CollectionUtil.isNullOrEmpty(eigenValueInfoV1List)) {
            return Collections.emptyList();
        }
        List<Integer> eigenValueIDList = eigenValueInfoV1List.stream().map(EigenValueInfoV1::getId).collect(Collectors.toList());
        List<MonitorPointBaseInfoV2> allMonitorPointList = tbMonitorPointMapper.selectListByEigenValueIDList(eigenValueIDList);

        eigenValueInfoV1List.forEach(item -> {
            item.setScopeStr(ScopeType.getDescriptionByCode(item.getScope()));
            if (!CollectionUtil.isNullOrEmpty(allMonitorPointList)) {
                item.setMonitorPointList(allMonitorPointList.stream().filter(a -> a.getMonitorItemID().equals(item.getMonitorItemID())).collect(Collectors.toList()));
            }
        });

        return eigenValueInfoV1List;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEigenValue(UpdateEigenValueParam pa) {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbEigenValue tbEigenValue = UpdateEigenValueParam.toNewVo(pa, subjectID);
        tbEigenValueMapper.updateByPrimaryKeySelective(tbEigenValue);

        // 删除之前关系,重新绑定
        tbEigenValueRelationMapper.deleteByEigenValueIDList(List.of(pa.getEigenValueID()));
        tbEigenValueRelationMapper.insertBatch(pa.getMonitorPointIDList(), pa.getEigenValueID());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchEigenValue(DeleteBatchEigenValueParam pa) {

        tbEigenValueMapper.deleteByEigenValueIDList(pa.getEigenValueIDList());
        tbEigenValueRelationMapper.deleteByEigenValueIDList(pa.getEigenValueIDList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDataEvent(AddDataEventParam pa) {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbDataEvent record = AddDataEventParam.toNewVo(pa, subjectID);
        tbDataEventMapper.insertSelective(record);
        tbDataEventRelationMapper.insertBatch(pa.getMonitorItemIDList(), record.getId());
    }

    @Override
    public Object queryDataEventList(QueryDataEventParam pa) {

        List<QueryDataEventInfo> queryDataEventInfos = tbDataEventMapper.selectListByProjectIDAndItemIDs(
                pa.getProjectID(), pa.getMonitorItemIDList());
        if (CollectionUtil.isNullOrEmpty(queryDataEventInfos)) {
            return Collections.emptyList();
        }
        List<MonitorItemBaseInfo> allMonitorItemList = tbMonitorItemMapper.selectListByEventIDList(queryDataEventInfos.stream()
                .map(QueryDataEventInfo::getId).collect(Collectors.toList()));

        queryDataEventInfos.forEach(item -> {
            item.setFrequencyStr(FrequencyEnum.getDescriptionFromValue(item.getFrequency()));
            if (!CollectionUtil.isNullOrEmpty(allMonitorItemList)) {
                item.setMonitorItemList(allMonitorItemList.stream().filter(a -> a.getEventID().equals(item.getId())).collect(Collectors.toList()));
            }
        });

        return queryDataEventInfos;
    }

    @Override
    public void updateDataEvent(UpdateDataEventParam pa) {

        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbDataEvent tbDataEvent = UpdateDataEventParam.toNewVo(pa, subjectID);
        tbDataEventMapper.updateByPrimaryKeySelective(tbDataEvent);

        // 删除之前关系,重新绑定
        tbDataEventRelationMapper.deleteByEventIDList(List.of(pa.getId()));
        tbDataEventRelationMapper.insertBatch(pa.getMonitorItemIDList(), pa.getId());
    }

    @Override
    public void deleteBatchDataEvent(DeleteBatchDataEventParam pa) {

        tbDataEventMapper.deleteByEventIDList(pa.getEventIDList());
        tbDataEventRelationMapper.deleteByEventIDList(pa.getEventIDList());
    }

    @Override
    public Object queryMonitorTypeConfiguration(QueryMonitorTypeConfigurationParam pa) {

        List<MonitorTypeBaseInfoV1> list = null;

        if (pa.getMonitorType() != null) {
            list = tbMonitorTypeMapper.selectByMonitorTypeList(List.of(pa.getMonitorType()));
        } else {
            list = tbMonitorTypeMapper.selectAllMonitorTypeBaseInfoV1();
        }


        if (CollectionUtil.isNullOrEmpty(list)) {
            return Collections.emptyList();
        }
        list.forEach(i -> {
            if (StringUtils.isNotBlank(i.getExValues())) {
                MonitorTypeConfigV1 monitorTypeConfigV1 = JSONUtil.toBean(i.getExValues(), MonitorTypeConfigV1.class);
                if (ObjectUtil.isNotNull(monitorTypeConfigV1)) {
                    i.setDisplayDensity(monitorTypeConfigV1.getDisplayDensity());
                    i.setStatisticalMethods(monitorTypeConfigV1.getStatisticalMethods());
                }
            }
        });

        return list;
    }

    @Override
    public List<MonitorPointDataInfo> queryMonitorPointDataList(QueryMonitorPointDataParam pa) {

        // 点信息列表
        List<MonitorPointDataInfo> monitorPointDataInfoList = tbMonitorPointMapper.selectMonitorPointDataInfoListByIDList(pa.getMonitorPointIDList());

        if (CollectionUtil.isNullOrEmpty(monitorPointDataInfoList)) {
            return Collections.emptyList();
        }

        // 全部传感器信息
        List<SensorBaseInfoResponse> allSensorInfoList = tbSensorMapper.selectListBymonitorPointIDList(pa.getMonitorPointIDList());

        // 监测项目与监测子字段类型关系表
        List<FieldBaseInfo> fieldList = tbMonitorItemFieldMapper.selectListByMonitorItemID(pa.getMonitorItemID());

        if (!CollectionUtil.isNullOrEmpty(allSensorInfoList)) {
            List<Integer> sensorIDList = allSensorInfoList.stream().map(SensorBaseInfoResponse::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> sensorDataList = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(),
                    pa.getDensityType(), pa.getStatisticsType(), fieldList, pa.getMonitorType());

            allSensorInfoList.forEach(sensorInfo -> {
                if (!CollectionUtil.isNullOrEmpty(sensorDataList)) {
                    List<Map<String, Object>> result = new LinkedList<>();
                    sensorDataList.forEach(da -> {
                        if (sensorInfo.getSensorID().equals((Integer) da.get(DbConstant.SENSOR_ID_FIELD_TOKEN))) {
                            if (da.get(fieldList.get(0).getFieldToken()) != null) {
                                result.add(da);
                            }
                        }
                    });
                    if (sensorInfo.getMonitorType().equals(MonitorType.SOIL_MOISTURE.getKey())) {
                        result.forEach(r -> {
                            SensorBaseInfoResponse sInfo = allSensorInfoList.stream()
                                    .filter(s -> s.getSensorID().equals((Integer) r.get(DbConstant.SENSOR_ID_FIELD_TOKEN)))
                                    .findFirst().orElse(null);
                            if (sInfo != null && StringUtils.isNotBlank(sInfo.getConfigFieldValue())) {
                                r.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(sInfo.getConfigFieldValue()).getByPath("$.埋深"));
                            }
                        });
                    }
                    sensorInfo.setMultiSensorData(result);
                }
            });

            monitorPointDataInfoList.forEach(m -> {
                m.setSensorList(allSensorInfoList.stream().filter(s -> s.getMonitorPointID().equals(m.getMonitorPointID())).collect(Collectors.toList()));
                m.setFieldList(fieldList);
            });
        }

        // 特征值列表
        if (!CollectionUtil.isNullOrEmpty(pa.getEigenValueIDList())) {
            List<EigenBaseInfo> eigenValueList = tbEigenValueMapper.selectByIDs(pa.getEigenValueIDList());
            List<TbEigenValueRelation> eigenValueRelations = tbEigenValueRelationMapper.selectByIDs(pa.getEigenValueIDList());

            if (!CollectionUtil.isNullOrEmpty(eigenValueRelations) && !CollectionUtil.isNullOrEmpty(eigenValueList)) {
                monitorPointDataInfoList.forEach(m -> {
                    List<Integer> eigenValueIDList = eigenValueRelations.stream().filter(e -> e.getMonitorPointID().equals(m.getMonitorPointID()))
                            .map(TbEigenValueRelation::getEigenValueID).collect(Collectors.toList());
                    if (!CollectionUtil.isNullOrEmpty(eigenValueIDList)) {
                        m.setEigenValueList(eigenValueList.stream().filter(ev -> eigenValueIDList.contains(ev.getId())).collect(Collectors.toList()));
                    }
                });
            }
        }

        // 大事记列表
        if (!CollectionUtil.isNullOrEmpty(pa.getEventIDList())) {
            List<EventBaseInfo> eventBaseInfoList = tbDataEventMapper.selectByIDs(pa.getEventIDList());
            if (!CollectionUtil.isNullOrEmpty(eventBaseInfoList)) {
                monitorPointDataInfoList.forEach(m -> {
                    m.setEventList(eventBaseInfoList);
                });
            }
        }

        return monitorPointDataInfoList;
    }

    @Override
    public Object queryMonitorPointHasDataCount(QueryMonitorPointHasDataCountParam pa) {

        // 全部传感器信息
        List<SensorBaseInfoResponse> allSensorInfoList = tbSensorMapper.selectListBymonitorPointIDList(pa.getMonitorPointIDList());
        if (CollectionUtil.isNullOrEmpty(allSensorInfoList)) {
            return Collections.emptyList();
        }
        // 监测项目与监测子字段类型关系表
        List<FieldBaseInfo> fieldList = tbMonitorItemFieldMapper.selectListByMonitorItemID(pa.getTbMonitorPoints().get(0).getMonitorItemID());

        List<Integer> sensorIDList = allSensorInfoList.stream().map(SensorBaseInfoResponse::getSensorID).collect(Collectors.toList());
        List<Map<String, Object>> maps = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(),
                pa.getDensity(), StatisticalMethods.LATEST.getValue(), fieldList, pa.getTbMonitorPoints().get(0).getMonitorType());
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat outputDateFormat;

        switch (pa.getDensity()) {
            case 3:
                outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 5:
                outputDateFormat = new SimpleDateFormat("yyyy-MM");
                break;
            case 6:
                outputDateFormat = new SimpleDateFormat("yyyy");
                break;
            default:
                throw new IllegalArgumentException("Invalid density value");
        }

        return maps.stream()
                .map(map -> {
                    Date date = null;
                    try {
                        date = inputDateFormat.parse((String) map.get("time"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    return outputDateFormat.format(date);
                })
                .distinct()
                .collect(Collectors.toList());
    }
}
