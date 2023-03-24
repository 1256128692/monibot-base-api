package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.monitor.monibotbaseapi.cache.DataUnitCache;
import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitoringItem;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;
import cn.shmedo.monitor.monibotbaseapi.service.ReservoirMonitorService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.MonitorTypeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.waterQuality.WaterQualityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.windPower.WindPowerUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservoirMonitorServiceImpl implements ReservoirMonitorService {

    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final TbMonitorPointMapper tbMonitorPointMapper;

    private final TbSensorMapper tbSensorMapper;

    private SensorDataDao sensorDataDao;

    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    private final RedisService redisService;

    private final TbMonitorItemMapper tbMonitorItemMapper;

    private final TbMonitorItemFieldMapper tbMonitorItemFieldMapper;

    private final TbMonitorTypeMapper tbMonitorTypeMapper;

    @Override
    public List<SensorNewDataInfo> queryMonitorPointList(QueryMonitorPointListParam pa) {

        LambdaQueryWrapper<TbProjectInfo> wrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getCompanyID, pa.getCompanyID());
        if (pa.getProjectTypeID() != null) {
            wrapper.eq(TbProjectInfo::getProjectType, pa.getProjectTypeID());
        }
        if (!StringUtil.isNullOrEmpty(pa.getAreaCode())) {
            wrapper.like(TbProjectInfo::getLocation, pa.getAreaCode());
        }
        // 1.项目信息列表
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectList(wrapper);
        if (CollectionUtil.isNullOrEmpty(tbProjectInfos)) {
            return Collections.emptyList();
        }
        List<Integer> projectIDList = tbProjectInfos.stream().map(TbProjectInfo::getID).collect(Collectors.toList());

        LambdaQueryWrapper<TbMonitorPoint> monitorPointLambdaQueryWrapper = new LambdaQueryWrapper<TbMonitorPoint>()
                .in(TbMonitorPoint::getProjectID, projectIDList)
                .eq(TbMonitorPoint::getMonitorType, pa.getMonitorType());
        if (pa.getMonitorItemID() != null) {
            monitorPointLambdaQueryWrapper.eq(TbMonitorPoint::getMonitorItemID, pa.getMonitorItemID());
        }
        // 2.监测点信息列表
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectList(monitorPointLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return Collections.emptyList();
        }
        return buildProjectAndMonitorAndSensorInfo(tbProjectInfos, tbMonitorPoints, pa.getMonitorType());
    }


    public List<SensorNewDataInfo> buildProjectAndMonitorAndSensorInfo(List<TbProjectInfo> tbProjectInfos,
                                                                       List<TbMonitorPoint> tbMonitorPoints,
                                                                       Integer monitorType) {

        List<SensorNewDataInfo> sensorNewDataInfoList = new LinkedList<>();
        // 获取项目类型(方式缓存)
        Map<Byte, TbProjectType> projectTypeMap = ProjectTypeCache.projectTypeMap;
        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;
        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;

        List<Integer> monitorPointIDs = tbMonitorPoints.stream().map(TbMonitorPoint::getID).collect(Collectors.toList());
        List<Integer> monitorItemIDs = tbMonitorPoints.stream().map(TbMonitorPoint::getMonitorItemID).collect(Collectors.toList());

        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, monitorPointIDs)
                .eq(TbSensor::getMonitorType, monitorType);
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);


        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .eq(TbMonitorItemField::getMonitorItemID, monitorItemIDs)
                .eq(TbMonitorItemField::getEnable, true);
        // 2. 监测项目与监测子字段类型关系表
        List<TbMonitorItemField> tbMonitorItemFields = tbMonitorItemFieldMapper.selectList(mIFQueryWrapper);

        // TODO:如果查不到对应关系.暂时先处理成查监测类下全部的监测子类型
        // 3. 监测点类型子字段列表
        List<TbMonitorTypeField> tbMonitorTypeFields = null;
        if (!CollectionUtil.isNullOrEmpty(tbMonitorItemFields)) {
            List<Integer> monitorTypeFieldIDs = tbMonitorItemFields.stream().map(TbMonitorItemField::getMonitorTypeFieldID).collect(Collectors.toList());
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .in(TbMonitorTypeField::getID, monitorTypeFieldIDs);
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);

        } else {
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .eq(TbMonitorTypeField::getMonitorType, monitorType);
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);
        }


        LambdaQueryWrapper<TbMonitorItem> mIQueryWrapper = new LambdaQueryWrapper<TbMonitorItem>()
                .in(TbMonitorItem::getID, monitorItemIDs);
        // 3. 监测点项目列表
        List<TbMonitorItem> TbMonitorItems = tbMonitorItemMapper.selectList(mIQueryWrapper);


        List<Integer> sensorIDList;
        if (!CollectionUtil.isNullOrEmpty(tbSensors)) {
            sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());
        } else {
            sensorIDList = null;
        }

        tbMonitorPoints.forEach(item -> {
            TbProjectInfo tbProjectInfo = tbProjectInfos.stream().filter(tpi -> tpi.getID().equals(item.getProjectID())).findFirst().orElse(null);
            List<TbSensor> sensorList = tbSensors.stream().filter(ts -> ts.getMonitorPointID().equals(item.getID())).collect(Collectors.toList());
            sensorNewDataInfoList.add(SensorNewDataInfo.reBuildProAndMonitor(item, tbProjectInfo,
                    projectTypeMap, sensorList, monitorTypeMap, TbMonitorItems));
        });

        List<Map<String, Object>> maps;
        List<FieldSelectInfo> fieldList;
        List<Map<String, Object>> filteredMaps = new LinkedList<>();
        // 根据传感器ID列表和传感器类型,查传感器最新数据
        if (!CollectionUtil.isNullOrEmpty(sensorIDList)) {
            fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);
            // 3. 传感器数据列表
            maps = sensorDataDao.querySensorNewData(sensorIDList, fieldList, false, monitorType);
            // 处理需要计算的监测子类型返回token
            fieldList = MonitorTypeUtil.handlefieldList(monitorType, fieldList);

            handleSpecialSensorDataList(monitorType, filteredMaps, tbSensors, fieldList, maps);
        } else {
            fieldList = null;
            maps = null;
        }

        Collection<Object> areas = sensorNewDataInfoList
                .stream().map(SensorNewDataInfo::getLocationInfo).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
        areas.clear();

        // 最终将传感器数据封装结果集
        List<FieldSelectInfo> finalFieldList = fieldList;

        // 处理最终返回结果集,如果是雨量则单独处理
        if (!CollectionUtil.isNullOrEmpty(filteredMaps)) {
            return handleFinalResultInfo(sensorNewDataInfoList, areaMap, filteredMaps, tbSensors, finalFieldList, dataUnitsMap);
        } else {
            return handleFinalResultInfo(sensorNewDataInfoList, areaMap, maps, tbSensors, finalFieldList, dataUnitsMap);
        }

    }

    private void handleSpecialSensorDataList(Integer monitorType, List<Map<String, Object>> filteredMaps,
                                             List<TbSensor> sensorList, List<FieldSelectInfo> fieldList,
                                             List<Map<String, Object>> maps) {
        if (CollectionUtil.isNullOrEmpty(maps)) {
            return;
        }
        // 雨量单独处理
        if (monitorType.equals(MonitorType.RAINFALL.getKey())) {
            // 当前时间
            DateTime nowDate = DateUtil.date();
            // 当天早上8点
            DateTime nowDateEightclock = DateUtil.offsetHour(DateUtil.beginOfDay(nowDate), 8);

            // 如果当前时间小于当天的早上8点,那么就统计昨天的早上8点到凌晨现在的时间
            if (DateUtil.compare(nowDate, nowDateEightclock) < 0) {
                DateTime yesterday = DateUtil.beginOfDay(DateUtil.offsetDay(nowDate, -1));
                nowDateEightclock = DateUtil.offsetHour(yesterday, 8);
            }
            //
            List<Map<String, Object>> currentRainMaps = sensorDataDao.querySensorRainStatisticsData(maps, nowDateEightclock.toTimestamp(), nowDate.toTimestamp(), fieldList, monitorType);
            if (!CollectionUtil.isNullOrEmpty(maps) && !CollectionUtil.isNullOrEmpty(currentRainMaps)) {
                for (Map<String, Object> mapEntry : maps) {
                    for (Map<String, Object> currentRainMap : currentRainMaps) {
                        String sid = mapEntry.get(DbConstant.SENSOR_ID_FIELD_TOKEN).toString();
                        String currentSid = currentRainMap.get(DbConstant.SENSOR_ID_FIELD_TOKEN).toString();
                        if (currentSid.equals(sid)) {
                            mapEntry.put(DbConstant.CURRENT_RAIN_FALL, currentRainMap.get(DbConstant.CURRENT_RAIN_FALL));
                            filteredMaps.add(mapEntry);
                        }
                    }
                }
            }
            if (!CollectionUtil.isNullOrEmpty(maps) && CollectionUtil.isNullOrEmpty(currentRainMaps)) {
                for (Map<String, Object> mapEntry : maps) {
                    mapEntry.put(DbConstant.CURRENT_RAIN_FALL, 0.0);
                    filteredMaps.add(mapEntry);
                }
            }
        } else if (monitorType.equals(MonitorType.FLOW_VELOCITY.getKey())) {

            // 流量计算
            maps.forEach(da -> {
                TbSensor tbSensor = sensorList.stream().filter(ts -> ts.getID().equals(da.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).findFirst().orElse(null);
                // 将JSON字符串转换为JSON对象
                if (tbSensor != null) {
                    da.put(DbConstant.RESERVOIR_AREA, JSONUtil.parseObj(tbSensor.getConfigFieldValue()).getByPath("$.area"));
                    if (!StringUtil.isNullOrEmpty(da.get(DbConstant.RESERVOIR_AREA).toString())) {
                        double area = Double.parseDouble(da.get(DbConstant.RESERVOIR_AREA).toString());
                        double speed = Double.parseDouble(da.get("speed").toString());
                        Double result = area * speed;
                        BigDecimal bd = new BigDecimal(result);
                        BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        da.put(DbConstant.RESERVOIR_FLOW, rounded);
                    }
                }
            });
        }
    }


    /**
     * @param sensorNewDataInfoList 最终返回信息
     * @param areaMap               行政区域信息
     * @param maps                  传感器数据列表
     * @param tbSensors             传感器基本信息
     * @param finalFieldList        监测类型子类型列表
     * @return
     */
    private List<SensorNewDataInfo> handleFinalResultInfo(List<SensorNewDataInfo> sensorNewDataInfoList,
                                                          Map<String, String> areaMap,
                                                          List<Map<String, Object>> maps,
                                                          List<TbSensor> tbSensors,
                                                          List<FieldSelectInfo> finalFieldList,
                                                          Map<Integer, TbDataUnit> dataUnitsMap) {

        sensorNewDataInfoList.forEach(snd -> {
            snd.setLocationInfo(areaMap.getOrDefault(snd.getLocationInfo(), null));
            // 存在多个传感器
            if (snd.getMultiSensor() != null) {
                if (snd.getMultiSensor()) {
                    List<Integer> currentMonitorPointIncludeSensorIDList = snd.getSensorList().stream().map(TbSensor::getID).collect(Collectors.toList());
                    List<Map<String, Object>> result = new LinkedList<>();
                    if (!CollectionUtil.isNullOrEmpty(maps)) {
                        maps.forEach(da -> {
                            if (currentMonitorPointIncludeSensorIDList.contains((Integer) da.get(DbConstant.SENSOR_ID_FIELD_TOKEN))) {
                                TbSensor tbSensor = tbSensors.stream().filter(ts -> ts.getID().equals(da.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).findFirst().orElse(null);
                                // 将JSON字符串转换为JSON对象
                                if (tbSensor != null) {
                                    da.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(tbSensor.getConfigFieldValue()).getByPath("$.deep"));
                                }
                                result.add(da);
                            }
                        });
                    }
                    snd.setMultiSensorData(result);
                    snd.setTime(DateUtil.parse((String) result.get(0).get(DbConstant.TIME_FIELD)));
                } else {
                    // 单个传感器
                    TbSensor tbSensor = snd.getSensorList().get(0);
                    if (!CollectionUtil.isNullOrEmpty(maps)) {
                        Map<String, Object> currentSensorData = maps.stream().filter(m -> m.get(DbConstant.SENSOR_ID_FIELD_TOKEN).equals(tbSensor.getID())).findFirst().orElse(null);
                        if (MapUtil.isNotEmpty(currentSensorData) ) {
                            if (tbSensor.getMonitorType().equals(MonitorType.SOIL_MOISTURE.getKey())) {
                                currentSensorData.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(tbSensor.getConfigFieldValue()).getByPath("$.deep"));
                            }
                            snd.setSensorData(handleSpecialType(tbSensor.getMonitorType(), snd.getMonitorItemID(), currentSensorData));
                            snd.setTime(DateUtil.parse((String) currentSensorData.get(DbConstant.TIME_FIELD)));
                        }
                    }
                }
            }
            snd.setFieldList(finalFieldList);

            List<TbDataUnit> dataUnitList = new LinkedList<>();
            for (int i = 0; i < finalFieldList.size(); i++) {
                if (finalFieldList.get(i).getFieldExValue() != null) {
                    if (!CollectionUtil.isNullOrEmpty(dataUnitList)) {
                        List<Integer> dataUnitIDList = dataUnitList.stream().map(TbDataUnit::getID).collect(Collectors.toList());
                        if (dataUnitIDList.contains(Integer.valueOf(finalFieldList.get(i).getFieldExValue()))) {
                            continue;
                        }
                    }
                    dataUnitList.add(dataUnitsMap.get(Integer.valueOf(finalFieldList.get(i).getFieldExValue())));
                }
            }
            snd.setDataUnitList(dataUnitList);
        });
        return sensorNewDataInfoList;
    }

    /**
     * 处理特殊传感器类型的传感器值
     *
     * @param monitorType
     * @param monitorItemID
     * @param currentSensorData
     * @return
     */
    private Map<String, Object> handleSpecialType(Integer monitorType, Integer monitorItemID, Map<String, Object> currentSensorData) {

        // 水质
        if (monitorType.equals(MonitorType.WATER_QUALITY.getKey())) {
            if (monitorItemID.equals(MonitoringItem.RIVER_WATER_QUALITY.getKey())) {
                // 河道水位,校验水质规则,[PH、溶解氧、高锰酸盐指数、氨氮、总磷](v1,v3,v6,v7,v8),抉择出水质等级最差的
                int v1 = WaterQualityUtil.getV1Category((Double) currentSensorData.get("v1"));
                int v3 = WaterQualityUtil.getV3Category((Double) currentSensorData.get("v3"));
                int v6 = WaterQualityUtil.getV6Category((Double) currentSensorData.get("v6"));
                int v7 = WaterQualityUtil.getV7Category((Double) currentSensorData.get("v7"));
                int v8 = WaterQualityUtil.getV8Category((Double) currentSensorData.get("v8"));
                List<Integer> levelList = new LinkedList<>(List.of(v1, v3, v6, v7, v8));
                int maxCategory = WaterQualityUtil.getMaxCategory(levelList);
                currentSensorData.put("waterQuality", maxCategory);

            } else if (monitorItemID.equals(MonitoringItem.RESERVOIR_WATER_QUALITY.getKey())) {
                // 水库水位,校验水质规则 ,含溶解氧(v3)
                int v3 = WaterQualityUtil.getV3Category((Double) currentSensorData.get("v3"));
                currentSensorData.put("waterQuality", v3);
            }
        } else if (monitorType.equals(MonitorType.WIND_SPEED.getKey())) {
            // 风力
            int v1 = WindPowerUtil.getV1Category((Double) currentSensorData.get("v1"));
            currentSensorData.put("windPower", v1);
        } else if (monitorType.equals(MonitorType.RAINFALL.getKey())) {
            // 当前降雨量
        }
        return currentSensorData;
    }


    /**
     * @param list 监测点子类型字段列表
     * @return 统一格式的子类型字段列表
     */
    @Override
    public List<FieldSelectInfo> getFieldSelectInfoListFromModleTypeFieldList(List<TbMonitorTypeField> list) {
        List<FieldSelectInfo> fieldSelectInfos = new ArrayList<>();
        list.forEach(modelField -> {
            FieldSelectInfo fieldSelectInfo = new FieldSelectInfo();
            fieldSelectInfo.setFieldToken(modelField.getFieldToken());
            fieldSelectInfo.setFieldName(modelField.getFieldName());
            fieldSelectInfo.setFieldOrder(modelField.getDisplayOrder());
            fieldSelectInfo.setFieldType(FieldType.valueOfString(modelField.getFieldDataType()));
//            fieldSelectInfo.setFieldStatisticsType(modelField.getFieldStatisticsType());
//            fieldSelectInfo.setFieldJsonPath(modelField.getFieldJsonPath());
            fieldSelectInfo.setFieldStatisticsType(null);
            fieldSelectInfo.setFieldJsonPath(null);
            fieldSelectInfo.setFieldExValue(modelField.getFieldUnitID().toString());
            fieldSelectInfos.add(fieldSelectInfo);
        });
        return fieldSelectInfos;
    }

    @Override
    public SensorNewDataInfo querySingleMonitorPointNewData(QueryMonitorPointDescribeParam pa) {


        LambdaQueryWrapper<TbProjectInfo> wrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .in(TbProjectInfo::getID, pa.getProjectID());
        // 1.项目信息列表
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectList(wrapper);
        if (CollectionUtil.isNullOrEmpty(tbProjectInfos)) {
            return null;
        }

        Integer monitorType = -1;
        LambdaQueryWrapper<TbMonitorPoint> monitorPointLambdaQueryWrapper = new LambdaQueryWrapper<TbMonitorPoint>()
                .in(TbMonitorPoint::getID, pa.getMonitorPointID());
        // 2.监测点信息列表
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectList(monitorPointLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return null;
        } else {
            monitorType = tbMonitorPoints.get(0).getMonitorType();
        }

        List<SensorNewDataInfo> sensorNewDataInfoList = buildProjectAndMonitorAndSensorInfo(tbProjectInfos, tbMonitorPoints, monitorType);
        if (CollectionUtil.isNullOrEmpty(sensorNewDataInfoList)) {
            return null;
        } else {
            return sensorNewDataInfoList.get(0);
        }
    }

    @Override
    public MonitorPointTypeStatisticsInfo queryMonitorPointTypeStatistics(StatisticsMonitorPointTypeParam pa) {

        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;
        Map<Byte, TbProjectType> projectTypeMap = ProjectTypeCache.projectTypeMap;

        List<TbSensor> sensorList = tbSensorMapper.selectStatisticsCountByCompanyID(pa.getCompanyID());
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectProjectInfoByCompanyID(pa.getCompanyID());
        List<TbMonitorPoint> monitorTypeAndProIDs = tbMonitorPointMapper.selectMonitorTypeAndProIDByCompanyID(pa.getCompanyID());

        MonitorPointTypeStatisticsInfo vo = new MonitorPointTypeStatisticsInfo();
        if (pa.getQueryType().equals(0)) {
            List<MonitorTypeBaseInfo> monitorTypeBaseInfos = tbMonitorTypeMapper.selectMonitorBaseInfo(pa.getCompanyID());
            monitorTypeBaseInfos.forEach(item -> {
                List<TbProjectType> projectTypeInfos = new LinkedList<>();
                if (!monitorTypeMap.isEmpty()) {
                    // 监测类型相关信息
                    TbMonitorType tbMonitorType = monitorTypeMap.get(item.getMonitorType());
                    item.setMonitorTypeName(tbMonitorType.getTypeName());
                    item.setMonitorTypeAlias(tbMonitorType.getTypeAlias());
                }

                if (!projectTypeMap.isEmpty()) {
                    if (!CollectionUtil.isNullOrEmpty(monitorTypeAndProIDs)) {
                        Set<Integer> projectIDs = monitorTypeAndProIDs.stream().filter(m -> m.getMonitorType().equals(item.getMonitorType())).map(TbMonitorPoint::getProjectID).collect(Collectors.toSet());
                        Set<Byte> projectTypes = tbProjectInfos.stream().filter(pi -> projectIDs.contains(pi.getID())).map(TbProjectInfo::getProjectType).collect(Collectors.toSet());
                        projectTypeMap.entrySet().forEach(p -> {
                            if (projectTypes.contains(p.getKey())) {
                                projectTypeInfos.add(p.getValue());
                            }
                        });
                    }
                    // 工程类型信息
                    item.setProjectTypeList(projectTypeInfos);
                }

                if ( !CollectionUtil.isNullOrEmpty(sensorList)) {
                    // 传感器警报信息
                    item.setWarnInfo(WarnInfo.toBuliderNewVo(sensorList.stream().filter(pojo -> pojo.getMonitorType().equals(item.getMonitorType())).collect(Collectors.toList())));
                }

            });
            vo.setTypeInfoList(monitorTypeBaseInfos);
        }

        return vo;
    }


    @Override
    public MonitorPointHistoryData queryMonitorPointHistoryDataList(QueryMonitorPointSensorDataListParam pa) {

        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;

        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, pa.getMonitorPointID());
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbSensors)) {
            return null;
        }
        List<Integer> sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());

        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID())
                .eq(TbMonitorItemField::getEnable, true);
        // 2. 监测项目与监测子字段类型关系表
        List<TbMonitorItemField> tbMonitorItemFields = tbMonitorItemFieldMapper.selectList(mIFQueryWrapper);

        // TODO:如果查不到对应关系.暂时先处理成查监测类下全部的监测子类型
        // 3. 监测点类型子字段列表
        List<TbMonitorTypeField> tbMonitorTypeFields = null;
        if (!CollectionUtil.isNullOrEmpty(tbMonitorItemFields)) {
            List<Integer> monitorTypeFieldIDs = tbMonitorItemFields.stream().map(TbMonitorItemField::getMonitorTypeFieldID).collect(Collectors.toList());
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .in(TbMonitorTypeField::getID, monitorTypeFieldIDs);
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);

        } else {
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .eq(TbMonitorTypeField::getMonitorType, pa.getTbMonitorPoint().getMonitorType());
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);
        }

        // 监测子类型字段
        List<FieldSelectInfo> fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);

        // 通用类型的传感器数据
        List<Map<String, Object>> maps = sensorDataDao.querySensorData(sensorIDList, pa.getBegin(), pa.getEnd(), pa.getDensity(),
                fieldList, false, pa.getTbMonitorPoint().getMonitorType());

        List<Map<String, Object>> resultMaps = new LinkedList<>();
        maps.forEach(map -> {
            // 如果当前数据为风力,水质,则进行单独处理
            Map<String, Object> stringObjectMap = handleSpecialType(pa.getTbMonitorPoint().getMonitorType(), pa.getTbMonitorPoint().getMonitorItemID(), map);
            resultMaps.add(stringObjectMap);
        });
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, tbSensors, fieldList, maps);


        // 处理时间排序
