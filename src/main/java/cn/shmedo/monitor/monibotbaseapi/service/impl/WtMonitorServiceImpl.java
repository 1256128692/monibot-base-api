package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.monitor.monibotbaseapi.cache.DataUnitCache;
import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WaterQuality;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorItemFieldResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHistoryAvgDataResponse;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.MonitorTypeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.sensor.SensorDataUtil;
import cn.shmedo.monitor.monibotbaseapi.util.waterQuality.WaterQualityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.windPower.WindPowerUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        if (!CollectionUtil.isNullOrEmpty(pa.getProjectIDList())) {
            wrapper.in(TbProjectInfo::getID, pa.getProjectIDList());
        }
        // 1.项目信息列表
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectList(wrapper);
        if (CollectionUtil.isNullOrEmpty(tbProjectInfos)) {
            return Collections.emptyList();
        }
        // 只查看已经配置过的监测项目
        List<Integer> projectIDList = tbProjectInfos.stream().map(TbProjectInfo::getID).collect(Collectors.toList());
        LambdaQueryWrapper<TbProjectMonitorClass> pmcWrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                .eq(TbProjectMonitorClass::getMonitorClass, pa.getMonitorClassType())
                .eq(TbProjectMonitorClass::getEnable, true)
                .in(TbProjectMonitorClass::getProjectID, projectIDList);
        List<TbProjectMonitorClass> tbProjectMonitorClassList = tbProjectMonitorClassMapper.selectList(pmcWrapper);
        if (CollectionUtil.isNullOrEmpty(tbProjectMonitorClassList)) {
            return Collections.emptyList();
        }
        List<Integer> configProjectIDList = tbProjectMonitorClassList.stream().map(TbProjectMonitorClass::getProjectID).collect(Collectors.toList());

        // 2.监测点信息列表
        List<MonitorPointAndItemInfo> tbMonitorPoints = tbMonitorPointMapper.selectListByCondition(configProjectIDList, pa.getMonitorType()
                , pa.getMonitorItemID(), pa.getMonitorClassType(), pa.getMonitorItemName());
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
        // 根据传感器ID列表和传感器类型,查传感器最新数据
        if (!CollectionUtil.isNullOrEmpty(sensorIDList)) {
            fieldList = getFieldSelectInfoListFromModleTypeFieldList(tbMonitorTypeFields);
            // 3. 传感器数据列表
            maps = sensorDataDao.querySensorNewData(sensorIDList, fieldList, false, monitorType);
            // 处理需要计算的监测子类型返回token
            fieldList = MonitorTypeUtil.handlefieldList(monitorType, fieldList);
        } else {
            fieldList = null;
            maps = null;
        }

        // 最终将传感器数据封装结果集
        List<FieldSelectInfo> finalFieldList = fieldList;

        return handleFinalResultInfo(sensorNewDataInfoList, maps, tbSensors, finalFieldList, dataUnitsMap);
    }

    /**
     * @param monitorType 监测类型
     * @param maps        传感器数据列表
     * @param density     密度
     */
    private void handleSpecialSensorDataList(Integer monitorType,
                                             List<Map<String, Object>> maps,
                                             String density, String key, String typeKey) {
        if (CollectionUtil.isNullOrEmpty(maps)) {
            return;
        }
        if (monitorType.equals(MonitorType.STRESS.getKey())
                || monitorType.equals(MonitorType.PRESSURE.getKey())
                || monitorType.equals(MonitorType.ONE_DIMENSIONAL_DISPLACEMENT.getKey())
                || monitorType.equals(MonitorType.THREE_DIMENSIONAL_DISPLACEMENT.getKey())
                || monitorType.equals(MonitorType.INTERNAL_TRIAXIAL_DISPLACEMENT.getKey())) {
            if (StringUtil.isNullOrEmpty(density)) {
                return;
            }

//            if (monitorType.equals(MonitorType.LEVEL.getKey()) || monitorType.equals(MonitorType.WATER_LEVEL.getKey())) {
//                key = "levelChange";
//                typeKey = "distance";
//            }


            if (monitorType.equals(MonitorType.STRESS.getKey())) {
                key = "strainPeriodDisp";
                typeKey = "strain";
            } else if (monitorType.equals(MonitorType.PRESSURE.getKey())) {
                key = "pressurePeriodDisp";
                typeKey = "pressure";
            } else if (monitorType.equals(MonitorType.ONE_DIMENSIONAL_DISPLACEMENT.getKey())) {
                key = "xPeriodDisp";
                typeKey = "xTotalDisp";
            }


            // 处理变化值,规则当前的数据减去上一笔时间的数据.暂不删除,需求可能会变回来
//            for (int i = 0; i < maps.size(); i++) {
//                Map<String, Object> map = maps.get(i);
//                String timeStr = (String) map.get("time");
//                long time = DateUtil.parse(timeStr).getTime();
//
//                long lastDataTime = 0L;
//                if (density.endsWith("h")) {
//                    // 小时为单位
//                    String hour = density.replaceAll("h", "");
//                    Integer hourInt = Integer.valueOf(hour);
//                    lastDataTime = time - hourInt * 60 * 60 * 1000;
//                } else {
//                    // 分钟为单位
//                    String minute = density.replaceAll("m", "");
//                    Integer minuteInt = Integer.valueOf(minute);
//                    lastDataTime = time - minuteInt * 60 * 1000;
//                }
//
//                // 在之前的Map对象中查找相应的v1值
//                for (int j = 0; j < maps.size(); j++) {
//                    Map<String, Object> prevMap = maps.get(j);
//                    String prevTimeStr = (String) prevMap.get("time");
//                    long prevTime = DateUtil.parse(prevTimeStr).getTime();
//                    if (prevTime == lastDataTime && prevMap.get("sensorID").equals(map.get("sensorID"))) {
//                        // 如果之前的Map对象中存在v1字段，则将其作为twoHoursAgo时刻的v1值
//                        if (prevMap.containsKey(typeKey)) {
//                            double v1TwoHoursAgo = (double) prevMap.get(typeKey);
//                            // 计算stressChange并添加到当前Map对象中
//                            double changeValue = (double) map.get(typeKey) - v1TwoHoursAgo;
//                            BigDecimal changeBD = new BigDecimal(changeValue);
//                            BigDecimal rounded = changeBD.setScale(2, BigDecimal.ROUND_HALF_UP);
//                            map.put(key, rounded);
//                            break;
//                        }
//                    }
//                }
//            }


            Map<Integer, List<Map<String, Object>>> groupedMapsBySensorID = maps.stream()
                    .collect(Collectors.groupingBy(map -> (Integer) map.get("sensorID")));

            for (List<Map<String, Object>> sensorMaps : groupedMapsBySensorID.values()) {
                sensorMaps.sort(Comparator.comparing(map -> (String) map.get("time"), Comparator.reverseOrder()));

                for (int i = 0; i < sensorMaps.size() - 1; i++) {
                    Map<String, Object> currentMap = sensorMaps.get(i);
                    Map<String, Object> nextMap = sensorMaps.get(i + 1);

                    double currentStrain = (double) currentMap.get(typeKey);
                    double nextStrain = (double) nextMap.get(typeKey);

                    double periodDisp = currentStrain - nextStrain;
                    BigDecimal rounded = BigDecimal.valueOf(periodDisp).setScale(2, BigDecimal.ROUND_HALF_UP);
                    periodDisp = rounded.doubleValue();

                    currentMap.put(key, periodDisp);
                }

            }

        }
    }


    /**
     * @param sensorNewDataInfoList 最终返回信息
     * @param maps                  传感器数据列表
     * @param tbSensors             传感器基本信息
     * @param finalFieldList        监测类型子类型列表
     * @return
     */
    private List<SensorNewDataInfo> handleFinalResultInfo(List<SensorNewDataInfo> sensorNewDataInfoList,
                                                          List<Map<String, Object>> maps,
                                                          List<TbSensor> tbSensors,
                                                          List<FieldSelectInfo> finalFieldList,
                                                          Map<Integer, TbDataUnit> dataUnitsMap) {

        Collection<Object> areas = sensorNewDataInfoList
                .stream().map(SensorNewDataInfo::getLocationInfo).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
        areas.clear();

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
                                    da.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(tbSensor.getConfigFieldValue()).getByPath("$.埋深"));
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
                                currentSensorData.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(tbSensor.getConfigFieldValue()).getByPath("$.埋深"));
                            }
                            snd.setSensorData(handleSpecialType(tbSensor.getMonitorType(), currentSensorData));
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
     * 比如:水质,风力,水位
     *
     * @param monitorType
     * @param currentSensorData
     * @return
     */
    private Map<String, Object> handleSpecialType(Integer monitorType, Map<String, Object> currentSensorData) {

        // 水质
        if (monitorType.equals(MonitorType.WATER_QUALITY.getKey())) {
            Object phosphorusTotal = currentSensorData.get("phosphorusTotal");
            Object temperature = currentSensorData.get("temperature");
            if (ObjectUtil.isNotNull(phosphorusTotal)) {
                // 河道水位,校验水质规则,[PH、溶解氧、高锰酸盐指数、氨氮、总磷](v1,v3,v6,v7,v8),抉择出水质等级最差的
                int v1 = WaterQualityUtil.getV1Category((Double) currentSensorData.get("ph"));
                int v3 = WaterQualityUtil.getV3Category((Double) currentSensorData.get("dissolvedOxygen"));
                int v6 = WaterQualityUtil.getV6Category((Double) currentSensorData.get("homomethylateIndex"));
                int v7 = WaterQualityUtil.getV7Category((Double) currentSensorData.get("ammoniaNitrogen"));
                int v8 = WaterQualityUtil.getV8Category((Double) currentSensorData.get("phosphorusTotal"));
                List<Integer> levelList = new LinkedList<>(List.of(v1, v3, v6, v7, v8));
                int maxCategory = WaterQualityUtil.getMaxCategory(levelList);
                currentSensorData.put("waterQuality", WaterQuality.getValueByKey(maxCategory));

            } else if (ObjectUtil.isNotNull(temperature)) {
                // 水库水位,校验水质规则 ,含溶解氧(v3)
                int v3 = WaterQualityUtil.getV3Category((Double) currentSensorData.get("dissolvedOxygen"));
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
            if (modelField.getFieldClass().equals(FieldClass.EXTEND_CONFIG.getCode())) {
                return;
            }
            FieldSelectInfo fieldSelectInfo = new FieldSelectInfo();
            fieldSelectInfo.setFieldToken(modelField.getFieldToken());
            fieldSelectInfo.setFieldName(modelField.getFieldName());
            fieldSelectInfo.setFieldOrder(modelField.getDisplayOrder());
            fieldSelectInfo.setFieldType(FieldType.valueOfString(modelField.getFieldDataType()));
            fieldSelectInfo.setFieldStatisticsType(modelField.getFieldClass().toString());
//            fieldSelectInfo.setFieldJsonPath(modelField.getFieldJsonPath());
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
        List<MonitorPointAndItemInfo> tbMonitorPoints = tbMonitorPointMapper.selectListByCondition(Arrays.asList(pa.getProjectID()), null,
                null, null, null);
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
            } else {
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

        List<TbSensor> sensorList = tbSensorMapper.selectStatisticsCountByCompanyID(pa.getCompanyID(), pa.getQueryType());
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
        List<Integer> monitorItemIDList = monitorItemList.stream().map(MonitorItemBaseInfo::getMonitorItemID).collect(Collectors.toList());

        MonitorPointTypeStatisticsInfo vo = new MonitorPointTypeStatisticsInfo();
        List<MonitorTypeBaseInfo> monitorTypeBaseInfos = tbMonitorTypeMapper.selectMonitorBaseInfo(monitorItemIDList);
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
                item.setWarnInfo(WarnInfo.toBuliderNewVo(sensorList.stream()
                        .filter(pojo -> pojo.getMonitorType().equals(item.getMonitorType()))
                        .collect(Collectors.toList())));
            }
            if (!CollectionUtil.isNullOrEmpty(monitorItemList)) {
                // 监测项目信息列表
                List<MonitorItemBaseInfo> list = monitorItemList.stream().filter(pojo -> pojo.getMonitorType().equals(item.getMonitorType())).collect(Collectors.toList());
                item.setMonitorItemList(list);
            }
        });
        List<MonitorTypeBaseInfo> filteredList = monitorTypeBaseInfos.stream()
                .filter(monitorTypeBaseInfo -> monitorTypeBaseInfo.getMonitorItemList() != null
                        && !monitorTypeBaseInfo.getMonitorItemList().isEmpty())
                .collect(Collectors.toList());
        vo.setTypeInfoList(filteredList);
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
                Map<String, Object> stringObjectMap = handleSpecialType(pa.getTbMonitorPoint().getMonitorType(), map);
                resultMaps.add(stringObjectMap);
            });
        }
        // 处理需要计算的监测子类型返回token
        MonitorTypeUtil.handleHistoryDatafieldList(pa.getTbMonitorPoint().getMonitorType(), fieldList);
        if (pa.getTbMonitorPoint().getMonitorType().equals(MonitorType.THREE_DIMENSIONAL_DISPLACEMENT.getKey())) {
            handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), "xPeriodDisp", "xTotalDisp");
            handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), "yPeriodDisp", "yTotalDisp");
            handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), "zPeriodDisp", "zTotalDisp");
        } else {
            handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), null, null);
        }

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
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(), fieldList, dataUnitsMap);

        return new MonitorPointHistoryData(pa.getTbMonitorPoint(), tbSensors, maps, fieldList, tbDataUnitList);
    }


    @Override
    public MonitorPointAllInfo queryMonitorPointBaseInfoList(QueryMonitorPointBaseInfoListParam pa) {

        MonitorPointAllInfo vo = new MonitorPointAllInfo();
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectListByProjectIDAndMonitorClass(pa.getProjectID(), pa.getMonitorClass());
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
                    .in(TbMonitorItem::getID, monitorItemIDs)
                    .eq(TbMonitorItem::getMonitorClass, pa.getMonitorClass());
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
        List<Map<String, Object>> dailyRainfallList = null;
        if (!CollectionUtil.isNullOrEmpty(maps)) {
            // 处理雨量历史时间段的降雨量
            resultList = handleDataOrder(maps, pa.getEnd(), pa.getDensity());
            // 处理雨量历史时间段的当前雨量
//            handleRainTypeSensorHistoryDataList(resultList, pa.getBegin(), pa.getEnd());
            // 处理日降雨量
            dailyRainfallList = handleDailyRainfallList(maps);
            if (!CollectionUtil.isNullOrEmpty(dailyRainfallList)) {
                if (dailyRainfallList.size() == 1) {
                    if (dailyRainfallList.get(0).get(DbConstant.DAILY_RAINFALL) != null) {
                        dailyRainfall = (Double) dailyRainfallList.get(0).get(DbConstant.DAILY_RAINFALL);
                    }
                }
            }
        }
        // 处理数据单位
        List<TbDataUnit> tbDataUnitList = handleDataUnit(pa.getTbMonitorPoint().getMonitorType(), fieldList, dataUnitsMap);

        return new RainMonitorPointHistoryData(pa.getTbMonitorPoint(), tbSensors, resultList, fieldList, tbDataUnitList, dailyRainfall, dailyRainfallList);
    }

    private List<Map<String, Object>> handleDailyRainfallList(List<Map<String, Object>> maps) {

        // 操作按日期分组
        Map<LocalDate, List<Map<String, Object>>> groupedMaps = maps.stream()
                .collect(Collectors.groupingBy(map -> LocalDateTime.parse((String) map.get("time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate()));

        // 遍历每一天的数据，生成新的newMaps列表
        List<Map<String, Object>> newMaps = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Map<String, Object>>> entry : groupedMaps.entrySet()) {
            LocalDate date = entry.getKey();
            List<Map<String, Object>> dailyMaps = entry.getValue();

            double dailyRainfall = (double) dailyMaps.get(0).get("dailyRainfall");

            Map<String, Object> newMap = new HashMap<>();
            newMap.put("time", LocalDateTime.of(date, LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            newMap.put("sensorID", dailyMaps.get(0).get("sensorID"));
            newMap.put("dailyRainfall", dailyRainfall);
            newMaps.add(newMap);
        }

//        List<Map<String, Object>> dailyRainData = sensorDataDao.querySensorDailyRainData(sensorIDList,
//                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()));
        return newMaps;
    }

    /**
     * 遍历 dataList，所有数据的时间都根据密度往后挪
     * 比如2h,就是挪2个小时
     *
     * @param dataList
     * @param end
     * @param density
     */
    private List<Map<String, Object>> handleDataOrder(List<Map<String, Object>> dataList, Timestamp end, String density) {
        List<Map<String, Object>> result = new LinkedList<>();
        if (StringUtil.isNullOrEmpty(density)) {
            return dataList;
        }
        long densityTime = 0L;
        if (density.endsWith("h")) {
            // 小时为单位
            String hour = density.replaceAll("h", "");
            Integer hourInt = Integer.valueOf(hour);
            densityTime = hourInt * 60 * 60 * 1000;
        } else {
            // 分钟为单位
            String minute = density.replaceAll("m", "");
            Integer minuteInt = Integer.valueOf(minute);
            densityTime = minuteInt * 60 * 1000;
        }

        long finalDensityTime = densityTime;
        dataList.forEach(data -> {
            String timeStr = (String) data.get("time");
            long timeMillis = DateUtil.parse(timeStr).getTime();
            timeMillis += finalDensityTime; // Add 2 hours
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
            Map<String, Object> stringObjectMap = handleSpecialType(pa.getMonitorType(), map);
            resultMaps.add(stringObjectMap);
        });

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
        Optional<FieldSelectInfo> cOriginal = fieldList.stream().filter(pojo -> pojo.getFieldToken().equals("cOriginal")).findFirst();
        cOriginal.get().setFieldName("C轴累加位移");

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
        MonitorTypeUtil.handleHistoryDatafieldList(pa.getTbMonitorPoint().getMonitorType(), fieldList);
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), "aPeriodDisp", "aAccu");
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), "bPeriodDisp", "bAccu");
        handleSpecialSensorDataList(pa.getTbMonitorPoint().getMonitorType(), resultMaps, pa.getDensity(), "cPeriodDisp", "cOriginal");

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
        List<MonitorPointAndItemInfo> tbMonitorPoints = tbMonitorPointMapper.selectListByCondition(projectIDList,
                pa.getMonitorType(), pa.getMonitorItemID(), pa.getMonitorClassType(), pa.getMonitorItemName());
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return Collections.emptyList();
        }
        return buildTDProjectAndMonitorAndSensorInfo(tbProjectInfos, tbMonitorPoints, pa.getMonitorType());
    }


    @Override
    public MonitorItemFieldResponse queryMonitorItemFieldList(QueryMonitorItemFieldListParam pa) {
        Map<Integer, TbDataUnit> dataUnitsMap = DataUnitCache.dataUnitsMap;
        Integer monitorPointID = pa.getMonitorPointIDList().get(0);

        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);

        List<TbDataUnit> tbDataUnitList = new LinkedList<>();
        if (!CollectionUtil.isNullOrEmpty(monitorTypeFields)) {
            List<Integer> dataUnitIDList = monitorTypeFields.stream().map(TbMonitorTypeField::getFieldUnitID).collect(Collectors.toList());
            dataUnitsMap.entrySet().forEach(entry -> {
                if (dataUnitIDList.contains(entry.getKey())) {
                    tbDataUnitList.add(entry.getValue());
                }
            });
        }

        List<Integer> monitorTypeList = monitorTypeFields.stream().map(TbMonitorTypeField::getMonitorType).collect(Collectors.toList());
        if (monitorTypeList.contains(MonitorType.WT_RAINFALL.getKey())) {
            TbMonitorTypeField vo = new TbMonitorTypeField();
            vo.setFieldToken("rainfall");
            vo.setFieldUnitID(1);
            vo.setFieldClass(2);
            vo.setMonitorType(MonitorType.WT_RAINFALL.getKey());
            vo.setFieldName("降雨量");
            vo.setFieldDataType("Double");
            monitorTypeFields.add(vo);
        }

        return new MonitorItemFieldResponse(monitorTypeFields, tbDataUnitList);
    }

    @Override
    public List<SensorHistoryAvgDataResponse> queryMonitorPointHistoryAvgDataList(QueryMonitorPointHistoryAvgDataParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListByMonitorPointIDsAndProjectIDs(pa.getMonitorPointIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return Collections.emptyList();
        }

        List<Integer> sensorIDList = sensorHistoryAvgDataResponseList.stream().map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
        Integer monitorPointID = sensorHistoryAvgDataResponseList.get(0).getMonitorPointID();
        Integer monitorType = sensorHistoryAvgDataResponseList.get(0).getMonitorType();
        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
        List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(sensorIDList, monitorTypeFields, pa.getBegin(), pa.getEnd(), pa.getDensity(), monitorType);

        // 处理传感器数据月平均值,年平均值
        List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                monitorTypeFields, sensorHistoryAvgDataResponseList);

        // 正序
        return responseList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime))
                .collect(Collectors.toList());
    }


    @Override
    public PageUtil.PageWithMap<SensorHistoryAvgDataResponse> queryMonitorPointHistoryAvgDataPage(QueryMonitorPointHistoryAvgDataPageParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListByMonitorPointIDsAndProjectIDs(pa.getMonitorPointIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return PageUtil.PageWithMap.empty();
        }

        List<Integer> sensorIDList = sensorHistoryAvgDataResponseList.stream().map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
        Integer monitorPointID = sensorHistoryAvgDataResponseList.get(0).getMonitorPointID();
        Integer monitorType = sensorHistoryAvgDataResponseList.get(0).getMonitorType();
        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
        List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(sensorIDList, monitorTypeFields, pa.getBegin(), pa.getEnd(), pa.getDensity(), monitorType);

        // 处理传感器数据月平均值,年平均值
        List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                monitorTypeFields, sensorHistoryAvgDataResponseList);

        // 时间倒序
        List<SensorHistoryAvgDataResponse> responses = responseList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime).reversed())
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(responses)) {
            return PageUtil.PageWithMap.empty();
        }
        PageUtil.Page<SensorHistoryAvgDataResponse> page = PageUtil.page(responses, pa.getPageSize(), pa.getCurrentPage());
        return new PageUtil.PageWithMap<SensorHistoryAvgDataResponse>(page.totalPage(), page.currentPageData(), page.totalCount(), null);
    }

    @Override
    public List<SensorHistoryAvgDataResponse> querySensorHistoryAvgDataList(QuerySensorHistoryAvgDataParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListBySensorIDsAndProjectIDs(pa.getSensorIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return Collections.emptyList();
        }

        List<Integer> sensorIDList = sensorHistoryAvgDataResponseList.stream().map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
        Integer monitorPointID = sensorHistoryAvgDataResponseList.get(0).getMonitorPointID();
        Integer monitorType = sensorHistoryAvgDataResponseList.get(0).getMonitorType();
        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
        List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(sensorIDList, monitorTypeFields, pa.getBegin(), pa.getEnd(), pa.getDensity(), monitorType);

        // 处理传感器数据月平均值,年平均值
        List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                monitorTypeFields, sensorHistoryAvgDataResponseList);

        // 正序
        return responseList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime))
                .collect(Collectors.toList());
    }

    @Override
    public PageUtil.PageWithMap<SensorHistoryAvgDataResponse> querySensorHistoryAvgDataPage(QuerySensorHistoryAvgDataPageParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListBySensorIDsAndProjectIDs(pa.getSensorIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return PageUtil.PageWithMap.empty();
        }

        List<Integer> sensorIDList = sensorHistoryAvgDataResponseList.stream().map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
        Integer monitorPointID = sensorHistoryAvgDataResponseList.get(0).getMonitorPointID();
        Integer monitorType = sensorHistoryAvgDataResponseList.get(0).getMonitorType();
        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
        List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(sensorIDList, monitorTypeFields, pa.getBegin(), pa.getEnd(), pa.getDensity(), monitorType);

        // 处理传感器数据月平均值,年平均值
        List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                monitorTypeFields, sensorHistoryAvgDataResponseList);

        // 时间倒序
        List<SensorHistoryAvgDataResponse> responses = responseList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime).reversed())
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(responses)) {
            return PageUtil.PageWithMap.empty();
        }
        PageUtil.Page<SensorHistoryAvgDataResponse> page = PageUtil.page(responses, pa.getPageSize(), pa.getCurrentPage());
        return new PageUtil.PageWithMap<SensorHistoryAvgDataResponse>(page.totalPage(), page.currentPageData(), page.totalCount(), null);
    }

    @Override
    public List<SensorHistoryAvgDataResponse> queryRainPointHistorySumDataList(QueryRainPointHistorySumDataParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListByMonitorPointIDsAndProjectIDs(pa.getMonitorPointIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return Collections.emptyList();
        }

        List<Integer> sensorIDList = sensorHistoryAvgDataResponseList.stream().map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
        Integer monitorPointID = sensorHistoryAvgDataResponseList.get(0).getMonitorPointID();
        Integer monitorType = sensorHistoryAvgDataResponseList.get(0).getMonitorType();
        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
        List<Map<String, Object>> dataList = sensorDataDao.queryRainSensorHistorySumData(sensorIDList, monitorTypeFields, pa.getBegin(), pa.getEnd(), pa.getDensity(), monitorType);

        // 处理传感器数据月累加值,年累加值
        List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleRainDataList(dataList, pa.getDensity(),
                monitorTypeFields, sensorHistoryAvgDataResponseList);

        // 正序
        return responseList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime))
                .collect(Collectors.toList());
    }


    @Override
    public PageUtil.PageWithMap<SensorHistoryAvgDataResponse> queryRainPointHistorySumDataPage(QueryRainPointHistorySumDataPageParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListByMonitorPointIDsAndProjectIDs(pa.getMonitorPointIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return PageUtil.PageWithMap.empty();
        }

        List<Integer> sensorIDList = sensorHistoryAvgDataResponseList.stream().map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
        Integer monitorPointID = sensorHistoryAvgDataResponseList.get(0).getMonitorPointID();
        Integer monitorType = sensorHistoryAvgDataResponseList.get(0).getMonitorType();
        List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
        List<Map<String, Object>> dataList = sensorDataDao.queryRainSensorHistorySumData(sensorIDList, monitorTypeFields, pa.getBegin(), pa.getEnd(), pa.getDensity(), monitorType);

        // 处理传感器数据月累加值,年累加值
        List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleRainDataList(dataList, pa.getDensity(),
                monitorTypeFields, sensorHistoryAvgDataResponseList);

        // 时间倒序
        List<SensorHistoryAvgDataResponse> responses = responseList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime).reversed())
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(responses)) {
            return PageUtil.PageWithMap.empty();
        }

        PageUtil.Page<SensorHistoryAvgDataResponse> page = PageUtil.page(responses, pa.getPageSize(), pa.getCurrentPage());
        return new PageUtil.PageWithMap<SensorHistoryAvgDataResponse>(page.totalPage(), page.currentPageData(), page.totalCount(), null);
    }

    @Override
    public List<SensorHistoryAvgDataResponse> queryWaterRainSensorHistoryAvgDataList(QueryWaterRainSensorHistoryAvgDataParam pa) {
        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListByMonitorPointIDsAndProjectIDs(pa.getMonitorPointIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return Collections.emptyList();
        }

        Map<Integer, List<SensorHistoryAvgDataResponse>> listMap = sensorHistoryAvgDataResponseList.stream().collect(Collectors.groupingBy(SensorHistoryAvgDataResponse::getMonitorType));

        List<SensorHistoryAvgDataResponse> resultList = new LinkedList<SensorHistoryAvgDataResponse>();
        List<SensorHistoryAvgDataResponse> waterLevelList = listMap.get(MonitorType.WATER_LEVEL.getKey());
        List<SensorHistoryAvgDataResponse> flowVelocityList = listMap.get(MonitorType.FLOW_VELOCITY.getKey());
        List<SensorHistoryAvgDataResponse> rainList = listMap.get(MonitorType.WT_RAINFALL.getKey());

        // 水位类型
        if (!CollectionUtil.isNullOrEmpty(waterLevelList)) {
            List<Integer> waterLevelSensorIDList = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getMonitorType().equals(MonitorType.WATER_LEVEL.getKey()))
                    .map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
            Integer monitorPointID = waterLevelList.get(0).getMonitorPointID();
            List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
            List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(waterLevelSensorIDList, monitorTypeFields,
                    pa.getBegin(), pa.getEnd(), pa.getDensity(), MonitorType.WATER_LEVEL.getKey());
            List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                    monitorTypeFields, waterLevelList);
            resultList.addAll(responseList);
        }
        // 流速类型
        if (!CollectionUtil.isNullOrEmpty(flowVelocityList)) {
            List<Integer> waterLevelSensorIDList = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getMonitorType().equals(MonitorType.FLOW_VELOCITY.getKey()))
                    .map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
            Integer monitorPointID = flowVelocityList.get(0).getMonitorPointID();
            List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
            List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(waterLevelSensorIDList, monitorTypeFields,
                    pa.getBegin(), pa.getEnd(), pa.getDensity(), MonitorType.FLOW_VELOCITY.getKey());
            List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                    monitorTypeFields, flowVelocityList);
            resultList.addAll(responseList);
        }
        // 雨量类型
        if (!CollectionUtil.isNullOrEmpty(rainList)) {
            List<Integer> waterLevelSensorIDList = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getMonitorType().equals(MonitorType.WT_RAINFALL.getKey()))
                    .map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
            Integer monitorPointID = rainList.get(0).getMonitorPointID();
            List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
            List<Map<String, Object>> dataList = sensorDataDao.queryRainSensorHistorySumData(waterLevelSensorIDList, monitorTypeFields,
                    pa.getBegin(), pa.getEnd(), pa.getDensity(), MonitorType.WT_RAINFALL.getKey());
            List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleRainDataList(dataList, pa.getDensity(),
                    monitorTypeFields, rainList);
            resultList.addAll(responseList);
        }

        // 时间倒序
        List<SensorHistoryAvgDataResponse> responses = resultList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime))
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(responses)) {
            return Collections.emptyList();
        }

        return responses;
    }


    @Override
    public PageUtil.PageWithMap<SensorHistoryAvgDataResponse> queryWaterRainSensorHistoryAvgDataPage(QueryWaterRainSensorHistoryAvgDataPageParam pa) {

        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList = tbSensorMapper.selectListByMonitorPointIDsAndProjectIDs(pa.getMonitorPointIDList(), pa.getProjectIDList());
        if (CollectionUtil.isNullOrEmpty(sensorHistoryAvgDataResponseList)) {
            return PageUtil.PageWithMap.empty();
        }

        Map<Integer, List<SensorHistoryAvgDataResponse>> listMap = sensorHistoryAvgDataResponseList.stream().collect(Collectors.groupingBy(SensorHistoryAvgDataResponse::getMonitorType));

        List<SensorHistoryAvgDataResponse> resultList = new LinkedList<SensorHistoryAvgDataResponse>();
        List<SensorHistoryAvgDataResponse> waterLevelList = listMap.get(MonitorType.WATER_LEVEL.getKey());
        List<SensorHistoryAvgDataResponse> flowVelocityList = listMap.get(MonitorType.FLOW_VELOCITY.getKey());
        List<SensorHistoryAvgDataResponse> rainList = listMap.get(MonitorType.WT_RAINFALL.getKey());

        // 水位类型
        if (!CollectionUtil.isNullOrEmpty(waterLevelList)) {
            List<Integer> waterLevelSensorIDList = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getMonitorType().equals(MonitorType.WATER_LEVEL.getKey()))
                    .map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
            Integer monitorPointID = waterLevelList.get(0).getMonitorPointID();
            List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
            List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(waterLevelSensorIDList, monitorTypeFields,
                    pa.getBegin(), pa.getEnd(), pa.getDensity(), MonitorType.WATER_LEVEL.getKey());
            List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                    monitorTypeFields, waterLevelList);
            resultList.addAll(responseList);
        }
        // 流速类型
        if (!CollectionUtil.isNullOrEmpty(flowVelocityList)) {
            List<Integer> waterLevelSensorIDList = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getMonitorType().equals(MonitorType.FLOW_VELOCITY.getKey()))
                    .map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
            Integer monitorPointID = flowVelocityList.get(0).getMonitorPointID();
            List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
            List<Map<String, Object>> dataList = sensorDataDao.querySensorHistoryAvgData(waterLevelSensorIDList, monitorTypeFields,
                    pa.getBegin(), pa.getEnd(), pa.getDensity(), MonitorType.FLOW_VELOCITY.getKey());
            List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleDataList(dataList, pa.getDensity(),
                    monitorTypeFields, flowVelocityList);
            resultList.addAll(responseList);
        }
        // 雨量类型
        if (!CollectionUtil.isNullOrEmpty(rainList)) {
            List<Integer> waterLevelSensorIDList = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getMonitorType().equals(MonitorType.WT_RAINFALL.getKey()))
                    .map(SensorHistoryAvgDataResponse::getSensorID).collect(Collectors.toList());
            Integer monitorPointID = rainList.get(0).getMonitorPointID();
            List<TbMonitorTypeField> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByMonitorID(monitorPointID);
            List<Map<String, Object>> dataList = sensorDataDao.queryRainSensorHistorySumData(waterLevelSensorIDList, monitorTypeFields,
                    pa.getBegin(), pa.getEnd(), pa.getDensity(), MonitorType.WT_RAINFALL.getKey());
            List<SensorHistoryAvgDataResponse> responseList = SensorDataUtil.handleRainDataList(dataList, pa.getDensity(),
                    monitorTypeFields, rainList);
            resultList.addAll(responseList);
        }

        // 时间倒序
        List<SensorHistoryAvgDataResponse> responses = resultList.stream()
                .sorted(Comparator.comparing(SensorHistoryAvgDataResponse::getTime).reversed())
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(responses)) {
            return PageUtil.PageWithMap.empty();
        }

        PageUtil.Page<SensorHistoryAvgDataResponse> page = PageUtil.page(responses, pa.getPageSize(), pa.getCurrentPage());
        return new PageUtil.PageWithMap<SensorHistoryAvgDataResponse>(page.totalPage(), page.currentPageData(), page.totalCount(), null);
    }


    private List<TriaxialDisplacementSensorNewDataInfo> buildTDProjectAndMonitorAndSensorInfo(List<TbProjectInfo> tbProjectInfos, List<MonitorPointAndItemInfo> tbMonitorPoints, Integer monitorType) {

        List<TriaxialDisplacementSensorNewDataInfo> sensorNewDataInfoList = new LinkedList<>();
        // 获取项目类型(方式缓存)
        Map<Byte, TbProjectType> projectTypeMap = ProjectTypeCache.projectTypeMap;
        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;

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
            Double xDeep = null;
            if (!StringUtil.isNullOrEmpty(s1)) {
                xDeep = Double.parseDouble(JSONUtil.parseObj(s1).getByPath("$.埋深").toString());
            }

            // 找到最大的 v3 和对应的深度
            Optional<Map<String, Object>> maxV3Data = newDataList.stream()
                    .max(Comparator.comparingDouble(data -> (Double) data.get("bAccu")));
            Integer sensorIDByV3 = maxV3Data.map(data -> (Integer) data.get("sensorID")).orElse(0);
            String s2 = pojo.getSensorList().stream().filter(s -> s.getID().equals(sensorIDByV3)).map(TbSensor::getConfigFieldValue).findFirst().orElse(null);
            Double yDeep = null;
            if (!StringUtil.isNullOrEmpty(s2)) {
                yDeep = Double.parseDouble(JSONUtil.parseObj(s2).getByPath("$.埋深").toString());
            }

            // 找到最大的 v5 和对应的深度
            Optional<Map<String, Object>> maxV5Data = newDataList.stream()
                    .max(Comparator.comparingDouble(data -> (Double) data.get("cOriginal")));
            Integer sensorIDByV5 = maxV5Data.map(data -> (Integer) data.get("sensorID")).orElse(0);
            String s3 = pojo.getSensorList().stream().filter(s -> s.getID().equals(sensorIDByV5)).map(TbSensor::getConfigFieldValue).findFirst().orElse(null);
            Double zDeep = null;
            if (!StringUtil.isNullOrEmpty(s3)) {
                zDeep = Double.parseDouble(JSONUtil.parseObj(s3).getByPath("$.埋深").toString());
            }

            // 构建新的数据集
            Map<String, Object> newData = new HashMap<>();
            newData.put("xValue", maxV1Data.map(data -> data.get("aAccu")).orElse(null));
            newData.put("yValue", maxV3Data.map(data -> data.get("bAccu")).orElse(null));
            newData.put("zValue", maxV5Data.map(data -> data.get("cOriginal")).orElse(null));
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
            map.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(sensor.getConfigFieldValue()).getByPath("$.埋深"));
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
            Double rainfall = Double.parseDouble(dataList.get(i).get("rainfall").toString());
            BigDecimal rounded = new BigDecimal(rainfall).setScale(2, BigDecimal.ROUND_HALF_UP);
            dataList.get(i).put("rainfall", rounded);
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
                    BigDecimal finalResult = result.setScale(2, BigDecimal.ROUND_HALF_UP);
                    todayDataList.get(i).put("currentRainfall", finalResult);
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
            map.put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(sensor.getConfigFieldValue()).getByPath("$.埋深"));
        }
        return map;
    }


}
