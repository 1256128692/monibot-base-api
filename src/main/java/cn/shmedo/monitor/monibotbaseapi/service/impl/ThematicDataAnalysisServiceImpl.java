package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.thematicDataAnalysis.DmParamDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ConfigBaseResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.service.IThematicDataAnalysisService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicFieldToken.*;
import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicEigenValueName.*;

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
    private final TbProjectConfigMapper tbProjectConfigMapper;
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final TbSensorMapper tbSensorMapper;
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;
    private final TbEigenValueMapper tbEigenValueMapper;
    private final FileService fileService;

    @Override
    @Deprecated
    public StThematicAnalysisInfo queryStGroupRealData(QueryStDataParam param) {
        final TbMonitorGroup tbMonitorGroup = param.getTbMonitorGroup();
        final Integer monitorGroupID = param.getMonitorGroupID();
        final Integer monitorType = MonitorType.WATER_LEVEL.getKey();
        Map<Integer, Double> monitorPointIDUpperLimitMap = param.getMonitorPointIDUpperLimitMap();
        String absolutePath = Optional.ofNullable(tbMonitorGroup.getImagePath()).filter(ObjectUtil::isNotEmpty)
                .map(fileService::getFileUrl).orElse(null);
        String groupConfig = tbProjectConfigMapper.selectList(new LambdaQueryWrapper<TbProjectConfig>()
                        .eq(TbProjectConfig::getKey, "stConfig::" + monitorGroupID)
                        .eq(TbProjectConfig::getGroup, "monitorGroup")).stream().findFirst()
                .map(TbProjectConfig::getValue).orElse(null);
        Map<Integer, ConfigBaseResponse> pointIDConfigMap = tbProjectInfoMapper.selectMonitorGroupRelatePointConfig(
                        monitorGroupID, monitorType, param.getGroup(), param.getKey())
                .stream().collect(Collectors.groupingBy(ConfigBaseResponse::getTargetID)).values().stream()
                .map(u -> u.stream().findFirst().orElseThrow(() -> new RuntimeException("Unreachable Exception")))
                .collect(Collectors.toMap(ConfigBaseResponse::getTargetID, v -> v));
        // set upperLimit in 'newData.dataList' only while these map's keySet is the same(ensure all monitorPoint owned 'upperName').
        monitorPointIDUpperLimitMap = CollectionUtil.isEmpty(CollectionUtil.disjunction(
                pointIDConfigMap.keySet(), monitorPointIDUpperLimitMap.keySet())) ? monitorPointIDUpperLimitMap
                : new HashMap<>();
        List<Integer> damFrontSensorIDList = tbMonitorPointMapper.selectListByProjectIDsAndMonitorItemName(
                        List.of(tbMonitorGroup.getProjectID()), "坝前水位").stream()
                .map(MonitorPointWithSensor::getSensorList).flatMap(Collection::stream)
                .map(SensorBaseInfoResponse::getSensorID).toList();
        Map<Integer, Integer> sensorIDPointIDMap = pointIDConfigMap.values().stream().collect(Collectors.toMap(
                ConfigBaseResponse::getChildID, ConfigBaseResponse::getTargetID));
        return StThematicAnalysisInfo.builder().monitorPointGroupID(tbMonitorGroup.getID()).groupImage(absolutePath)
                .monitorPointGroupName(tbMonitorGroup.getName()).groupConfig(groupConfig)
                .newData(dealStNewData(damFrontSensorIDList, sensorIDPointIDMap, pointIDConfigMap, param, monitorType,
                        monitorPointIDUpperLimitMap))
                .avgData(dealStAvgData(damFrontSensorIDList, sensorIDPointIDMap, pointIDConfigMap, param, monitorType)).build();
    }

    //all the {@code segmentValue} of the first record in records would be set to zero
    //PM-@洪嘉一:(内部变形图表最新值显示问题)都不需要 只有浸润线会需要展示最新值
    @Override
    @Deprecated
    public DmThematicAnalysisInfo queryDmAnalysisData(QueryDmDataParam param) {
        DmParamDto dto = prepareDmDataParam(param);
        DmThematicAnalysisInfo res = DmThematicAnalysisInfo.builder().monitorPointID(dto.getMonitorPointID())
                .monitorPointName(dto.getMonitorPointName()).build();
        res.setHistoryData(CollectionUtil.isEmpty(dto.getSensorIDConfigMap()) ? new ArrayList<>()
                : dealDmHistoryData(param, dto));
        return res;
    }

    @Override
    @Deprecated
    public PageUtil.Page<DmThematicAnalysisPageInfo> queryDmAnalysisDataPage(QueryDmDataPageParam param) {
        DmParamDto dto = prepareDmDataParam(param);
        if (CollectionUtil.isEmpty(param.getDataList()) || CollectionUtil.isEmpty(dto.getSensorIDConfigMap())) {
            return PageUtil.page(new ArrayList<>(), param.getPageSize(), param.getCurrentPage());
        }
        return dealDmHistoryPageData(param, dto);
    }

    @Override
    @Deprecated
    public List<Date> queryDmPageDataList(QueryDmDataParam param) {
        DmParamDto dto = prepareDmDataParam(param);
        return CollectionUtil.isEmpty(dto.getSensorIDConfigMap()) ? new ArrayList<>()
                : dealDmHistoryData(param, dto).stream().map(u -> (Date) u.get(DbConstant.TIME_FIELD)).toList();
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

    @Override
    public List<ThematicGroupPointListInfo> queryThematicGroupPointList(QueryThematicGroupPointListParam param) {
        //(group,key,value) - (monitorGroup,原始key::123,value)
        List<ThematicGroupPointListInfo> dataList = tbMonitorPointMapper.selectThematicGroupPointList(param);
        Map<String, String> ossKeyFilePath = getAbsulatePathMap(dataList.stream().map(ThematicGroupPointListInfo::getMonitorGroupImagePath).toList());
        return dataList.stream().peek(u -> {
            String imagePath = u.getMonitorGroupImagePath();
            if (ObjectUtil.isNotEmpty(imagePath) && ossKeyFilePath.containsKey(imagePath)) {
                u.setMonitorGroupImagePath(ossKeyFilePath.get(imagePath));
            }
        }).toList();
    }

    @Override
    public List<ThematicQueryTransverseInfo> queryTransverseList(QueryTransverseListParam param) {
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, param.getInspectedPointIDList()));
        if (CollUtil.isEmpty(sensorList)) {
            return List.of();
        }
        String fieldToken = param.getFieldToken();
        Integer datumPointID = Optional.ofNullable(param.getDatumPoint()).map(DatumPointConfig::getMonitorPointID).orElse(null);
        Integer upper = Optional.ofNullable(param.getDatumPoint()).map(DatumPointConfig::getUpper).orElse(null);
        Integer lower = Optional.ofNullable(param.getDatumPoint()).map(DatumPointConfig::getLower).orElse(null);

        Map<Integer, String> pointIDNameMap = param.getTbMonitorPointList().stream().collect(Collectors.toMap(
                TbMonitorPoint::getID, TbMonitorPoint::getName));
        Map<Integer, Tuple<Integer, String>> sensorPointInfoMap = sensorList.stream().collect(Collectors.toMap(
                TbSensor::getID, u -> new Tuple<>(u.getMonitorPointID(), pointIDNameMap.get(u.getMonitorPointID()))));
        DisplayDensity densityType = DisplayDensity.fromValue(param.getDisplayDensity());
        List<Map<String, Object>> dataList = queryPointDataList(sensorList, densityType, StatisticalMethods.fromValue(param.getStatisticalMethod()),
                param.getStartTime(), param.getEndTime());
        SimpleDateFormat formatter = TimeUtil.DestinyFormatter.getFormatter(densityType);
        return dataList.stream().filter(u -> u.containsKey(DbConstant.TIME_FIELD))
                .collect(Collectors.groupingBy(u -> formatter.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)))))
                .entrySet().stream().map(u -> {
                    try {
                        ThematicQueryTransverseInfo.ThematicQueryTransverseInfoBuilder builder = ThematicQueryTransverseInfo
                                .builder().time(formatter.parse(u.getKey()));
                        Tuple<Double, Double> limitInfo = new Tuple<>();
                        if (Objects.nonNull(datumPointID)) {
                            u.getValue().stream().filter(w -> w.containsKey(DbConstant.SENSOR_ID_TAG)).filter(w ->
                                            datumPointID.equals(sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG))).getItem1()))
                                    .findAny().ifPresent(w -> {
                                        Tuple<Integer, String> pointInfo = sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG)));
                                        double value = Convert.toDouble(w.get(fieldToken));
                                        limitInfo.setItem1(value + upper);
                                        limitInfo.setItem2(value - lower);
                                        builder.datumPointData(DatumPointData.builder().monitorPointID(pointInfo.getItem1())
                                                .monitorPointName(pointInfo.getItem2()).value(value).upper(value + upper)
                                                .lower(value - lower).build());
                                    });
                        }
                        builder.monitorPointList(u.getValue().stream().filter(w -> Objects.isNull(datumPointID)
                                || !datumPointID.equals(sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG)))
                                .getItem1())).map(w -> {
                            double value = Convert.toDouble(w.get(fieldToken));
                            Tuple<Integer, String> pointInfo = sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG)));
                            return ThematicMonitorPointValueInfo.builder().abnormalValue(getAbnormalValue(limitInfo, value))
                                    .monitorPointID(pointInfo.getItem1()).monitorPointName(pointInfo.getItem2()).value(value).build();
                        }).toList());
                        return builder.build();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public List<Map<String, Object>> queryLongitudinalList(QueryLongitudinalListParam param) {
        final SimpleDateFormat formatter = TimeUtil.DestinyFormatter.getFormatter(DisplayDensity.fromValue(param.getDisplayDensity()));
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, param.getInspectedPointIDList()));
        if (CollUtil.isEmpty(sensorList)) {
            return List.of();
        }
        Map<Integer, TbMonitorPoint> monitorPointIDMap = param.getTbMonitorPointList().stream().collect(Collectors.toMap(TbMonitorPoint::getID, Function.identity()));
        Map<Integer, TbMonitorPoint> sensorPointMap = sensorList.stream().filter(u -> monitorPointIDMap.containsKey(u.getMonitorPointID()))
                .collect(Collectors.toMap(TbSensor::getID, v -> monitorPointIDMap.get(v.getMonitorPointID())));

        // 真正被展示出来的监测点集
        Set<Integer> monitorPointSet = new HashSet<>(param.getMonitorPointIDList());
        Map<Integer, List<ThematicEigenValueInfo>> pointIDEigenValueMap = tbEigenValueMapper.selectFieldInfoByPointIDList(param.getInspectedPointIDList()).stream().collect(Collectors.groupingBy(ThematicEigenValueInfo::getMonitorPointID));
        Double threshold = Optional.ofNullable(param.getCutoffWallConfig()).map(CutoffWallConfig::getValue).orElse(null);
        Set<Integer> bilateralMonitorIDSet = Optional.ofNullable(param.getCutoffWallConfig()).map(CutoffWallConfig::getMonitorPointIDList).map(HashSet::new).orElse(null);
        //TODO get dataList
        List<Map<String, Object>> dataList = List.of();