//        Map<Date, List<Map<String, Object>>> sortedGroupedMaps = TimeUtil.handleTimeSort(resultMaps, false);
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(),fieldList, dataUnitsMap);

        return new MonitorPointHistoryData(pa.getTbMonitorPoint(),tbSensors,maps,fieldList,tbDataUnitList);
    }


    @Override
    public MonitorPointHistoryData querySmcPointHistoryDataList(QuerySmcPointHistoryDataListParam pa) {

        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;
        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, pa.getMonitorPointID());
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbSensors)) {
            return null;
        }
        List<Integer> sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());

        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID())
                .eq(TbMonitorItemField::getEnable, true);
        // 2. 监测项目与监测子字段类型关系表
        List<TbMonitorItemField> tbMonitorItemFields = tbMonitorItemFieldMapper.selectList(mIFQueryWrapper);

        // TODO:如果查不到对应关系.暂时先处理成查监测类下全部的监测子类型
        // 3. 监测点类型子字段列表
        List<TbMonitorTypeField> tbMonitorTypeFields = null;
        if (!CollectionUtil.isNullOrEmpty(tbMonitorItemFields)) {
            List<Integer> monitorTypeFieldIDs = tbMonitorItemFields.stream().map(TbMonitorItemField::getMonitorTypeFieldID).collect(Collectors.toList());
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .in(TbMonitorTypeField::getID, monitorTypeFieldIDs);
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);

        } else {
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .eq(TbMonitorTypeField::getMonitorType, pa.getTbMonitorPoint().getMonitorType());
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);
        }

        // 监测子类型字段
        List<FieldSelectInfo> fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);

        // 通用类型的传感器数据
        List<Map<String, Object>> maps = sensorDataDao.querySensorData(sensorIDList, pa.getBegin(), pa.getEnd(), pa.getDensity(),
                fieldList, false, pa.getTbMonitorPoint().getMonitorType());

        List<Map<String, Object>> resultMaps = new LinkedList<>();
        maps.forEach(map -> {
            // 处理墒情数据
            Map<String, Object> stringObjectMap = handleShangQingType(map, tbSensors);
            resultMaps.add(stringObjectMap);
        });
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, tbSensors, fieldList, maps);

        // 处理时间排序
