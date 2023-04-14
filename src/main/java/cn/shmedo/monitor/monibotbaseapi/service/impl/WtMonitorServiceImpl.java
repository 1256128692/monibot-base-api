package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
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
import cn.shmedo.monitor.monibotbaseapi.model.enums.WaterQuality;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
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
public class WtMonitorServiceImpl implements WtMonitorService {

    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final TbMonitorPointMapper tbMonitorPointMapper;

    private final TbSensorMapper tbSensorMapper;

    private SensorDataDao sensorDataDao;

    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    private final RedisService redisService;

    private final TbMonitorItemMapper tbMonitorItemMapper;

    private final TbMonitorItemFieldMapper tbMonitorItemFieldMapper;

    private final TbMonitorTypeMapper tbMonitorTypeMapper;

    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;

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
        // 2.监测点信息列表
        List<MonitorPointAndItemInfo> tbMonitorPoints = tbMonitorPointMapper.selectListByCondition(projectIDList, pa.getMonitorType(), pa.getMonitorItemID());
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return Collections.emptyList();
        }
        return buildProjectAndMonitorAndSensorInfo(tbProjectInfos, tbMonitorPoints, pa.getMonitorType());
    }


    /**
     * @param tbProjectInfos  项目基本信息列表
     * @param tbMonitorPoints 监测点基本信息列表
     * @param monitorType     监测类型
     * @return
     */
    public List<SensorNewDataInfo> buildProjectAndMonitorAndSensorInfo(List<TbProjectInfo> tbProjectInfos,
                                                                       List<MonitorPointAndItemInfo> tbMonitorPoints,
                                                                       Integer monitorType) {

        List<SensorNewDataInfo> sensorNewDataInfoList = new LinkedList<>();
        // 获取项目类型(方式缓存)
        Map<Byte, TbProjectType> projectTypeMap = ProjectTypeCache.projectTypeMap;
        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;
        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;

        List<Integer> monitorPointIDs = tbMonitorPoints.stream().map(MonitorPointAndItemInfo::getID).collect(Collectors.toList());
        List<Integer> monitorItemIDs = tbMonitorPoints.stream().map(MonitorPointAndItemInfo::getMonitorItemID).collect(Collectors.toList());

        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, monitorPointIDs)
                .eq(TbSensor::getMonitorType, monitorType);
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);


        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .in(TbMonitorItemField::getMonitorItemID, monitorItemIDs);
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

        List<Integer> sensorIDList;
        if (!CollectionUtil.isNullOrEmpty(tbSensors)) {
            sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());
        } else {
            sensorIDList = null;
        }

        tbMonitorPoints.forEach(item -> {
            TbProjectInfo projectInfo = tbProjectInfos.stream().filter(tpi -> tpi.getID().equals(item.getProjectID())).findFirst().orElse(null);
            List<TbSensor> sensorList = tbSensors.stream().filter(ts -> ts.getMonitorPointID().equals(item.getID())).collect(Collectors.toList());
            sensorNewDataInfoList.add(SensorNewDataInfo.reBuildProAndMonitor(item, projectInfo,
                    projectTypeMap, sensorList, monitorTypeMap));
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

    /**
     * @param monitorType  监测类型
     * @param sensorList   传感器信息列表
     * @param fieldList    字段列表(v1,v2,xxx)
     * @param maps         传感器数据列表
     * @param filteredMaps 雨量的传感器数据列表
     */
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
            // 流量暂不计算
        } else if (monitorType.equals(MonitorType.LEVEL.getKey())) {

            String key = "levelChange";
            // 处理压力变化值,规则当前的数据减去上一笔的数据
            for (int i = 0; i < maps.size(); i++) {
                Map<String, Object> map = maps.get(i);
                String timeStr = (String) map.get("time");
                long time = DateUtil.parse(timeStr).getTime();

                // 找到两个小时之前的时间戳
                long twoHoursAgo = time - 2 * 60 * 60 * 1000;

                // 在之前的Map对象中查找相应的v1值
                for (int j = 0; j < maps.size(); j++) {
                    Map<String, Object> prevMap = maps.get(j);
                    String prevTimeStr = (String) prevMap.get("time");
                    long prevTime = DateUtil.parse(prevTimeStr).getTime();
                    if (prevTime == twoHoursAgo) {
                        // 如果之前的Map对象中存在v1字段，则将其作为twoHoursAgo时刻的v1值
                        if (prevMap.containsKey("distance")) {
                            double v1TwoHoursAgo = (double) prevMap.get("distance");
                            // 计算stressChange并添加到当前Map对象中
                            double changeValue = (double) map.get("distance") - v1TwoHoursAgo;
                            BigDecimal changeBD = new BigDecimal(changeValue);
                            BigDecimal rounded = changeBD.setScale(2, BigDecimal.ROUND_HALF_UP);
                            map.put(key, rounded);
                            break;
                        }
                    }
                }
            }

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
            if (snd.getMultiSensor() != null) {
                if (snd.getMultiSensor()) {
                    // 存在多个传感器
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
                    if (!CollectionUtil.isNullOrEmpty(result)) {
                        snd.setTime(DateUtil.parse((String) result.get(0).get(DbConstant.TIME_FIELD)));
                    }
                } else {
                    // 单个传感器
                    TbSensor tbSensor = snd.getSensorList().get(0);
                    if (!CollectionUtil.isNullOrEmpty(maps)) {
                        Map<String, Object> currentSensorData = maps.stream().filter(m -> m.get(DbConstant.SENSOR_ID_FIELD_TOKEN).equals(tbSensor.getID())).findFirst().orElse(null);
                        if (MapUtil.isNotEmpty(currentSensorData)) {
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
            if (!CollectionUtil.isNullOrEmpty(finalFieldList)) {
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
            }
            snd.setDataUnitList(dataUnitList);
        });

        return sensorNewDataInfoList.stream()
                .sorted(Comparator.comparing(SensorNewDataInfo::getTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 处理特殊传感器类型的传感器的额外值
     * 比如:水质,风力
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
                int v1 = WaterQualityUtil.getV1Category((Double) currentSensorData.get("dissolvedOxygen"));
                int v3 = WaterQualityUtil.getV3Category((Double) currentSensorData.get("turbidity"));
                int v6 = WaterQualityUtil.getV6Category((Double) currentSensorData.get("homomethylateIndex"));
                int v7 = WaterQualityUtil.getV7Category((Double) currentSensorData.get("ammoniaNitrogen"));
                int v8 = WaterQualityUtil.getV8Category((Double) currentSensorData.get("phosphorusTotal"));
                List<Integer> levelList = new LinkedList<>(List.of(v1, v3, v6, v7, v8));
                int maxCategory = WaterQualityUtil.getMaxCategory(levelList);
                currentSensorData.put("waterQuality", WaterQuality.getValueByKey(maxCategory));

            } else if (monitorItemID.equals(MonitoringItem.RESERVOIR_WATER_QUALITY.getKey())) {
                // 水库水位,校验水质规则 ,含溶解氧(v3)
                int v3 = WaterQualityUtil.getV3Category((Double) currentSensorData.get("turbidity"));
                currentSensorData.put("waterQuality", WaterQuality.getValueByKey(v3));
            }
        } else if (monitorType.equals(MonitorType.WIND_SPEED.getKey())) {
            // 风力
            int v1 = WindPowerUtil.getV1Category((Double) currentSensorData.get("windSpeed"));
            currentSensorData.put("windPower", v1);
        } else if (monitorType.equals(MonitorType.RAINFALL.getKey())) {
            // 当前降雨量
        } else if (monitorType.equals(MonitorType.STRESS.getKey())) {
            // 压力变化
        } else if (monitorType.equals(MonitorType.PRESSURE.getKey())) {
            // 压强变化
        } else if (monitorType.equals(MonitorType.LEVEL.getKey())) {
            // 水位变化
            currentSensorData.put("levelChange", 0.0);
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
        // 2.监测点信息列表
        List<MonitorPointAndItemInfo> tbMonitorPoints = tbMonitorPointMapper.selectListByCondition(Arrays.asList(pa.getProjectID()), null, null);
        List<MonitorPointAndItemInfo> result = new ArrayList<>();
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return null;
        } else {
            MonitorPointAndItemInfo monitorPointAndItemInfo = tbMonitorPoints.stream().filter(pojo ->
                pojo.getID().equals(pa.getMonitorPointID())
            ).findFirst().orElse(null);
            if (monitorPointAndItemInfo != null) {
                monitorType = monitorPointAndItemInfo.getMonitorType();
                result.add(monitorPointAndItemInfo);
            }else {
                return null;
            }
        }

        List<SensorNewDataInfo> sensorNewDataInfoList = buildProjectAndMonitorAndSensorInfo(tbProjectInfos, result, monitorType);
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

        // TODO WarnInfo待处理,统计方式:处理统计成为单个监测点下最差情况
        List<TbSensor> sensorList = tbSensorMapper.selectStatisticsCountByCompanyID(pa.getCompanyID());
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectProjectInfoByCompanyID(pa.getCompanyID());

        // 当前公司下没有工程,则没有监测类型,返回空
        if (CollectionUtil.isNullOrEmpty(tbProjectInfos)) {
            return null;
        }
        // 查询该公司下  已经存在监测点配置监测类别  的数据
        List<Integer> projectIDList = tbProjectInfos.stream().map(TbProjectInfo::getID).collect(Collectors.toList());
        LambdaQueryWrapper<TbProjectMonitorClass> wrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                .eq(TbProjectMonitorClass::getMonitorClass, pa.getQueryType())
                .in(TbProjectMonitorClass::getProjectID, projectIDList)
                .eq(TbProjectMonitorClass::getEnable, true);
        List<TbProjectMonitorClass> proMonitorClassList = tbProjectMonitorClassMapper.selectList(wrapper);

        // 当前公司下有工程,但是工程都没有配置监测类别,返回空
        if (CollectionUtil.isNullOrEmpty(proMonitorClassList)) {
            return null;
        }
        List<Integer> proIDList = proMonitorClassList.stream().map(TbProjectMonitorClass::getProjectID).collect(Collectors.toList());
        List<TbMonitorPoint> tbMonitorPointList = tbMonitorPointMapper.selectMonitorTypeAndProIDByProIDList(proIDList);

        // 监测项目列表
        List<MonitorItemBaseInfo> monitorItemList = tbMonitorItemMapper.selectListByCondition(pa.getCompanyID(), proIDList, pa.getQueryType());

        MonitorPointTypeStatisticsInfo vo = new MonitorPointTypeStatisticsInfo();

        List<MonitorTypeBaseInfo> monitorTypeBaseInfos = tbMonitorTypeMapper.selectMonitorBaseInfo(proIDList);
        monitorTypeBaseInfos.forEach(item -> {
            List<TbProjectType> projectTypeInfos = new LinkedList<>();
            if (!monitorTypeMap.isEmpty()) {
                // 监测类型相关信息
                TbMonitorType tbMonitorType = monitorTypeMap.get(item.getMonitorType());
                item.setMonitorTypeName(tbMonitorType.getTypeName());
                item.setMonitorTypeAlias(tbMonitorType.getTypeAlias());
            }
            if (!projectTypeMap.isEmpty()) {
                // 工程类型信息
                if (!CollectionUtil.isNullOrEmpty(tbMonitorPointList)) {
                    Set<Integer> projectIDs = tbMonitorPointList.stream().filter(m -> m.getMonitorType().equals(item.getMonitorType())).map(TbMonitorPoint::getProjectID).collect(Collectors.toSet());
                    Set<Byte> projectTypes = tbProjectInfos.stream().filter(pi -> projectIDs.contains(pi.getID())).map(TbProjectInfo::getProjectType).collect(Collectors.toSet());
                    projectTypeMap.entrySet().forEach(p -> {
                        if (projectTypes.contains(p.getKey())) {
                            projectTypeInfos.add(p.getValue());
                        }
                    });
                }
                item.setProjectTypeList(projectTypeInfos);
            }
            if (!CollectionUtil.isNullOrEmpty(sensorList)) {
                // 传感器警报信息
                item.setWarnInfo(WarnInfo.toBuliderNewVo(sensorList.stream().filter(pojo -> pojo.getMonitorType().equals(item.getMonitorType())).collect(Collectors.toList())));
            }
            if (!CollectionUtil.isNullOrEmpty(monitorItemList)) {
                // 监测项目信息列表
                List<MonitorItemBaseInfo> list = monitorItemList.stream().filter(pojo -> pojo.getMonitorType().equals(item.getMonitorType())).collect(Collectors.toList());
                item.setMonitorItemList(list);
            }

        });
        vo.setTypeInfoList(monitorTypeBaseInfos);
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
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID());
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
        if (!CollectionUtil.isNullOrEmpty(maps)) {
            maps.forEach(map -> {
                // 如果当前数据为风力,水质,则进行单独处理
                Map<String, Object> stringObjectMap = handleSpecialType(pa.getTbMonitorPoint().getMonitorType(), pa.getTbMonitorPoint().getMonitorItemID(), map);
                resultMaps.add(stringObjectMap);
            });
        }
        // 处理需要计算的监测子类型返回token
        MonitorTypeUtil.handlefieldList(pa.getTbMonitorPoint().getMonitorType(), fieldList);
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, tbSensors, fieldList, maps);

        // 处理时间排序
//        Map<Date, List<Map<String, Object>>> sortedGroupedMaps = TimeUtil.handleTimeSort(resultMaps, false);
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(), fieldList, dataUnitsMap);

        return new MonitorPointHistoryData(pa.getTbMonitorPoint(), tbSensors, maps, fieldList, tbDataUnitList);
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
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID());
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
        if (!CollectionUtil.isNullOrEmpty(maps)) {
            maps.forEach(map -> {
                // 处理墒情数据
                Map<String, Object> stringObjectMap = handleShangQingType(map, tbSensors);
                resultMaps.add(stringObjectMap);
            });
        }
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, tbSensors, fieldList, maps);

        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(), fieldList, dataUnitsMap);

        return new MonitorPointHistoryData(pa.getTbMonitorPoint(), tbSensors, maps, fieldList, tbDataUnitList);
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
    public RainMonitorPointHistoryData queryRainPointHistoryDataList(QueryRainMonitorPointSensorDataListParam pa) {

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
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID());
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

        Double dailyRainfall = 0.0;
        List<Map<String, Object>> resultList = null;
        if (!CollectionUtil.isNullOrEmpty(maps)) {
            // 处理雨量历史时间段的降雨量
            resultList = handleDataOrder(maps, pa.getEnd());
            // 处理雨量历史时间段的当前雨量
            handleRainTypeSensorHistoryDataList(resultList, pa.getBegin(), pa.getEnd());
            // 处理日降雨量
            DateTime endTime = DateUtil.offsetHour(DateUtil.beginOfDay(pa.getBegin()), 32);
            List<Map<String, Object>> dailyRainData = sensorDataDao.querySensorDailyRainData(sensorIDList, pa.getBegin(), new Timestamp(endTime.getTime()));
            if (!CollectionUtil.isNullOrEmpty(dailyRainData)) {
                if (dailyRainData.get(0).get(DbConstant.DAILY_RAINFALL) != null) {
                    dailyRainfall = (Double) dailyRainData.get(0).get(DbConstant.DAILY_RAINFALL);
                }
            }
        }
        // 处理需要计算的监测子类型返回token
        MonitorTypeUtil.handlefieldList(pa.getTbMonitorPoint().getMonitorType(), fieldList);
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(), fieldList, dataUnitsMap);

        return new RainMonitorPointHistoryData(pa.getTbMonitorPoint(), tbSensors, resultList, fieldList, tbDataUnitList, dailyRainfall);
    }

    /**
     * 遍历 dataList，所有数据的时间都往后挪2小时
     *
     * @param dataList
     * @param end
     */
    private List<Map<String, Object>> handleDataOrder(List<Map<String, Object>> dataList, Timestamp end) {
        List<Map<String, Object>> result = new LinkedList<>();

        dataList.forEach(data -> {
            String timeStr = (String) data.get("time");
            long timeMillis = DateUtil.parse(timeStr).getTime();
            timeMillis += 2 * 60 * 60 * 1000; // Add 2 hours
            Date newTime = new Date(timeMillis);
            String newTimeStr = DateUtil.formatDateTime(newTime);
            data.put("time", newTimeStr);
        });

        dataList.forEach(item -> {
            String timeStr = (String) item.get("time");
            long timeMillis = DateUtil.parse(timeStr).getTime();
            if (timeMillis <= end.getTime()) {
                result.add(item);
            }
        });
        return result;
    }


    @Override
    public MonitorPointListHistoryData queryMonitorPointListHistoryDataList(QueryMonitorPointsSensorDataListParam pa) {
        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;

        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, pa.getMonitorPointIDs());
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbSensors)) {
            return null;
        }
        List<Integer> sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());

        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .eq(TbMonitorItemField::getMonitorItemID, pa.getMonitorItemID());
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
                    .eq(TbMonitorTypeField::getMonitorType, pa.getMonitorType());
            tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectList(mTQueryWrapper);
        }

        // 监测子类型字段
        List<FieldSelectInfo> fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);

        // 通用类型的传感器数据
        List<Map<String, Object>> maps = sensorDataDao.querySensorData(sensorIDList, pa.getBegin(), pa.getEnd(), pa.getDensity(),
                fieldList, false, pa.getMonitorType());

        List<Map<String, Object>> resultMaps = new LinkedList<>();
        maps.forEach(map -> {
            // 如果当前数据为风力,水质,则进行单独处理
            Map<String, Object> stringObjectMap = handleSpecialType(pa.getMonitorType(), pa.getMonitorItemID(), map);
            resultMaps.add(stringObjectMap);
        });
        handleSpecialSensorDataList(pa.getMonitorType(), resultMaps, tbSensors, fieldList, maps);

        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getMonitorType(), fieldList, dataUnitsMap);

        return new MonitorPointListHistoryData(pa.getTbMonitorPointList(), tbSensors, maps, fieldList, tbDataUnitList);
    }


    @Override
    public TriaxialDisplacementMonitorPointHistoryData queryDisplacementPointHistoryDataList(QueryDisplacementPointHistoryParam pa) {

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
                .eq(TbMonitorItemField::getMonitorItemID, pa.getTbMonitorPoint().getMonitorItemID());
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
        if (!CollectionUtil.isNullOrEmpty(maps)) {
            maps.forEach(map -> {
                // 处理内部三轴位移数据
                Map<String, Object> stringObjectMap = handleTriaxialDisplacementType(map, tbSensors);
                resultMaps.add(stringObjectMap);
            });
        }
        // 处理需要计算的监测子类型返回token
        MonitorTypeUtil.handlefieldList(pa.getTbMonitorPoint().getMonitorType(), fieldList);
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, tbSensors, fieldList, maps);

        // 处理时间排序
        Map<Date, List<Map<String, Object>>> sortedGroupedMaps = TimeUtil.handleTimeSort(resultMaps, false);
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(), fieldList, dataUnitsMap);

        return new TriaxialDisplacementMonitorPointHistoryData(pa.getTbMonitorPoint(), tbSensors, sortedGroupedMaps, fieldList, tbDataUnitList);
    }

    @Override
    public List<TriaxialDisplacementSensorNewDataInfo> queryDisplacementMonitorPointNewDataList(QueryDisplacementMonitorPointNewDataParam pa) {
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
        // 2.监测点信息列表
        List<MonitorPointAndItemInfo> tbMonitorPoints = tbMonitorPointMapper.selectListByCondition(projectIDList, pa.getMonitorType(), pa.getMonitorItemID());
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return Collections.emptyList();
        }
        return buildTDProjectAndMonitorAndSensorInfo(tbProjectInfos, tbMonitorPoints, pa.getMonitorType());
    }

    private List<TriaxialDisplacementSensorNewDataInfo> buildTDProjectAndMonitorAndSensorInfo(List<TbProjectInfo> tbProjectInfos, List<MonitorPointAndItemInfo> tbMonitorPoints, Integer monitorType) {

        List<TriaxialDisplacementSensorNewDataInfo> sensorNewDataInfoList = new LinkedList<>();
        // 获取项目类型(方式缓存)
        Map<Byte, TbProjectType> projectTypeMap = ProjectTypeCache.projectTypeMap;
        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;
        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;

        List<Integer> monitorPointIDs = tbMonitorPoints.stream().map(MonitorPointAndItemInfo::getID).collect(Collectors.toList());
        List<Integer> monitorItemIDs = tbMonitorPoints.stream().map(MonitorPointAndItemInfo::getMonitorItemID).collect(Collectors.toList());

        LambdaQueryWrapper<TbSensor> sensorLambdaQueryWrapper = new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, monitorPointIDs)
                .eq(TbSensor::getMonitorType, monitorType);
        // 1.传感器信息列表
        List<TbSensor> tbSensors = tbSensorMapper.selectList(sensorLambdaQueryWrapper);


        LambdaQueryWrapper<TbMonitorItemField> mIFQueryWrapper = new LambdaQueryWrapper<TbMonitorItemField>()
                .in(TbMonitorItemField::getMonitorItemID, monitorItemIDs);
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

        List<Integer> sensorIDList;
        if (!CollectionUtil.isNullOrEmpty(tbSensors)) {
            sensorIDList = tbSensors.stream().map(TbSensor::getID).collect(Collectors.toList());
        } else {
            sensorIDList = null;
        }

        tbMonitorPoints.forEach(item -> {
            TbProjectInfo tbProjectInfo = tbProjectInfos.stream().filter(tpi -> tpi.getID().equals(item.getProjectID())).findFirst().orElse(null);
            List<TbSensor> sensorList = tbSensors.stream().filter(ts -> ts.getMonitorPointID().equals(item.getID())).collect(Collectors.toList());
            sensorNewDataInfoList.add(TriaxialDisplacementSensorNewDataInfo.reBuildProAndMonitor(item, tbProjectInfo,
                    projectTypeMap, sensorList, monitorTypeMap));
        });

        List<Map<String, Object>> maps;
        List<FieldSelectInfo> fieldList;
        // 根据传感器ID列表和传感器类型,查传感器最新数据
        if (!CollectionUtil.isNullOrEmpty(sensorIDList)) {
            fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);
            // 3. 传感器数据列表
            maps = sensorDataDao.querySensorNewData(sensorIDList, fieldList, false, monitorType);
            handleTriaxialDisplacementSensorDataList(maps, sensorNewDataInfoList);
        }

        return sensorNewDataInfoList;
    }

    private void handleTriaxialDisplacementSensorDataList(List<Map<String, Object>> dataList,
                                                          List<TriaxialDisplacementSensorNewDataInfo> monitorPointInfos) {

        Collection<Object> areas = monitorPointInfos
                .stream().map(TriaxialDisplacementSensorNewDataInfo::getLocationInfo).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
        areas.clear();

        monitorPointInfos.forEach(pojo -> {
            List<Integer> sensorIDs = pojo.getSensorList().stream().map(TbSensor::getID).collect(Collectors.toList());

            // 过滤出来当前监测点下的传感器数据列表
            List<Map<String, Object>> newDataList = dataList.stream()
                    .filter(data -> sensorIDs.contains(Integer.valueOf(data.get("sensorID").toString())))
                    .collect(Collectors.toList());

            // 找到最大的 v1 和对应的深度
            Optional<Map<String, Object>> maxV1Data = newDataList.stream()
                    .max(Comparator.comparingDouble(data -> (Double) data.get("aAccu")));
            Integer sensorIDByV1 = maxV1Data.map(data -> (Integer) data.get("sensorID")).orElse(0);
            String s1 = pojo.getSensorList().stream().filter(s -> s.getID().equals(sensorIDByV1)).map(TbSensor::getConfigFieldValue).findFirst().orElse(null);
            Double xDeep = Double.parseDouble(JSONUtil.parseObj(s1).getByPath("$.deep").toString());

            // 找到最大的 v3 和对应的深度
            Optional<Map<String, Object>> maxV3Data = newDataList.stream()
                    .max(Comparator.comparingDouble(data -> (Double) data.get("bAccu")));
            Integer sensorIDByV3 = maxV3Data.map(data -> (Integer) data.get("sensorID")).orElse(0);
            String s2 = pojo.getSensorList().stream().filter(s -> s.getID().equals(sensorIDByV3)).map(TbSensor::getConfigFieldValue).findFirst().orElse(null);
            Double yDeep = Double.parseDouble(JSONUtil.parseObj(s2).getByPath("$.deep").toString());

            // 找到最大的 v5 和对应的深度
            Optional<Map<String, Object>> maxV5Data = newDataList.stream()
                    .max(Comparator.comparingDouble(data -> (Double) data.get("cOriginal")));
            Integer sensorIDByV5 = maxV5Data.map(data -> (Integer) data.get("sensorID")).orElse(0);
            String s3 = pojo.getSensorList().stream().filter(s -> s.getID().equals(sensorIDByV5)).map(TbSensor::getConfigFieldValue).findFirst().orElse(null);
            Double zDeep = Double.parseDouble(JSONUtil.parseObj(s3).getByPath("$.deep").toString());

            // 构建新的数据集
            Map<String, Object> newData = new HashMap<>();
            newData.put("xValue", maxV1Data.map(data -> data.get("aAccu")).orElse(Double.NaN));
            newData.put("yValue", maxV3Data.map(data -> data.get("bAccu")).orElse(Double.NaN));
            newData.put("zValue", maxV5Data.map(data -> data.get("cOriginal")).orElse(Double.NaN));
            newData.put("xDeep", xDeep);
            newData.put("yDeep", yDeep);
            newData.put("zDeep", zDeep);

            pojo.setLocationInfo(areaMap.getOrDefault(pojo.getLocationInfo(), null));
            pojo.setSensorData(newData);

            Optional<Map<String, Object>> latest = newDataList.stream()
                    .sorted(Comparator.comparing((Map<String, Object> data) -> (String) data.get("time")).reversed())
                    .findFirst();
            if (latest.isPresent()) {
                String latestTime = (String) latest.get().get("time");
                pojo.setTime(DateUtil.parse(latestTime, "yyyy-MM-dd HH:mm:ss.SSS"));
            }

        });

    }

    /**
     * 处理内部三轴位移
     *
     * @param map       传感器数据
     * @param tbSensors 传感器列表
     * @return
     */
    private Map<String, Object> handleTriaxialDisplacementType(Map<String, Object> map, List<TbSensor> tbSensors) {
        TbSensor sensor = tbSensors.stream().filter(tbSensor -> tbSensor.getID().equals(map.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).findFirst().orElse(null);
        if (sensor != null) {
            map.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(sensor.getConfigFieldValue()).getByPath("$.deep"));
        }
        return map;
    }

    /**
     * 处理数据单位
     *
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
            double clock8v1 = 0.0;
            for (Map<String, Object> map : todayDataList) {
                String timeStr = (String) map.get("time");
                if (StrUtil.isNotBlank(timeStr)) {
                    if (resultStr.equals(timeStr)) {
                        clock8v1 = Double.parseDouble(map.get("rainfall").toString());
                    }
                }
            }
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
                        double v1 = Double.parseDouble(data2.get("rainfall").toString());
                        if (currentTime.equals(eightClockDateTime)) {
                            currentRainfall = 0.0;
                        } else {
                            currentRainfall += v1;
                        }
                        BigDecimal bd = new BigDecimal(currentRainfall);
                        BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        data1.put("currentRainfall", rounded);
                    }
                }
            }
            for (int i = 0; i < todayDataList.size(); i++) {
                DateTime currentTime = DateUtil.parse(todayDataList.get(i).get("time").toString());
                if (!currentTime.equals(eightClockDateTime)) {
                    double currentRainfall = Double.parseDouble(todayDataList.get(i).get("currentRainfall").toString());
                    BigDecimal rounded = new BigDecimal(currentRainfall).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal bd2 = new BigDecimal(clock8v1);
                    BigDecimal result = rounded.subtract(bd2);
                    todayDataList.get(i).put("currentRainfall", result);
                }
            }
        }

        // 根据需求,不展示昨日的当前降雨量
        if (!CollectionUtil.isNullOrEmpty(yesterdayDataList)) {
            for (int i = 0; i < yesterdayDataList.size(); i++) {
                Map<String, Object> data1 = yesterdayDataList.get(i);
                data1.put("currentRainfall", null);
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
