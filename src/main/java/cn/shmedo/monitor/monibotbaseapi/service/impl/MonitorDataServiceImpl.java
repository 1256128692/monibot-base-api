package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.DeleteBatchDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.UpdateDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeConfigurationParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent.QueryDataEventInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent.TimeRange;
import cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue.EigenValueInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeConfigV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorPointBaseInfoV2;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EigenBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EventBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.MonitorPointDataInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorDataService;
import cn.shmedo.monitor.monibotbaseapi.util.InfluxDBDataUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

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

        List<EigenValueInfoV1> eigenValueInfoV1List = tbEigenValueMapper.selectListByCondition(pa.getMonitorItemID(),
                pa.getProjectID(), pa.getMonitorPointIDList(), pa.getScope());

        if (CollectionUtil.isNullOrEmpty(eigenValueInfoV1List)) {
            return Collections.emptyList();
        }
        List<Integer> eigenValueIDList = eigenValueInfoV1List.stream().map(EigenValueInfoV1::getId).collect(Collectors.toList());
        List<MonitorPointBaseInfoV2> allMonitorPointList = tbMonitorPointMapper.selectListByEigenValueIDList(eigenValueIDList);

        eigenValueInfoV1List.forEach(item -> {
            item.setScopeStr(ScopeType.getDescriptionByCode(item.getScope()));
            if (!CollectionUtil.isNullOrEmpty(allMonitorPointList)) {
                item.setMonitorPointList(allMonitorPointList.stream().filter(a -> a.getEigenValueID().equals(item.getId())).collect(Collectors.toList()));
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

        if (pa.getBegin() != null && pa.getEnd() != null) {
            return queryDataEventInfos.stream()
                    .filter(eventInfo -> isTimeInRange(eventInfo.getTimeRange(), pa.getBegin(), pa.getEnd(), eventInfo.getFrequency()))
                    .collect(Collectors.toList());
        }

        return queryDataEventInfos;
    }


    private boolean isTimeInRange(String timeRange, Date begin, Date end, Integer frequency) {
        List<TimeRange> dateRanges = parseJsonTimeRange(timeRange);
        return CollUtil.isNotEmpty(dateRanges) && dateRanges.stream().anyMatch(dateRange -> {
            if (frequency == 1) {
                // 处理每年的情况
                int beginYear = DateUtil.year(begin);
                int endYear = DateUtil.year(end);

                Date startTime = adjustToSameYear(dateRange.getStartTime(), beginYear);
                Date endTime = adjustToSameYear(dateRange.getEndTime(), endYear);

                return DateUtil.isIn(begin, startTime, endTime) || DateUtil.isIn(end, startTime, endTime)
                        || DateUtil.isIn(startTime, begin, end) || DateUtil.isIn(endTime, begin, end);
            } else {
                // 非每年的情况
                return DateUtil.isIn(begin, dateRange.getStartTime(), dateRange.getEndTime()) ||
                        DateUtil.isIn(end, dateRange.getStartTime(), dateRange.getEndTime())
                        || DateUtil.isIn(dateRange.getStartTime(), begin, end) || DateUtil.isIn(dateRange.getEndTime(), begin, end);
            }
        });
    }

    private Date adjustToSameYear(Date date, int targetYear) {
        // 将时间调整到目标年份
        return DateUtil.offset(date, DateField.YEAR, targetYear - DateUtil.year(date));
    }

    private List<TimeRange> parseJsonTimeRange(String timeRange) {
        JSONArray jsonArray = new JSONArray(timeRange);
        List<TimeRange> timeRangeList = new ArrayList<>(jsonArray.size());
        for (Object obj : jsonArray) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                Date startTime = jsonObject.getDate("startTime");
                Date endTime = jsonObject.getDate("endTime");
                timeRangeList.add(new TimeRange(startTime, endTime));
            }
        }
        return timeRangeList;
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
            } else {
                // 没有配置的,默认全部
                i.setDisplayDensity(List.of(1,2,3,4,5,6));
                i.setStatisticalMethods(List.of(1,2,3,4,5));
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
            Integer total = 0;
            total = sensorDataList.size();
            List<Map<String, Object>> finalSensorDataList = sensorDataList;
            if (pa.getDataSort()) {
                CollUtil.sortByProperty(finalSensorDataList, "time");
            }

            allSensorInfoList.forEach(sensorInfo -> {
                if (!CollectionUtil.isNullOrEmpty(finalSensorDataList)) {
                    List<Map<String, Object>> result = new LinkedList<>();
                    finalSensorDataList.forEach(da -> {
                        if (sensorInfo.getSensorID().equals((Integer) da.get(DbConstant.SENSOR_ID_FIELD_TOKEN))) {
                            if (pa.getFilterEmptyData()) {
                                if (da.get(fieldList.get(0).getFieldToken()) != null) {
                                    result.add(da);
                                }
                            } else {
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

                    if (pa.getDensityType() == DisplayDensity.WEEK.getValue() || pa.getDensityType() == DisplayDensity.MONTH.getValue() ||
                            pa.getDensityType() == DisplayDensity.YEAR.getValue()) {
                        sensorInfo.setMultiSensorData(InfluxDBDataUtil.calculateStatistics(result, pa.getDensityType(), pa.getStatisticsType(), pa.getDataSort()));
                    } else {
                        sensorInfo.setMultiSensorData(result);
                    }

                }
            });

            Integer finalTotal = total;
            monitorPointDataInfoList.forEach(m -> {
                m.setSensorList(allSensorInfoList.stream().filter(s -> s.getMonitorPointID().equals(m.getMonitorPointID())).collect(Collectors.toList()));
                m.setFieldList(fieldList);
                m.setSensorDataCount(finalTotal);
            });
        }

        // 特征值列表
        if (!CollectionUtil.isNullOrEmpty(pa.getEigenValueIDList())) {
            List<EigenBaseInfo> eigenValueList = tbEigenValueMapper.selectByIDs(pa.getEigenValueIDList());
            List<TbEigenValueRelation> eigenValueRelations = tbEigenValueRelationMapper.selectByIDs(pa.getEigenValueIDList());

            if (!CollectionUtil.isNullOrEmpty(eigenValueRelations) && !CollectionUtil.isNullOrEmpty(eigenValueList)) {
                eigenValueList.forEach(e -> e.setScopeStr(ScopeType.getDescriptionByCode(e.getScope())));
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
                eventBaseInfoList.forEach(e -> e.setFrequencyStr(FrequencyEnum.getDescriptionFromValue(e.getFrequency())));
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
                .filter(m -> m.get("sensorID") != null && m.get("time") != null &&
                        m.entrySet().stream()
                                .filter(entry -> !entry.getKey().equals("sensorID") &&
                                        !entry.getKey().equals("time"))
                                .allMatch(entry -> entry.getValue() != null))
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

    @Override
    public List<MonitorPointDataInfo> queryMonitorPointFilterDataList(QueryMonitorPointDataParam pa) {

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
            Integer total = 0;
            List<Integer> sensorIDList = allSensorInfoList.stream().map(SensorBaseInfoResponse::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> sensorDataList = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(),
                    pa.getDensityType(), pa.getStatisticsType(), fieldList, pa.getMonitorType());

            total = sensorDataList.size();
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
                    if (pa.getDensityType() == DisplayDensity.WEEK.getValue() || pa.getDensityType() == DisplayDensity.MONTH.getValue() ||
                            pa.getDensityType() == DisplayDensity.YEAR.getValue()) {
                        sensorInfo.setMultiSensorData(InfluxDBDataUtil.calculateStatistics(result, pa.getDensityType(), pa.getStatisticsType(), pa.getDataSort()));
                    } else {
                        sensorInfo.setMultiSensorData(result);
                    }

                    List<Map<String, Object>> multiSensorData = sensorInfo.getMultiSensorData();
                    Map<String, Map<String, Object>> maxData = calculateMaxData(multiSensorData);
                    Map<String, Map<String, Object>> minData = calculateMinData(multiSensorData);

                    // 将最大值和最小值信息添加到传感器信息中
                    sensorInfo.setMaxSensorDataList(maxData);
                    sensorInfo.setMinSensorDataList(minData);
                }
            });


            Integer finalTotal = total;
            monitorPointDataInfoList.forEach(m -> {
                m.setSensorList(allSensorInfoList.stream().filter(s -> s.getMonitorPointID().equals(m.getMonitorPointID())).collect(Collectors.toList()));
                m.setFieldList(fieldList);
                m.setSensorDataCount(finalTotal);
            });
        }

        return monitorPointDataInfoList;
    }

    @Override
    public Object queryMonitorTypeFieldList(QueryMonitorTypeFieldParam pa) {
        // 监测项目与监测子字段类型关系表
        return tbMonitorItemFieldMapper.selectListByMonitorItemID(pa.getMonitorItemID());
    }

    @Override
    public void addEigenValueList(AddEigenValueListParam pa) {

        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();

        Integer maxEigenValueID = tbEigenValueMapper.selectMaxID();
        List<AddEigenValueParam> tbEigenValues = new LinkedList<AddEigenValueParam>();
        for (int i = 0; i < pa.getDataList().size(); i++) {
            if (maxEigenValueID != null) {
                pa.getDataList().get(i).setEigenValueID(maxEigenValueID + i + 1);
            } else {
                pa.getDataList().get(i).setEigenValueID(i + 1);
            }
            tbEigenValues.add(AddEigenValueParam.toNewVo1(pa.getDataList().get(i), subjectID));
        }

        tbEigenValueMapper.insertBatchSelective(tbEigenValues);

        tbEigenValueRelationMapper.insertBatchRelation(tbEigenValues);

    }

    @Override
    public Object queryMonitorPointDataPage(QueryMonitorPointDataPageParam pa) {

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

            List<Map<String, Object>> finalSensorDataList = sensorDataList;
            allSensorInfoList.forEach(sensorInfo -> {
                if (!CollectionUtil.isNullOrEmpty(finalSensorDataList)) {
                    List<Map<String, Object>> result = new LinkedList<>();
                    finalSensorDataList.forEach(da -> {
                        if (sensorInfo.getSensorID().equals((Integer) da.get(DbConstant.SENSOR_ID_FIELD_TOKEN))) {
                            result.add(da);
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

                    if (pa.getDensityType() == DisplayDensity.WEEK.getValue() || pa.getDensityType() == DisplayDensity.MONTH.getValue() ||
                            pa.getDensityType() == DisplayDensity.YEAR.getValue()) {
                        sensorInfo.setMultiSensorData(InfluxDBDataUtil.calculateStatistics(result, pa.getDensityType(), pa.getStatisticsType(), true));
                    } else {
                        sensorInfo.setMultiSensorData(result);
                    }

                }
            });

            monitorPointDataInfoList.forEach(m -> {
                m.setSensorList(allSensorInfoList.stream().filter(s -> s.getMonitorPointID().equals(m.getMonitorPointID())).collect(Collectors.toList()));
                m.setFieldList(fieldList);
            });
        }

        return PageUtil.page(monitorPointDataInfoList, pa.getPageSize(), pa.getCurrentPage());
    }

    @Override
    public Object queryDisMonitorTypeHasDataCountByMonitorPoints(QueryDisMonitorTypeHasDataCountByMonitorPointsParam pa) {
        // 全部传感器信息
        List<SensorBaseInfoResponse> allSensorInfoList = tbSensorMapper.selectListBymonitorPointIDList(pa.getMonitorPointIDList());
        if (CollectionUtil.isNullOrEmpty(allSensorInfoList)) {
            return Collections.emptyList();
        }

        // key:监测类型
        Map<Integer, List<SensorBaseInfoResponse>> groupedByMonitorType = allSensorInfoList.stream()
                .collect(Collectors.groupingBy(SensorBaseInfoResponse::getMonitorType));


        List<Map<String, Object>> allSensorDataList = new LinkedList<Map<String, Object>>();

        // 遍历分组
        for (Map.Entry<Integer, List<SensorBaseInfoResponse>> entry : groupedByMonitorType.entrySet()) {
            Integer monitorType = entry.getKey();
            List<SensorBaseInfoResponse> sensors = entry.getValue();

            List<FieldBaseInfo> fieldList = tbMonitorTypeFieldMapper.selectListByMonitorType(monitorType);
            // 提取传感器ID列表
            List<Integer> sensorIDList = sensors.stream()
                    .map(SensorBaseInfoResponse::getSensorID)
                    .collect(Collectors.toList());

            List<Map<String, Object>> maps = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(),
                    pa.getDensity(), StatisticalMethods.LATEST.getValue(), fieldList, pa.getTbMonitorPoints().get(0).getMonitorType());

            // 将查询结果添加到总的列表中
            allSensorDataList.addAll(maps);
        }
        if (CollectionUtil.isNullOrEmpty(allSensorDataList)) {
            return null;
        }
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

        return allSensorDataList.stream()
                .filter(m -> m.get("sensorID") != null && m.get("time") != null &&
                        m.entrySet().stream()
                                .filter(entry -> !entry.getKey().equals("sensorID") &&
                                        !entry.getKey().equals("time"))
                                .allMatch(entry -> entry.getValue() != null))
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

    /**
     * 计算单个传感器 (每个子类型字段) 的最大值,以及对应时间
     *
     * @param dataList
     * @return
     */
    private Map<String, Map<String, Object>> calculateMaxData(List<Map<String, Object>> dataList) {
        Map<String, Map<String, Object>> maxDataMap = new HashMap<>();
        for (Map<String, Object> data : dataList) {
            Integer sensorID = (Integer) data.get("sensorID");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String fieldToken = entry.getKey();
                if (!fieldToken.equals("time") && !fieldToken.equals("sensorID") && !fieldToken.equals("deep")) {
                    Double value = (Double) entry.getValue();
                    Map<String, Object> maxData = maxDataMap.computeIfAbsent(fieldToken, k -> new HashMap<>());
                    if (!maxData.containsKey("value") || value > (Double) maxData.get("value")) {
                        maxData.put("value", value);
                        maxData.put("time", data.get("time"));
                        maxData.put("sensorID", sensorID);
                    }
                }
            }
        }
        return maxDataMap;
    }

    /**
     * 计算单个传感器 (每个子类型字段) 的最小值,以及对应时间
     *
     * @param dataList
     * @return
     */
    private Map<String, Map<String, Object>> calculateMinData(List<Map<String, Object>> dataList) {
        Map<String, Map<String, Object>> minDataMap = new HashMap<>();
        for (Map<String, Object> data : dataList) {
            Integer sensorID = (Integer) data.get("sensorID");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String fieldToken = entry.getKey();
                if (!fieldToken.equals("time") && !fieldToken.equals("sensorID") && !fieldToken.equals("deep")) {
                    Double value = (Double) entry.getValue();
                    Map<String, Object> minData = minDataMap.computeIfAbsent(fieldToken, k -> new HashMap<>());
                    if (!minData.containsKey("value") || value < (Double) minData.get("value")) {
                        minData.put("value", value);
                        minData.put("time", data.get("time"));
                        minData.put("sensorID", sensorID);
                    }
                }
            }
        }
        return minDataMap;
    }

}