//        Map<Date, List<Map<String, Object>>> sortedGroupedMaps = TimeUtil.handleTimeSort(resultMaps, false);
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(),fieldList, dataUnitsMap);

        return new MonitorPointHistoryData(pa.getTbMonitorPoint(),tbSensors,maps,fieldList,tbDataUnitList);
    }


    @Override
    public MonitorPointAllInfo queryMonitorPointBaseInfoList(Integer projectID) {

        MonitorPointAllInfo vo = new MonitorPointAllInfo();
        LambdaQueryWrapper<TbMonitorPoint> wrapper = new LambdaQueryWrapper<TbMonitorPoint>()
                .eq(TbMonitorPoint::getProjectID, projectID);
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectList(wrapper);
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return null;
        }

        List<TbMonitorType> tbMonitorTypes = new LinkedList<TbMonitorType>();
        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;
        List<Integer> tbMonitorTypeIDs = tbMonitorPoints.stream().map(TbMonitorPoint::getMonitorType).collect(Collectors.toList());
        monitorTypeMap.entrySet().forEach(item -> {
            if (!CollectionUtil.isNullOrEmpty(tbMonitorTypeIDs)) {
                if (tbMonitorTypeIDs.contains(item.getKey())) {
                    tbMonitorTypes.add(item.getValue());
                }
            }
        });

        vo.setTbMonitorPoints(tbMonitorPoints);
        vo.setTbMonitorTypes(tbMonitorTypes);
        List<Integer> monitorItemIDs = tbMonitorPoints.stream().map(TbMonitorPoint::getMonitorItemID).collect(Collectors.toList());
        if (!CollectionUtil.isNullOrEmpty(monitorItemIDs)) {
            LambdaQueryWrapper<TbMonitorItem> tmiWrapper = new LambdaQueryWrapper<TbMonitorItem>()
                    .in(TbMonitorItem::getID, monitorItemIDs);
            List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectList(tmiWrapper);
            vo.setTbMonitorItems(tbMonitorItems);
        }

        return vo;
    }

    @Override
    public MonitorPointHistoryData queryRainPointHistoryDataList(QueryMonitorPointSensorDataListParam pa) {

        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;

        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, pa.getMonitorPointID());
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbSensors)) {
            return null;
        }
        List<Integer> sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());

        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID())
                .eq(TbMonitorItemField::getEnable, true);
        // 2. 监测项目与监测子字段类型关系表
        List<TbMonitorItemField> tbMonitorItemFields = tbMonitorItemFieldMapper.selectList(mIFQueryWrapper);

        // TODO:如果查不到对应关系.暂时先处理成查监测类下全部的监测子类型
        // 3. 监测点类型子字段列表
        List<TbMonitorTypeField> tbMonitorTypeFields = null;
        if (!CollectionUtil.isNullOrEmpty(tbMonitorItemFields)) {
            List<Integer> monitorTypeFieldIDs = tbMonitorItemFields.stream().map(TbMonitorItemField::getMonitorTypeFieldID).collect(Collectors.toList());
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .in(TbMonitorTypeField::getID, monitorTypeFieldIDs);
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);

        } else {
            LambdaQueryWrapper<TbMonitorTypeField> mTQueryWrapper = new LambdaQueryWrapper<TbMonitorTypeField>()
                    .eq(TbMonitorTypeField::getMonitorType, pa.getTbMonitorPoint().getMonitorType());
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);
        }

        // 监测子类型字段
        List<FieldSelectInfo> fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);

        // 通用类型的传感器数据
        List<Map<String, Object>> maps = sensorDataDao.querySensorData(sensorIDList, pa.getBegin(), pa.getEnd(), pa.getDensity(),
                fieldList, false, pa.getTbMonitorPoint().getMonitorType());

        // 处理雨量历史时间段的当前雨量
        handleRainTypeSensorHistoryDataList(maps, pa.getBegin(), pa.getEnd());

        // 处理时间排序