//        Map.of("emptyPipe",20,"distanceElevation", ThematicDistanceElevationInfo obj) List
        return dataList.stream().collect(Collectors.groupingBy(u -> formatter.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)))))
                .entrySet().stream().map(u -> {
                    try {

                        // 非防渗墙双边点集
                        List<ThematicPipeData> nonBilateralList = u.getValue().stream().filter(w -> w.containsKey(DbConstant.SENSOR_ID_TAG))
                                .filter(w -> sensorPointMap.containsKey(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG))))
                                .filter(w -> Optional.ofNullable(bilateralMonitorIDSet).map(s -> !s.contains(sensorPointMap
                                        .get(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG))).getID())).orElse(true))
                                .map(w -> {
                                    TbMonitorPoint tbMonitorPoint = sensorPointMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG)));

                                    ThematicPipeData.ThematicPipeDataBuilder builder = ThematicPipeData.builder()
                                            .monitorPointID(tbMonitorPoint.getID()).monitorPointName(tbMonitorPoint.getName());
                                    Optional.ofNullable(w.get(EMPTY_PIPE_DISTANCE)).map(Convert::toInt).ifPresent(builder::emptyPipeDistance);
                                    Optional.ofNullable(w.get(LEVEL_ELEVATION)).map(Convert::toDouble).map(s -> {
                                        ThematicLevelElevationInfo.ThematicLevelElevationInfoBuilder elevationInfoBuilder = ThematicLevelElevationInfo.builder().value(s);

//                                        getMaxEigenValueData();

                                        return elevationInfoBuilder.build();
                                    }).ifPresent(builder::levelElevation);


                                    return builder.build();
                                }).toList();

                        // 防渗墙双边点集
                        //TODO
                        List<ThematicPipeData> bilateralList = List.of();


                        return Map.of("time", formatter.parse(u.getKey()), "pipeDataList", CollUtil.union(nonBilateralList, bilateralList));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    @Override
    public ThematicRainWaterAnalysisInfo queryRainWaterData(QueryRainWaterDataParam param) {
        final SimpleDateFormat formatter = TimeUtil.DestinyFormatter.getFormatter(DisplayDensity.fromValue(param.getDisplayDensity()));
        ThematicRainWaterAnalysisInfo.ThematicRainWaterAnalysisInfoBuilder builder = ThematicRainWaterAnalysisInfo.builder();
        List<ThematicRainWaterDataInfo> dataList = queryRainWaterDataList(param, formatter);
        builder.dataList(dataList);
        Optional.of(dataList).filter(CollUtil::isNotEmpty).ifPresent(u -> {
            List<Map<String, Object>> maxDataList = new ArrayList<>();
            u.stream().reduce((o1, o2) -> o1.getRainfall() > o2.getRainfall() ? o1 : o2).map(w -> w.toMaxDataMap(1, formatter)).ifPresent(maxDataList::add);
            u.stream().reduce((o1, o2) -> o1.getDistance() > o2.getDistance() ? o1 : o2).map(w -> w.toMaxDataMap(2, formatter)).ifPresent(maxDataList::add);
            Optional.ofNullable(param.getVolumeFlowInputMonitorPointID()).flatMap(w -> u.stream().reduce((o1, o2) ->
                    o1.getVolumeFlowInput() > o2.getVolumeFlowInput() ? o1 : o2).map(s -> s.toMaxDataMap(3, formatter))).ifPresent(maxDataList::add);
            Optional.ofNullable(param.getVolumeFlowOutputMonitorPointID()).flatMap(w -> u.stream().reduce((o1, o2) ->
                    o1.getVolumeFlowOutput() > o2.getVolumeFlowOutput() ? o1 : o2).map(s -> s.toMaxDataMap(4, formatter))).ifPresent(maxDataList::add);
            builder.maxDataList(maxDataList);
        });
        Optional.ofNullable(param.getEigenvalueDataList()).filter(CollUtil::isNotEmpty).ifPresent(builder::eigenvalueDataList);
        Optional.ofNullable(param.getDataEventDataList()).filter(CollUtil::isNotEmpty).ifPresent(builder::dataEventDataList);
        return builder.build();
    }

    @Override
    public PageUtil.Page<ThematicRainWaterDataInfo> queryRainWaterPageData(QueryRainWaterDataPageParam param) {
        List<ThematicRainWaterDataInfo> dataList = queryRainWaterDataList(param, TimeUtil.DestinyFormatter.getFormatter(DisplayDensity.fromValue(param.getDisplayDensity())));
        return PageUtil.page(dataList, param.getPageSize(), param.getCurrentPage());
    }

    @Override
    public List<ThematicDryBeachInfo> queryDryBeachDataList(QueryDryBeachDataListParam param) {
        final SimpleDateFormat formatter = TimeUtil.DestinyFormatter.getFormatter(DisplayDensity.fromValue(param.getDisplayDensity()));
        List<ThematicEigenValueInfo> eigenValueList = tbEigenValueMapper.selectFieldInfoByPointIDList(List
                .of(param.getDryBeachMonitorPointID(), param.getDistanceMonitorPointID()));
        Map<String, List<ThematicEigenValueInfo>> tokenValueMap = eigenValueList.stream()
                .filter(u -> DISTANCE.equals(u.getFieldToken()) || DRY_BEACH.equals(u.getFieldToken()))
                .collect(Collectors.groupingBy(ThematicEigenValueInfo::getFieldToken));
        // 设计洪水位(配置了多个同名特征值时取最小值)
        Double design = tokenValueMap.get(DISTANCE).stream().filter(u -> DESIGN_FLOOD_DISTANCE.equals(u.getEigenValueName()))
                .map(ThematicEigenValueInfo::getEigenValue).reduce((o1, o2) -> o1 < o2 ? o1 : o2).orElse(null);
        // 最小干滩长度(配置了多个同名特征值时取最小值)
        Double minDryBeach = tokenValueMap.get(DRY_BEACH).stream().filter(u -> MIN_DRY_BEACH.equals(u.getEigenValueName()))
                .map(ThematicEigenValueInfo::getEigenValue).reduce((o1, o2) -> o1 < o2 ? o1 : o2).orElse(null);
        //TODO get dataList
        List<Map<String, Object>> dataList = List.of();
        return dataList.stream().collect(Collectors.groupingBy(u -> formatter.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)))))
                .entrySet().stream().map(u -> {
                    try {
                        ThematicDryBeachInfo.ThematicDryBeachInfoBuilder builder = ThematicDryBeachInfo.builder().time(formatter.parse(u.getKey()));
                        u.getValue().stream().peek(w -> {
                            if (w.containsKey(SLOPE_RATIO)) {
                                builder.slopeRratio(Convert.toDouble(w.get(SLOPE_RATIO)));
                            } else if (w.containsKey(s4)) {
                                builder.rainfall(Convert.toDouble(w.get(s4)));
                            } else if (w.containsKey(DRY_BEACH)) {
                                Optional.of(w.get(DRY_BEACH)).map(Convert::toDouble).ifPresent(n -> builder.dryBeach(Optional.ofNullable(design)
                                        .filter(s -> s < n).map(s -> Map.of("value", n, "abnormalValue", n - s)).orElse(Map.of("value", n))));
                            } else if (w.containsKey(DISTANCE)) {
                                Optional.of(w.get(DISTANCE)).map(Convert::toDouble).ifPresent(n -> builder.distance(Optional.ofNullable(minDryBeach)
                                        .filter(s -> s > n).map(s -> Map.of("value", n, "abnormalValue", n - s)).orElse(Map.of("value", n))));
                            }
                        }).toList();
                        return builder.build();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public DryBeachDataInfo queryDryBeachData(QueryDryBeachDataParam param) {
        final Integer rainfallMonitorPointID = param.getRainfallMonitorPointID();
        final Integer dryBeachMonitorPointID = param.getDryBeachMonitorPointID();
        final Integer distanceMonitorPointID = param.getDistanceMonitorPointID();

        DryBeachDataInfo.DryBeachDataInfoBuilder builder = DryBeachDataInfo.builder();
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, List.of(dryBeachMonitorPointID, rainfallMonitorPointID, distanceMonitorPointID)));
        for (TbSensor tbSensor : sensorList) {
            Integer sensorID = tbSensor.getID();
            Integer monitorPointID = tbSensor.getMonitorPointID();
            Integer monitorType = tbSensor.getMonitorType();
            if (dryBeachMonitorPointID.equals(monitorPointID)) {
                sensorDataDao.querySensorNewData(List.of(sensorID), List.of(buildFieldSelectInfo(DRY_BEACH), buildFieldSelectInfo(SLOPE_RATIO), buildFieldSelectInfo(DbConstant.TIME_FIELD), buildFieldSelectInfo(DbConstant.SENSOR_ID_TAG)), false, monitorType)
                        .stream().filter(w -> w.containsKey(DRY_BEACH) && w.containsKey(SLOPE_RATIO)).findFirst().ifPresent(u -> {
                            Optional.of(u).map(w -> w.get(DRY_BEACH)).map(Convert::toDouble).ifPresent(builder::dryBeach);
                            Optional.of(u).map(w -> w.get(SLOPE_RATIO)).map(Convert::toDouble).ifPresent(builder::slopeRratio);
                        });
            } else if (distanceMonitorPointID.equals(monitorPointID)) {
                queryNewDryBeachData(sensorID, monitorType, DISTANCE, builder::distance);
            } else if (rainfallMonitorPointID.equals(monitorPointID)) {
                queryNewDryBeachData(sensorID, monitorType, s4, builder::rainfall);
            }
        }
        return builder.build();
    }

    /**
     * @param monitorPointIDUpperLimitMap is different to the eponymous map in {@code param}
     */
    private StAnalysisDataGroup dealStNewData(final List<Integer> damFrontSensorIDList,
                                              final Map<Integer, Integer> sensorIDPointIDMap,
                                              final Map<Integer, ConfigBaseResponse> pointIDConfigMap,
                                              final QueryStDataParam param, final Integer monitorType,
                                              final Map<Integer, Double> monitorPointIDUpperLimitMap) {
        final List<Integer> sensorIDList = sensorIDPointIDMap.keySet().stream().toList();
        final List<FieldSelectInfo> fieldSelectInfoList = param.getFieldSelectInfoList();
        List<StAnalysisData> dataList = sensorDataDao.querySensorNewData(
                sensorIDList, fieldSelectInfoList, false, monitorType).stream().filter(u -> Objects.nonNull(
                u.get(DbConstant.SENSOR_ID_TAG)) && Objects.nonNull(u.get(DbConstant.TIME_FIELD))
                && Objects.nonNull(u.get(DISTANCE))).map(u -> {
            Integer monitorPointID = Optional.of(u.get(DbConstant.SENSOR_ID_TAG)).map(Convert::toInt).map(sensorIDPointIDMap::get).orElse(null);
            ConfigBaseResponse config = Optional.ofNullable(monitorPointID).map(pointIDConfigMap::get)
                    .orElse(new ConfigBaseResponse());
            return StAnalysisData.builder().monitorPointID(monitorPointID).monitorPointName(config.getTargetName())
                    .time(Convert.toDate(u.get(DbConstant.TIME_FIELD)))
                    .distance(Convert.toDouble(u.get(DISTANCE)))
                    .upperLimit(monitorPointIDUpperLimitMap.get(monitorPointID))
                    .pointConfig(config.getConfig()).build();
        }).toList();
        List<Double> damDistanceList = sensorDataDao.querySensorNewData(
                        damFrontSensorIDList, fieldSelectInfoList, false, monitorType).stream()
                .map(u -> u.get(DISTANCE)).filter(Objects::nonNull).map(Convert::toDouble).toList();
        return StAnalysisDataGroup.builder().distance(damDistanceList.stream().reduce(Double::sum)
                .orElse(0d) / damDistanceList.size()).dataList(dataList).build();
    }

    private List<StAnalysisDataGroup> dealStAvgData(final List<Integer> damFrontSensorIDList,
                                                    final Map<Integer, Integer> sensorIDPointIDMap,
                                                    final Map<Integer, ConfigBaseResponse> pointIDConfigMap,
                                                    final QueryStDataParam param, final Integer monitorType) {
        final Integer density = param.getDensity();
        final List<Integer> sensorIDList = sensorIDPointIDMap.keySet().stream().toList();
        final List<FieldSelectInfo> fieldSelectInfoList = param.getFieldSelectInfoList();
        final Timestamp startTime = param.getStartTime();
        final Timestamp endTime = param.getEndTime();
        final SimpleDateFormat sensorDateFormatter = TimeUtil.getMilliDefaultFormatter();
        final List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime, endTime,
                "1d", fieldSelectInfoList, false, monitorType, null);
        final List<Map<String, Object>> damMetadataList = sensorDataDao.querySensorData(damFrontSensorIDList, startTime,
                endTime, "1d", fieldSelectInfoList, false, monitorType, null);
        if (Objects.nonNull(density)) {
            switch (density) {
                case 1 -> {
                    // daily avg
                    return getStAvgData(metadataList, damMetadataList, pointIDConfigMap, o -> getZeroDateTime(
                            DateUtil.parse(o.toString(), sensorDateFormatter)));
                }
                case 2 -> {
                    // month avg
                    return getStAvgData(metadataList, damMetadataList, pointIDConfigMap, o -> getZeroDateTime(
                            DateUtil.parse(o.toString(), sensorDateFormatter).setField(DateField.DAY_OF_MONTH, 1)));
                }
                case 3 -> {
                    // year avg
                    return getStAvgData(metadataList, damMetadataList, pointIDConfigMap, o -> getZeroDateTime(
                            DateUtil.parse(o.toString(), sensorDateFormatter).setField(DateField.MONTH, 1)
                                    .setField(DateField.DAY_OF_MONTH, 1)));
                }
                default -> {
                    log.error("Unsupported density,density: {}", density);
                    return new ArrayList<>();
                }
            }
        } else {
            return new ArrayList<>();
        }
    }

    private List<StAnalysisDataGroup> getStAvgData(final List<Map<String, Object>> metadataList,
                                                   final List<Map<String, Object>> damFrontMetadataList,
                                                   final Map<Integer, ConfigBaseResponse> pointIDConfigMap,
                                                   final Function<Object, Date> groupFunc) {
        final Map<Integer, ConfigBaseResponse> sensorIDPointConfigMap = pointIDConfigMap.values().stream().collect(
                Collectors.toMap(ConfigBaseResponse::getChildID, v -> v));
        Map<Date, List<Tuple<Integer, Double>>> sensorMap = metadataList.stream().collect(Collectors.groupingBy(
                u -> groupFunc.apply(u.get(DbConstant.TIME_FIELD)))).entrySet().stream().map(u -> new Tuple<>(
                u.getKey(), u.getValue().stream().collect(Collectors.groupingBy(w -> Convert.toInt(w.get(DbConstant.SENSOR_ID_TAG))))
                .entrySet().stream().map(w -> new Tuple<>(
                        w.getKey(), w.getValue().stream().map(s -> Convert.toDouble(s.get(DISTANCE)))
                        .reduce(Double::sum).orElse(0d) / w.getValue().size())).toList())).collect(Collectors.toMap(
                Tuple::getItem1, Tuple::getItem2));
        Map<Date, Double> damFrontMap = damFrontMetadataList.stream().collect(Collectors.groupingBy(u -> groupFunc.apply(
                u.get(DbConstant.TIME_FIELD)))).entrySet().stream().map(u -> new Tuple<>(u.getKey(), u.getValue().stream()
                .map(w -> Convert.toDouble(w.get(DISTANCE))).reduce(Double::sum).orElse(0d)
                / u.getValue().size())).collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));
        //TODO 在某个密度下,监测点数据和坝前水位数据部分缺失了怎么办? 如果用户忘记给某个监测点关联传感器导致一直没数据怎么办？
        //TODO 暂时做了一个过滤,专题数据必须要求该密度下一定存在【监测点水位】和【坝前水位】数据
        return CollectionUtil.intersection(sensorMap.keySet(), damFrontMap.keySet()).stream().map(u ->
                StAnalysisDataGroup.builder().time(u).distance(damFrontMap.get(u)).dataList(sensorMap.get(u).stream()
                        .map(w -> {
                            ConfigBaseResponse response = sensorIDPointConfigMap.get(w.getItem1());
                            return StAnalysisData.builder().monitorPointID(response.getTargetID()).monitorPointName(
                                            response.getTargetName()).pointConfig(response.getConfig())
                                    .distance(w.getItem2()).build();
                        }).toList()).build()).toList();
    }

    private DmParamDto prepareDmDataParam(QueryDmDataParam param) {
        Integer monitorPointID = param.getMonitorPointID();
        TbMonitorPoint tbMonitorPoint = param.getTbMonitorPoint();
        Integer monitorType = tbMonitorPoint.getMonitorType();
        return DmParamDto.builder().monitorPointID(monitorPointID).monitorPointName(tbMonitorPoint.getName())
                .monitorType(monitorType).sensorIDConfigMap(tbProjectInfoMapper.selectMonitorPointRelateSensorConfig(
                        monitorPointID, monitorType, param.getGroup(), param.getKey()).stream().collect(Collectors.toMap(
                        ConfigBaseResponse::getTargetID, t -> t))).build();
    }

    @SuppressWarnings("DuplicatedCode")
    private PageUtil.Page<DmThematicAnalysisPageInfo> dealDmHistoryPageData(final QueryDmDataPageParam param, final DmParamDto dto) {
        final Map<Integer, ConfigBaseResponse> configMap = dto.getSensorIDConfigMap();
        final Integer monitorType = dto.getMonitorType();
        final List<FieldSelectInfo> fieldSelectInfoList = param.getFieldSelectInfoList();
        final Integer density = param.getDensity();
        final String fieldToken = param.getFieldToken();
        final Timestamp startTime = param.getStartTime();
        final Timestamp endTime = param.getEndTime();
        final List<Integer> sensorIDList = configMap.keySet().stream().toList();
        final SimpleDateFormat sensorDateFormatter = TimeUtil.getMilliDefaultFormatter();
        final Set<String> selectDateStrSet = param.getDataList().stream().distinct().map(u -> DateUtil.format(
                u, sensorDateFormatter)).collect(Collectors.toSet());
        final TbMonitorPoint tbMonitorPoint = param.getTbMonitorPoint();
        final Integer monitorPointID = tbMonitorPoint.getID();
        final String monitorPointName = tbMonitorPoint.getName();
        final Integer currentPage = param.getCurrentPage();
        final Integer pageSize = param.getPageSize();
        if (Objects.nonNull(density)) {
            switch (density) {
                // all
                case 0 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(
                            sensorIDList, startTime, endTime, null, fieldSelectInfoList, false, monitorType, null);
                    List<Map<String, Object>> sourceList = getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> {
                                String dateStr = o.toString();
                                return selectDateStrSet.contains(dateStr) ? DateUtil.parse(dateStr, sensorDateFormatter)
                                        : null;
                            });
                    List<DmThematicAnalysisPageInfo> dataList = dmInfoListToDmPageInfoList(
                            sourceList, monitorPointID, monitorPointName);
                    return PageUtil.page(dataList, pageSize, currentPage);
                }
                // daily avg
                case 1 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(
                            sensorIDList, startTime, endTime, "1d", fieldSelectInfoList, false, monitorType, null);
                    List<Map<String, Object>> sourceList = getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> {
                                String dateStr = o.toString();
                                return selectDateStrSet.contains(dateStr) ? getZeroDateTime(DateUtil.parse(
                                        o.toString(), sensorDateFormatter)) : null;
                            });
                    List<DmThematicAnalysisPageInfo> dataList = dmInfoListToDmPageInfoList(
                            sourceList, monitorPointID, monitorPointName);
                    return PageUtil.page(dataList, pageSize, currentPage);
                }
                // month avg
                case 2 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(
                            sensorIDList, startTime, endTime, "1d", fieldSelectInfoList, false, monitorType, null);
                    List<Map<String, Object>> sourceList = getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> {
                                String dateStr = o.toString();
                                return selectDateStrSet.contains(dateStr) ? getZeroDateTime(DateUtil.parse(
                                        dateStr, sensorDateFormatter).setField(DateField.DAY_OF_MONTH, 1)) : null;
                            });
                    List<DmThematicAnalysisPageInfo> dataList = dmInfoListToDmPageInfoList(
                            sourceList, monitorPointID, monitorPointName);
                    return PageUtil.page(dataList, pageSize, currentPage);
                }
                // year avg
                case 3 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(
                            sensorIDList, startTime, endTime, "1d", fieldSelectInfoList, false, monitorType, null);
                    List<Map<String, Object>> sourceList = getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> {
                                String dateStr = o.toString();
                                return selectDateStrSet.contains(dateStr) ? getZeroDateTime(DateUtil.parse(
                                                dateStr, sensorDateFormatter).setField(DateField.MONTH, 1)
                                        .setField(DateField.DAY_OF_MONTH, 1)) : null;
                            });
                    List<DmThematicAnalysisPageInfo> dataList = dmInfoListToDmPageInfoList(
                            sourceList, monitorPointID, monitorPointName);
                    return PageUtil.page(dataList, pageSize, currentPage);
                }
                default -> {
                    log.error("Unsupported density,density: {}", density);
                    return PageUtil.Page.empty();
                }
            }
        } else {
            return PageUtil.Page.empty();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private List<Map<String, Object>> dealDmHistoryData(final QueryDmDataParam param, final DmParamDto dto) {
        final Map<Integer, ConfigBaseResponse> configMap = dto.getSensorIDConfigMap();
        final Integer monitorType = dto.getMonitorType();
        final List<FieldSelectInfo> fieldSelectInfoList = param.getFieldSelectInfoList();
        final Integer density = param.getDensity();
        final String fieldToken = param.getFieldToken();
        final Timestamp startTime = param.getStartTime();
        final Timestamp endTime = param.getEndTime();
        final List<Integer> sensorIDList = configMap.keySet().stream().toList();
        final SimpleDateFormat sensorDateFormatter = TimeUtil.getMilliDefaultFormatter();
        if (Objects.nonNull(density)) {
            switch (density) {
                // all
                case 0 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, null, fieldSelectInfoList, false, monitorType, null);
                    return getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> DateUtil.parse(o.toString(), sensorDateFormatter));
                }
                // daily avg
                case 1 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, "1d", fieldSelectInfoList, false, monitorType, null);
                    return getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> getZeroDateTime(DateUtil.parse(o.toString(), sensorDateFormatter)));
                }
                // month avg
                case 2 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, "1d", fieldSelectInfoList, false, monitorType, null);
                    return getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> getZeroDateTime(DateUtil.parse(o.toString(), sensorDateFormatter)
                                    .setField(DateField.DAY_OF_MONTH, 1)));
                }
                // year avg
                case 3 -> {
                    List<Map<String, Object>> metadataList = sensorDataDao.querySensorData(sensorIDList, startTime,
                            endTime, "1d", fieldSelectInfoList, false, monitorType, null);
                    return getDmHistoryData(metadataList, configMap, fieldToken,
                            o -> getZeroDateTime(DateUtil.parse(o.toString(), sensorDateFormatter)
                                    .setField(DateField.MONTH, 1).setField(DateField.DAY_OF_MONTH, 1)));
                }
                default -> {
                    log.error("Unsupported density,density: {}", density);
                    return new ArrayList<>();
                }
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @param groupFunc Get the {@code groupTime} which is grouping key generate by influxDB metadata time field.
     *                  In addition,record should be discarded while returning null
     * @return [{"time":${DateTime},"dataList":[{${DmAnalysisData}}]}]
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<Map<String, Object>> getDmHistoryData(final List<Map<String, Object>> metadataList,
                                                       final Map<Integer, ConfigBaseResponse> configMap,
                                                       final String fieldToken, final Function<Object, Date> groupFunc) {
        // if influxDB metadata not contains 'sid','time' or corresponding fieldToken,think as illegal data.
        if (metadataList.stream().anyMatch(p -> Objects.isNull(p.get(DbConstant.TIME_FIELD))
                || Objects.isNull(p.get(fieldToken)) || Objects.isNull(p.get(DbConstant.SENSOR_ID_TAG)))) {
            log.error("InfluxDB contains illegal data,data:\n{}", metadataList);
            throw new RuntimeException("InfluxDB contains illegal data!");
        }
        Map<Date, List<DmAnalysisData>> dataMap = metadataList.stream().map(u -> {
                    int sensorID = Convert.toInt(u.get(DbConstant.SENSOR_ID_TAG));
                    ConfigBaseResponse response = configMap.get(sensorID);
                    return DmAnalysisData.builder().sensorID(sensorID).totalValue(Convert.toDouble(u.get(fieldToken)))
                            .groupTime(groupFunc.apply(u.get(DbConstant.TIME_FIELD)))
                            .fieldToken(fieldToken).sensorName(response.getTargetName()).config(response.getConfig()).build();
                }).filter(u -> Objects.nonNull(u.getGroupTime())).collect(Collectors.groupingBy(DmAnalysisData::getGroupTime))
                .entrySet().stream().map(u -> new Tuple<>(u.getKey(), u.getValue().stream().collect(Collectors.groupingBy(
                        DmAnalysisData::getSensorID)).values().stream().map(w -> w.stream().findFirst().map(s -> {
                    s.setTotalValue(w.stream().map(DmAnalysisData::getTotalValue).reduce(Double::sum)
                            .orElse(0d) / w.size());
                    return s;
                }).orElseThrow(() -> new RuntimeException("Unreachable Exception"))).toList())).collect(Collectors
                        .toMap(Tuple::getItem1, Tuple::getItem2));
        dataMap.values().stream().flatMap(Collection::stream).toList().stream().collect(Collectors.groupingBy(
                DmAnalysisData::getSensorID)).values().stream().map(u -> u.stream().sorted(
                (o1, o2) -> (int) (o1.getGroupTime().getTime() - o2.getGroupTime().getTime())).reduce((o1, o2) -> {
            o2.setSegmentValue(o2.getTotalValue() - o1.getTotalValue());
            return o2;
        }).orElseThrow(() -> new RuntimeException("Unreachable Exception"))).toList();
        return dataMap.entrySet().stream().map(u -> Map.of("time", u.getKey(), "dataList", u.getValue())).toList();
    }

    private DateTime getZeroDateTime(DateTime dateTime) {
        return dateTime.setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0).setField(DateField.MILLISECOND, 0);
    }

    private List<DmThematicAnalysisPageInfo> dmInfoListToDmPageInfoList(final List<Map<String, Object>> source,
                                                                        final Integer monitorPointID,
                                                                        final String monitorPointName) {
        return source.stream().map(u -> (DmAnalysisData) u.get("dataList")).toList().stream()
                .map(u -> DmThematicAnalysisPageInfo.pageParentBuilder().parent(u).monitorPointID(monitorPointID)
                        .monitorPointName(monitorPointName).build()).sorted((o1, o2) ->
                        (int) (o1.getTime().getTime() - o2.getTime().getTime())).toList();
    }

    private List<Map<String, Object>> queryPointDataList(List<TbSensor> tbSensorList, DisplayDensity displayType,
                                                         StatisticalMethods statisticalMethods, Date startTime, Date endTime) {
        return Optional.of(tbSensorList).filter(CollUtil::isNotEmpty).map(u -> {
            List<Map<String, Object>> res = new ArrayList<>();
            Map<Integer, List<Integer>> monitorSensorIDMap = u.stream().collect(Collectors.groupingBy(TbSensor::getMonitorType))
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().stream().map(TbSensor::getID).toList()));
            Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap = tbMonitorTypeFieldMapper.selectList(
                            new LambdaQueryWrapper<TbMonitorTypeField>().in(TbMonitorTypeField::getMonitorType, monitorSensorIDMap.keySet()))
                    .stream().collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));
            for (Map.Entry<Integer, List<Integer>> entry : monitorSensorIDMap.entrySet()) {
                Integer monitorType = entry.getKey();
                if (monitorTypeFieldMap.containsKey(monitorType)) {
                    List<FieldSelectInfo> fieldSelectInfos = combineFieldSelectInfoList(monitorTypeFieldMap.get(monitorType));
                    List<Integer> sensorIDList = entry.getValue();
                    //TODO get dataList
                    List<Map<String, Object>> dataList = List.of();
                    res.addAll(dataList);
                }
            }
            return res;
        }).orElse(List.of());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<ThematicRainWaterDataInfo> queryRainWaterDataList(final QueryRainWaterDataBaseInfo param, final SimpleDateFormat formatter) {
        final Integer volumeFlowInputMonitorPointID = param.getVolumeFlowInputMonitorPointID();
        final Integer volumeFlowOutputMonitorPointID = param.getVolumeFlowOutputMonitorPointID();
        List<Integer> monitorIDList = new ArrayList<>();
        monitorIDList.add(param.getDistanceMonitorPointID());
        monitorIDList.add(param.getRainfallMonitorPointID());
        Optional.ofNullable(volumeFlowInputMonitorPointID).ifPresent(monitorIDList::add);
        Optional.ofNullable(volumeFlowOutputMonitorPointID).ifPresent(monitorIDList::add);
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, monitorIDList));
        Map<Integer, Integer> sensorPointIDMap = sensorList.stream().collect(Collectors.toMap(TbSensor::getID, TbSensor::getMonitorPointID));

        //TODO get dataList
        List<Map<String, Object>> dataList = List.of();
        return dataList.stream().collect(Collectors.groupingBy(u -> formatter.format(u.get(DbConstant.TIME_FIELD)))).entrySet().stream().map(u -> {
            try {
                ThematicRainWaterDataInfo.ThematicRainWaterDataInfoBuilder builder = ThematicRainWaterDataInfo.builder().time(formatter.parse(u.getKey()));
                u.getValue().stream().filter(w -> w.containsKey(s4) || w.containsKey(DISTANCE) || w.containsKey(VOLUME_FLOW)).peek(w -> {
                    if (w.containsKey(s4)) {
                        builder.rainfall(Convert.toDouble(w.get(s4)));
                    } else if (w.containsKey(DISTANCE)) {
                        builder.distance(Convert.toDouble(w.get(DISTANCE)));
                    } else if (w.containsKey(VOLUME_FLOW)) {
                        Optional.of(w.get(DbConstant.SENSOR_ID_TAG)).map(Convert::toInt).filter(sensorPointIDMap::containsKey)
                                .map(sensorPointIDMap::get).ifPresent(s -> {
                                    if (s.equals(volumeFlowInputMonitorPointID)) {
                                        builder.volumeFlowInput(Convert.toDouble(w.get(VOLUME_FLOW)));
                                    } else if (s.equals(volumeFlowOutputMonitorPointID)) {
                                        builder.volumeFlowOutput(Convert.toDouble(w.get(VOLUME_FLOW)));
                                    }
                                });
                    }
                }).toList();
                return builder.build();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private List<FieldSelectInfo> combineFieldSelectInfoList(final List<TbMonitorTypeField> fieldList) {
        List<FieldSelectInfo> fieldSelectInfos = fieldList.stream().map(TbMonitorTypeField::getFieldToken)
                .map(this::buildFieldSelectInfo).collect(Collectors.toList());
        fieldSelectInfos.add(buildFieldSelectInfo(DbConstant.SENSOR_ID_TAG));
        fieldSelectInfos.add(buildFieldSelectInfo(DbConstant.TIME_FIELD));
        return fieldSelectInfos;
    }

    private FieldSelectInfo buildFieldSelectInfo(final String fieldToken) {
        FieldSelectInfo info = new FieldSelectInfo();
        info.setFieldToken(fieldToken);
        return info;
    }

    private @Nullable Double getAbnormalValue(final Tuple<Double, Double> limitInfo, double value) {
        Double curUpper = limitInfo.getItem1();
        Double curLower = limitInfo.getItem2();
        Double abnormalValue = null;
        if (Objects.nonNull(curUpper) && value > curUpper) {
            abnormalValue = value - curUpper;
        } else if (Objects.nonNull(curLower) && value < curLower) {
            abnormalValue = value - curLower;
        }
        return abnormalValue;
    }

    /**
     * 查询单个属性的最新数据，解析成{@code double}后进行对应操作
     *
     * @param sensorID    传感器ID
     * @param monitorType 监测类型
     * @param fieldToken  属性fieldToken
     * @param action      操作
     */
    private void queryNewDryBeachData(final Integer sensorID, final Integer monitorType, final String fieldToken, Consumer<? super Double> action) {
        sensorDataDao.querySensorNewData(List.of(sensorID), List.of(buildFieldSelectInfo(fieldToken), buildFieldSelectInfo(DbConstant.TIME_FIELD), buildFieldSelectInfo(DbConstant.SENSOR_ID_TAG)), false, monitorType)
                .stream().filter(w -> w.containsKey(fieldToken)).findFirst().map(w -> w.get(fieldToken)).map(Convert::toDouble).ifPresent(action);
    }

    private Map<String, String> getAbsulatePathMap(final List<String> ossKeyList) {
        Integer companyID = CurrentSubjectHolder.getCurrentSubject().getCompanyID();
        return Optional.of(ossKeyList).filter(CollUtil::isNotEmpty).map(u -> fileService.getFileUrlList(u, companyID)
                .stream().collect(Collectors.toMap(FileInfoResponse::getFilePath, FileInfoResponse::getAbsolutePath))).orElse(Map.of());
    }
}