//        Map<Date, List<Map<String, Object>>> sortedGroupedMaps = TimeUtil.handleTimeSort(maps, false);
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(),fieldList, dataUnitsMap);

        return new MonitorPointHistoryData(pa.getTbMonitorPoint(),tbSensors,maps,fieldList,tbDataUnitList);
    }

    /**
     * 处理数据单位
     * @param monitorType
     * @param fieldList
     * @param dataUnitsMap
     * @return
     */
    private List<TbDataUnit> handleDataUnit(Integer monitorType, List<FieldSelectInfo> fieldList, Map<Integer, TbDataUnit> dataUnitsMap) {
        List<TbDataUnit> tbDataUnitList = new LinkedList<>();
        if (!CollectionUtil.isNullOrEmpty(fieldList)) {
            List<String> dataUnitIDList = fieldList.stream().map(FieldSelectInfo::getFieldExValue).collect(Collectors.toList());
            dataUnitsMap.entrySet().forEach(entry -> {
                if (dataUnitIDList.contains(entry.getKey().toString())) {
                    tbDataUnitList.add(entry.getValue());
                }
            });
            MonitorTypeUtil.handlefieldList(monitorType, fieldList);
        }
        return tbDataUnitList;
    }


    /**
     * 处理传感器类型为雨量的历史数据,计算出每个时间段的当前数据
     * 计算规则,当天8点的当前雨量为0,当天8点后的数据,比如说10点的数据,就是(8点的v1 + 10点的v1)
     * 以此类推, 12点的数据就是 (8点的v1 + 10点的v1 + 12点的v1)
     *
     * @param dataList
     * @param begin
     * @param end
     */
    private void handleRainTypeSensorHistoryDataList(List<Map<String, Object>> dataList, Timestamp begin, Timestamp end) {

        String dateStr = DateUtil.format(DateUtil.beginOfDay(new Date(end.getTime())), "yyyy-MM-dd HH:mm:ss");
        String resultStr = dateStr.substring(0, 10) + " 08:00:00";
        Date eightClockDateTime = DateUtil.parse(resultStr, "yyyy-MM-dd HH:mm:ss");
        DateTime yesterdayEightClockDateTime = DateUtil.offsetDay(eightClockDateTime, -1);

        List<Map<String, Object>> yesterdayDataList = new LinkedList<>();
        List<Map<String, Object>> todayDataList = new LinkedList<>();

        // 对数据进行分流,超过当天8点的数据,为一批;  不超过的当天8点的记为另一批
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> data1 = dataList.get(i);
            dataList.get(i).put("currentRainfall", 0.0);
            DateTime currentTime = DateUtil.parse(data1.get("time").toString());
            if (DateUtil.compare(currentTime, eightClockDateTime) >= 0) {
                todayDataList.add(data1);
            } else {
                yesterdayDataList.add(data1);
            }
        }

        // 当日超过8点的数据处理
        if (!CollectionUtil.isNullOrEmpty(todayDataList)) {
            for (int i = 0; i < todayDataList.size(); i++) {
                Map<String, Object> data1 = todayDataList.get(i);
                for (int j = 0; j < todayDataList.size(); j++) {
                    Map<String, Object> data2 = todayDataList.get(j);
                    // 在这里执行 data1 和 data2 的比较
                    DateTime currentTime = DateUtil.parse(data1.get("time").toString());
                    DateTime otherTime = DateUtil.parse(data2.get("time").toString());
                    if (DateUtil.compare(currentTime, otherTime) >= 0 &&
                            data1.get(DbConstant.SENSOR_ID_FIELD_TOKEN).equals(data2.get("sensorID"))) {
                        // 处理相同传感器 ID 的数据 并且 当前传感器采集时间大于或者等于其余传感器采集时间
                        double currentRainfall = Double.parseDouble(data1.get("currentRainfall").toString());
                        double v1 = Double.parseDouble(data2.get("v1").toString());
                        currentRainfall += v1;
                        BigDecimal bd = new BigDecimal(currentRainfall);
                        BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        data1.put("currentRainfall", rounded);
                        // 如果当前时间是8点,则不统计当前降雨量
                        if (currentTime.equals(eightClockDateTime)) {
                            data1.put("currentRainfall", 0.0);
                        }
                    }
                }
            }
        }

        if (!CollectionUtil.isNullOrEmpty(yesterdayDataList)) {
            for (int i = 0; i < yesterdayDataList.size(); i++) {
                Map<String, Object> data1 = yesterdayDataList.get(i);
                for (int j = 0; j < yesterdayDataList.size(); j++) {
                    Map<String, Object> data2 = yesterdayDataList.get(j);
                    // 在这里执行 data1 和 data2 的比较
                    DateTime currentTime = DateUtil.parse(data1.get("time").toString());
                    DateTime otherTime = DateUtil.parse(data2.get("time").toString());
                    if (DateUtil.compare(currentTime, otherTime) >= 0 &&
                            data1.get("sensorID").equals(data2.get("sensorID"))) {
                        // 处理相同传感器 ID 的数据 并且 当前传感器采集时间大于或者等于其余传感器采集时间
                        double currentRainfall = Double.parseDouble(data1.get("currentRainfall").toString());
                        double v1 = Double.parseDouble(data2.get("v1").toString());
                        currentRainfall += v1;
                        BigDecimal bd = new BigDecimal(currentRainfall);
                        BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        data1.put("currentRainfall", rounded);
                        // 如果当前时间是8点,则不统计当前降雨量
                        if (currentTime.equals(yesterdayEightClockDateTime)) {
                            data1.put("currentRainfall", 0.0);
                        }
                    }
                }
            }
        }

    }


    /**
     * 处理墒情类型的深度
     *
     * @param map
     * @param tbSensors
     * @return
     */
    private Map<String, Object> handleShangQingType(Map<String, Object> map, List<TbSensor> tbSensors) {

        TbSensor sensor = tbSensors.stream().filter(tbSensor -> tbSensor.getID().equals(map.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).findFirst().orElse(null);
        if (sensor != null) {
            map.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(sensor.getConfigFieldValue()).getByPath("$.deep"));
        }
        return map;
    }


}
