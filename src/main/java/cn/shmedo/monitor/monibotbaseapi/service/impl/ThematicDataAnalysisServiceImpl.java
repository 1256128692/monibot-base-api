package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.FieldDataType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.thematicDataAnalysis.DmParamDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ConfigBaseResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.service.IThematicDataAnalysisService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.util.InfluxDBDataUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Nullable;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicFieldToken.*;
import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicEigenValueName.*;
import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicExcelExceptionDesc.FIELD_ERROR;

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
        return tbMonitorPointMapper.selectThematicGroupPointList(param);
    }

    @Override
    public List<ThematicQueryTransverseInfo> queryTransverseList(QueryTransverseListParam param) {
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, param.getInspectedPointIDList()));
        if (CollUtil.isEmpty(sensorList)) {
            return List.of();
        }
        DisplayDensity displayDensity = DisplayDensity.fromValue(param.getDisplayDensity());
        String fieldToken = param.getFieldToken();
        Integer datumPointID = Optional.ofNullable(param.getDatumPoint()).map(DatumPointConfig::getMonitorPointID).orElse(null);
        Double upper = Optional.ofNullable(param.getDatumPoint()).map(DatumPointConfig::getUpper).orElse(null);
        Double lower = Optional.ofNullable(param.getDatumPoint()).map(DatumPointConfig::getLower).orElse(null);

        Map<Integer, String> pointIDNameMap = param.getTbMonitorPointList().stream().collect(Collectors.toMap(
                TbMonitorPoint::getID, TbMonitorPoint::getName));
        Map<Integer, Tuple<Integer, String>> sensorPointInfoMap = sensorList.stream().collect(Collectors.toMap(
                TbSensor::getID, u -> new Tuple<>(u.getMonitorPointID(), pointIDNameMap.get(u.getMonitorPointID()))));
        List<Map<String, Object>> dataList = queryPointDataList(sensorList, displayDensity, param.getStatisticalMethod(),
                param.getStartTime(), param.getEndTime());
        DateTimeFormatter formatter = TimeUtil.DestinyFormatter.getFormatter(displayDensity);
        return dataList.stream().filter(u -> u.containsKey(DbConstant.TIME_FIELD))
                .collect(Collectors.groupingBy(u -> DateUtil.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)), formatter)))
                .entrySet().stream().map(u -> {
                    ThematicQueryTransverseInfo.ThematicQueryTransverseInfoBuilder builder = ThematicQueryTransverseInfo
                            .builder().time(DateUtil.parse(u.getKey(), formatter));
                    Tuple<Double, Double> limitInfo = new Tuple<>();
                    if (Objects.nonNull(datumPointID)) {
                        u.getValue().stream().filter(w -> w.containsKey(DbConstant.SENSOR_ID_FIELD_TOKEN)).filter(w ->
                                        datumPointID.equals(sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).getItem1()))
                                .findAny().ifPresent(w -> {
                                    Tuple<Integer, String> pointInfo = sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN)));
                                    Double value = Optional.ofNullable(w.get(fieldToken)).map(Convert::toDouble).orElse(null);
                                    Optional.ofNullable(value).map(s -> s + upper).ifPresent(limitInfo::setItem1);
                                    Optional.ofNullable(value).map(s -> s - upper).ifPresent(limitInfo::setItem2);
                                    builder.datumPointData(DatumPointData.builder().monitorPointID(pointInfo.getItem1())
                                            .monitorPointName(pointInfo.getItem2()).value(value)
                                            .upper(Optional.ofNullable(value).map(s -> s + upper).orElse(null))
                                            .lower(Optional.ofNullable(value).map(s -> s - lower).orElse(null)).build());
                                });
                    }
                    builder.monitorPointList(u.getValue().stream().filter(w -> Objects.isNull(datumPointID)
                            || !datumPointID.equals(sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN)))
                            .getItem1())).map(w -> {
                        Double value = Optional.ofNullable(w.get(fieldToken)).map(Convert::toDouble).orElse(null);
                        Tuple<Integer, String> pointInfo = sensorPointInfoMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN)));
                        return ThematicMonitorPointValueInfo.builder().abnormalValue(getAbnormalValue(limitInfo, value))
                                .monitorPointID(pointInfo.getItem1()).monitorPointName(pointInfo.getItem2()).value(value).build();
                    }).toList());
                    return builder.build();
                }).sorted(((o1, o2) -> DateUtil.compare(o1.getTime(), o2.getTime()))).toList();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public WetLineConfigInfo queryWetLineConfig(QueryWetLineConfigParam param) {
        WetLineConfigInfo res = tbMonitorPointMapper.selectWetLineConfig(param);
        Optional.ofNullable(res.getMonitorGroupImagePath()).filter(ObjectUtil::isNotEmpty).map(fileService::getFileUrl)
                .ifPresent(res::setMonitorGroupImagePath);
        List<FieldSelectInfo> fieldSelectInfoList = tbMonitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                        .eq(TbMonitorTypeField::getMonitorType, param.getMonitorType())).stream().map(TbMonitorTypeField::getFieldToken)
                .map(InfluxDBDataUtil::buildFieldSelectInfo).toList();
        List<Integer> sensorIDList = res.getMonitorPointList().stream().map(ThematicPointListInfo::getSensorID)
                .filter(Objects::nonNull).distinct().toList();

        Map<Integer, Tuple<Double, Double>> dataMap = Optional.of(sensorIDList).filter(CollUtil::isNotEmpty).map(u ->
                        sensorDataDao.querySensorNewData(u, fieldSelectInfoList, false, MonitorType.WET_LINE.getKey()).stream()
                                .filter(w -> Objects.nonNull(w.get(LEVEL_ELEVATION)) && Objects.nonNull(w.get(EMPTY_PIPE_DISTANCE)))
                                .collect(Collectors.toMap(k -> Convert.toInt(k.get(DbConstant.SENSOR_ID_FIELD_TOKEN)),
                                        v -> new Tuple<>(Convert.toDouble(v.get(LEVEL_ELEVATION)), Convert.toDouble(v.get(EMPTY_PIPE_DISTANCE))))))
                .orElse(Map.of());
        if (CollUtil.isNotEmpty(dataMap)) {
            res.getMonitorPointList().stream().peek(u -> Optional.ofNullable(u.getSensorID()).filter(dataMap::containsKey)
                    .map(dataMap::get).ifPresent(w -> {
                        u.setLevelElevation(w.getItem1());
                        u.setEmptyPipeDistance(w.getItem2());
                    })).toList();
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> queryLongitudinalList(QueryLongitudinalListParam param) {
        Map<Integer, Integer> orderMap = param.getOrderMap();
        DisplayDensity displayDensity = DisplayDensity.fromValue(param.getDisplayDensity());
        final DateTimeFormatter formatter = TimeUtil.DestinyFormatter.getFormatter(displayDensity);
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, param.getInspectedPointIDList()));
        if (CollUtil.isEmpty(sensorList)) {
            return List.of();
        }
        Map<Integer, TbMonitorPoint> monitorPointIDMap = param.getTbMonitorPointList().stream().collect(Collectors.toMap(TbMonitorPoint::getID, Function.identity()));
        Map<Integer, TbMonitorPoint> sensorPointMap = sensorList.stream().filter(u -> monitorPointIDMap.containsKey(u.getMonitorPointID()))
                .collect(Collectors.toMap(TbSensor::getID, u -> monitorPointIDMap.get(u.getMonitorPointID())));
        Map<Integer, List<ThematicEigenValueInfo>> pointIDEigenValueMap = tbEigenValueMapper.selectFieldInfoByPointIDList(param.getInspectedPointIDList()).stream().collect(Collectors.groupingBy(ThematicEigenValueInfo::getMonitorPointID));
        Double threshold = Optional.ofNullable(param.getCutoffWallConfig()).map(CutoffWallConfig::getValue).orElse(null);
        Set<Integer> bilateralMonitorIDSet = Optional.ofNullable(param.getCutoffWallConfig()).map(CutoffWallConfig::getMonitorPointIDList).map(HashSet::new).orElse(null);

        // 需要被展示出来的两侧防渗墙监测点集
        Set<Integer> monitorPointSet = param.getMonitorPointIDList().stream().filter(u ->
                Optional.ofNullable(bilateralMonitorIDSet).map(w -> w.contains(u)).orElse(false)).collect(Collectors.toSet());

        List<Map<String, Object>> dataList = queryPointDataList(sensorList, displayDensity, param.getStatisticalMethod(),
                param.getStartTime(), param.getEndTime());
        return dataList.stream().filter(u -> u.containsKey(DbConstant.SENSOR_ID_FIELD_TOKEN))
                .filter(u -> sensorPointMap.containsKey(Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN))))
                .collect(Collectors.groupingBy(u -> DateUtil.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)), formatter)))
                .entrySet().stream().map(u -> {
                    // 非防渗墙双边点集
                    List<ThematicPipeData> nonBilateralList = u.getValue().stream().filter(w -> Optional.ofNullable(bilateralMonitorIDSet)
                            .map(s -> !s.contains(sensorPointMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).getID()))
                            .orElse(true)).map(w -> {
                        TbMonitorPoint tbMonitorPoint = sensorPointMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN)));
                        Integer monitorPointID = tbMonitorPoint.getID();
                        ThematicPipeData.ThematicPipeDataBuilder builder = ThematicPipeData.builder()
                                .monitorPointID(monitorPointID).monitorPointName(tbMonitorPoint.getName());
                        Optional.ofNullable(w.get(EMPTY_PIPE_DISTANCE)).map(Convert::toDouble).ifPresent(builder::emptyPipeDistance);
                        Optional.ofNullable(w.get(LEVEL_ELEVATION)).map(Convert::toDouble).map(s ->
                                ThematicLevelElevationInfo.builder().eigenValue(getMaxEigenValueData(s, pointIDEigenValueMap.get(monitorPointID)))
                                        .value(s).build()).ifPresent(builder::levelElevation);
                        return builder.build();
                    }).toList();

                    List<Map<String, Object>> list = u.getValue().stream().filter(w -> Optional.ofNullable(bilateralMonitorIDSet)
                            .map(s -> s.contains(sensorPointMap.get(Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN))).getID()))
                            .orElse(false)).toList();

                    // 防渗墙双边点集
                    List<ThematicPipeData> bilateralList = Optional.of(list).filter(w -> w.size() == 2).map(w -> {
                        List<ThematicPipeData> res = new ArrayList<>();
                        Map<String, Object> o1 = w.get(0);
                        Map<String, Object> o2 = w.get(1);
                        Double o1Val = Optional.ofNullable(o1.get(LEVEL_ELEVATION)).map(Convert::toDouble).orElse(null);
                        Double o2Val = Optional.ofNullable(o2.get(LEVEL_ELEVATION)).map(Convert::toDouble).orElse(null);
                        Double osmoticValue = Optional.ofNullable(threshold).filter(s -> Objects.nonNull(o1Val) && Objects.nonNull(o2Val))
                                .filter(s -> s <= Math.abs(o1Val - o2Val)).map(s -> s - Math.abs(o1Val - o2Val)).orElse(null);
                        dealBilateralData(pointIDEigenValueMap, monitorPointSet, o1, sensorPointMap.get(Convert.toInt(o1.get(DbConstant.SENSOR_ID_FIELD_TOKEN))), osmoticValue, res::add);
                        dealBilateralData(pointIDEigenValueMap, monitorPointSet, o2, sensorPointMap.get(Convert.toInt(o2.get(DbConstant.SENSOR_ID_FIELD_TOKEN))), osmoticValue, res::add);
                        return res;
                    }).orElse(List.of());
                    return Map.of("time", DateUtil.parse(u.getKey(), formatter), "pipeDataList",
                            CollUtil.union(nonBilateralList, bilateralList).stream().sorted(Comparator.comparingInt(o -> orderMap.get(o.getMonitorPointID()))).toList());
                }).sorted((o1, o2) -> DateUtil.compare(Convert.toDate(o1.get("time")), Convert.toDate(o2.get("time")))).toList();
    }

    @Override
    public ThematicRainWaterAnalysisInfo queryRainWaterData(QueryRainWaterDataParam param) {
        final DateTimeFormatter formatter = TimeUtil.DestinyFormatter.getFormatter(DisplayDensity.fromValue(param.getDisplayDensity()));
        ThematicRainWaterAnalysisInfo.ThematicRainWaterAnalysisInfoBuilder builder = ThematicRainWaterAnalysisInfo.builder();
        List<ThematicRainWaterDataInfo> dataList = queryRainWaterDataList(param, formatter);
        builder.dataList(dataList);
        Optional.of(dataList).filter(CollUtil::isNotEmpty).ifPresent(u -> {
            List<Map<String, Object>> maxDataList = new ArrayList<>();
            u.stream().filter(o -> Objects.nonNull(o.getRainfall())).reduce((o1, o2) -> o1.getRainfall() > o2.getRainfall() ? o1 : o2)
                    .map(w -> w.toMaxDataMap(1, formatter)).ifPresent(maxDataList::add);
            u.stream().filter(o -> Objects.nonNull(o.getDistance())).reduce((o1, o2) -> o1.getDistance() > o2.getDistance() ? o1 : o2)
                    .map(w -> w.toMaxDataMap(2, formatter)).ifPresent(maxDataList::add);
            Optional.ofNullable(param.getVolumeFlowInputMonitorPointID()).flatMap(w -> u.stream().filter(o -> Objects.nonNull(o.getVolumeFlowInput()))
                    .reduce((o1, o2) -> o1.getVolumeFlowInput() > o2.getVolumeFlowInput() ? o1 : o2)
                    .map(s -> s.toMaxDataMap(3, formatter))).ifPresent(maxDataList::add);
            Optional.ofNullable(param.getVolumeFlowOutputMonitorPointID()).flatMap(w -> u.stream().filter(o -> Objects.nonNull(o.getVolumeFlowInput()))
                    .reduce((o1, o2) -> o1.getVolumeFlowOutput() > o2.getVolumeFlowOutput() ? o1 : o2)
                    .map(s -> s.toMaxDataMap(4, formatter))).ifPresent(maxDataList::add);
            builder.maxDataList(maxDataList);
        });
        Optional.ofNullable(param.getEigenvalueDataList()).filter(CollUtil::isNotEmpty).ifPresent(builder::eigenvalueDataList);
        Optional.ofNullable(param.getDataEventDataList()).filter(CollUtil::isNotEmpty).ifPresent(builder::dataEventDataList);
        return builder.build();
    }

    @Override
    public PageUtil.Page<ThematicRainWaterDataInfo> queryRainWaterPageData(QueryRainWaterDataPageParam param) {
        List<ThematicRainWaterDataInfo> dataList = new ArrayList<>(queryRainWaterDataList(param, TimeUtil.DestinyFormatter.getFormatter(DisplayDensity.fromValue(param.getDisplayDensity()))));
        Collections.reverse(dataList);
        return PageUtil.page(dataList, param.getPageSize(), param.getCurrentPage());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public List<ThematicDryBeachInfo> queryDryBeachDataList(QueryDryBeachDataListParam param) {
        DisplayDensity displayDensity = DisplayDensity.fromValue(param.getDisplayDensity());
        final String rainfallToken = param.getRainfallToken();
        final DateTimeFormatter formatter = TimeUtil.DestinyFormatter.getFormatter(displayDensity);
        List<ThematicEigenValueInfo> eigenValueList = tbEigenValueMapper.selectFieldInfoByPointIDList(
                List.of(param.getDryBeachMonitorPointID(), param.getDistanceMonitorPointID()));
        Map<String, List<ThematicEigenValueInfo>> tokenValueMap = eigenValueList.stream()
                .filter(u -> DISTANCE.equals(u.getFieldToken()) || DRY_BEACH.equals(u.getFieldToken()))
                .collect(Collectors.groupingBy(ThematicEigenValueInfo::getFieldToken));
        // 设计洪水位(配置了多个同名特征值时取最小值)
        Double design = Optional.ofNullable(tokenValueMap.get(DISTANCE)).flatMap(u -> u.stream()
                .filter(w -> DESIGN_FLOOD_DISTANCE.equals(w.getEigenValueName())).map(ThematicEigenValueInfo::getEigenValue)
                .reduce((o1, o2) -> o1 < o2 ? o1 : o2)).orElse(null);
        // 最小干滩长度(配置了多个同名特征值时取最小值)
        Double minDryBeach = Optional.ofNullable(tokenValueMap.get(DRY_BEACH)).flatMap(u -> u.stream()
                .filter(w -> MIN_DRY_BEACH.equals(w.getEigenValueName())).map(ThematicEigenValueInfo::getEigenValue)
                .reduce((o1, o2) -> o1 < o2 ? o1 : o2)).orElse(null);

        List<Map<String, Object>> dataList = queryDryBeachDataList(param.getTbSensorList(), param.getMonitorTypeFieldMap(),
                displayDensity, param.getStartTime(), param.getEndTime());
        return dataList.stream().filter(u -> u.containsKey(DbConstant.TIME_FIELD))
                .collect(Collectors.groupingBy(u -> DateUtil.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)), formatter))).entrySet().stream().map(u -> {
                    ThematicDryBeachInfo.ThematicDryBeachInfoBuilder builder = ThematicDryBeachInfo.builder().time(DateUtil.parse(u.getKey(), formatter));
                    u.getValue().stream().peek(w -> {
                        Optional.ofNullable(w.get(SLOPE_RATIO)).map(Convert::toDouble).ifPresent(builder::slopeRratio);
                        Optional.ofNullable(w.get(rainfallToken)).map(Convert::toDouble).ifPresent(builder::rainfall);
                        Optional.ofNullable(w.get(DRY_BEACH)).map(Convert::toDouble).ifPresent(n -> builder.dryBeach(Optional.ofNullable(minDryBeach)
                                .filter(s -> s > n).map(s -> Map.of("value", n, "abnormalValue", n - s)).orElse(Map.of("value", n))));
                        Optional.ofNullable(w.get(DISTANCE)).map(Convert::toDouble).ifPresent(n -> builder.distance(Optional.ofNullable(design)
                                .filter(s -> s < n).map(s -> Map.of("value", n, "abnormalValue", n - s)).orElse(Map.of("value", n))));
                    }).toList();
                    return builder.build();
                }).sorted((o1, o2) -> DateUtil.compare(o1.getTime(), o2.getTime())).toList();
    }

    @Override
    public DryBeachDataInfo queryDryBeachData(QueryDryBeachDataParam param) {
        final Integer rainfallMonitorPointID = param.getRainfallMonitorPointID();
        final Integer dryBeachMonitorPointID = param.getDryBeachMonitorPointID();
        final Integer distanceMonitorPointID = param.getDistanceMonitorPointID();

        DryBeachDataInfo.DryBeachDataInfoBuilder builder = DryBeachDataInfo.builder();
        Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap = param.getMonitorTypeFieldMap();
        for (TbSensor tbSensor : param.getTbSensorList()) {
            Integer sensorID = tbSensor.getID();
            Integer monitorPointID = tbSensor.getMonitorPointID();
            Integer monitorType = tbSensor.getMonitorType();
            if (monitorTypeFieldMap.containsKey(monitorType)) {
                List<FieldSelectInfo> fieldSelectInfoList = monitorTypeFieldMap.get(monitorType).stream()
                        .map(TbMonitorTypeField::getFieldToken).map(InfluxDBDataUtil::buildFieldSelectInfo).toList();
                if (dryBeachMonitorPointID.equals(monitorPointID)) {
                    sensorDataDao.querySensorNewData(List.of(sensorID), fieldSelectInfoList, false, monitorType)
                            .stream().filter(w -> w.containsKey(DRY_BEACH) && w.containsKey(SLOPE_RATIO)).findFirst().ifPresent(u -> {
                                Optional.of(u).map(w -> w.get(DRY_BEACH)).map(Convert::toDouble).ifPresent(builder::dryBeach);
                                Optional.of(u).map(w -> w.get(SLOPE_RATIO)).map(Convert::toDouble).ifPresent(builder::slopeRratio);
                            });
                } else if (distanceMonitorPointID.equals(monitorPointID)) {
                    queryNewDryBeachData(sensorID, monitorType, fieldSelectInfoList, DISTANCE, builder::distance);
                } else if (rainfallMonitorPointID.equals(monitorPointID)) {
                    queryNewDryBeachData(sensorID, monitorType, fieldSelectInfoList, monitorType.equals(MonitorType.RAINFALL.getKey()) ? RAINFALL : PERIOD_RAINFALL, builder::rainfall);
                }
            }
        }
        return builder.build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addManualDataBatch(AddManualDataBatchParam param) {
        for (AddManualItem item : param.getInsertDataList()) {
            List<FieldSelectInfo> fieldSelectInfoList = item.getFieldTokenList().stream()
                    .map(InfluxDBDataUtil::buildFieldSelectInfo).peek(u -> u.setFieldType(FieldType.DOUBLE)).toList();
            sensorDataDao.insertSensorData(item.getDataList(), false, true, fieldSelectInfoList, item.getMonitorType(), StrUtil.EMPTY);
        }
    }

    @Override
    public void getImportManualTemplate(GetImportManualTemplateParam param, HttpServletResponse response) {
        Map<FieldClass, List<MonitorTypeFieldV2>> classFieldMap = param.getClassFieldMap();
        List<String> sheetHeader = new ArrayList<>(List.of("序号", "人工传感器", "时间"));
        Optional.ofNullable(classFieldMap.get(FieldClass.BASIC)).map(u -> u.stream()
                .map(w -> w.getFieldName() + "（" + w.getEngUnit() + "）").toList()).ifPresent(sheetHeader::addAll);
//        目前不允许直接导入扩展属性
//        Optional.ofNullable(classFieldMap.get(FieldClass.EXTEND)).map(u -> u.stream()
//                .map(w -> w.getFieldName() + "（" + w.getEngUnit() + "）").toList()).ifPresent(sheetHeader::addAll);

        List<String> sheetDemo = new ArrayList<>(List.of("1", "示例请务必删除该行数据", "2023/10/11 10:00:00"));
        Optional.ofNullable(classFieldMap.get(FieldClass.BASIC)).map(u -> u.stream().map(TbMonitorTypeField::getFieldDataType)
                .map(FieldDataType::valueOfString).map(this::getSheetDemoCellData).toList()).ifPresent(sheetDemo::addAll);
//        目前不允许直接导入扩展属性
//        Optional.ofNullable(classFieldMap.get(FieldClass.EXTEND)).map(u -> u.stream().map(TbMonitorTypeField::getFieldDataType)
//                .map(FieldDataType::valueOfString).map(this::getSheetDemoCellData).toList()).ifPresent(sheetDemo::addAll);
        String fileName = Optional.of(classFieldMap).map(Map::values).flatMap(u -> u.stream().findAny())
                .flatMap(u -> u.stream().findAny()).map(MonitorTypeFieldV2::getMonitorTypeName).orElse("") + "_人工数据.xlsx";

        try (ExcelWriter writer = ExcelUtil.getWriter(true); ServletOutputStream os = response.getOutputStream()) {
            // response headers
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(ExcelUtil.XLSX_CONTENT_TYPE);
            response.setHeader(DefaultConstant.CONTENT_DISPOSITION_HEADER, writer.getDisposition(fileName, StandardCharsets.UTF_8));
            CellStyle cellStyle = writer.getCellStyle();
            cellStyle.setBorderLeft(BorderStyle.NONE);
            cellStyle.setBorderRight(BorderStyle.NONE);
            cellStyle.setBorderBottom(BorderStyle.NONE);
            cellStyle.setBorderTop(BorderStyle.NONE);

            // need nested by {@code List.of} cause {@code ExcelWriter} will recognize the outside {@code Iterable} as column.
            writer.write(List.of(sheetHeader, sheetDemo)).setFreezePane(1).setRowHeight(-1, 25);
            for (int i = 0; i < sheetHeader.size(); i++) {
                writer.setColumnWidth(i, Math.max(sheetHeader.get(i).length() * 2, 20));
            }
            writer.flush(os);
        } catch (IOException e) {
            log.error("获取输出流失败!");
            response.setContentType(DefaultConstant.JSON);
            response.setHeader(DefaultConstant.CONTENT_DISPOSITION_HEADER, "");
            throw new RuntimeException();
        }
    }

    @Override
    public ResultWrapper<Object> importManualDataBatch(Integer projectID, Integer monitorType, MultipartFile file) {
        List<MonitorTypeFieldV2> monitorTypeFieldV2List = tbMonitorTypeFieldMapper.queryByMonitorTypesV2(List.of(monitorType), false);
        Map<String, FieldDataType> fieldTokenDataTypeMap = monitorTypeFieldV2List.stream().collect(
                Collectors.toMap(TbMonitorTypeField::getFieldToken, u -> FieldDataType.valueOfString(u.getFieldDataType())));
        Map<FieldClass, List<MonitorTypeFieldV2>> classFieldMap = monitorTypeFieldV2List.stream().collect(
                Collectors.groupingBy(u -> FieldClass.codeOf(u.getFieldClass())));
        if (CollUtil.isEmpty(classFieldMap)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在或未设置子类型,无法解析导入文件!");
        }
        Map<String, String> basicColFieldTokenMap = Optional.ofNullable(classFieldMap.get(FieldClass.BASIC))
                .map(u -> u.stream().collect(Collectors.toMap(w -> w.getFieldName() + "（" + w.getEngUnit() + "）",
                        TbMonitorTypeField::getFieldToken))).orElse(Collections.emptyMap());
//        目前不允许直接导入扩展属性
//        Map<String, String> extendColFieldTokenMap = Optional.ofNullable(classFieldMap.get(FieldClass.EXTEND))
//                .map(u -> u.stream().collect(Collectors.toMap(w -> w.getFieldName() + "（" + w.getEngUnit() + "）",
//                        TbMonitorTypeField::getFieldToken))).orElse(Collections.emptyMap());
        Map<String, String> colNameFieldTokenMap = MapUtil.builder(new HashMap<String, String>()).putAll(basicColFieldTokenMap).build();
//                .putAll(extendColFieldTokenMap).build();

        try (ExcelReader reader = ExcelUtil.getReader(file.getInputStream(), 0)) {
            // parse the table header
            List<Object> header = reader.readRow(0);
            Map<Integer, String> colFieldTokenMap = IntStream.range(3, header.size())
                    .filter(u -> colNameFieldTokenMap.containsKey(Convert.toStr(header.get(u))))
                    .mapToObj(u -> new Tuple<>(u, colNameFieldTokenMap.get(Convert.toStr(header.get(u)))))
                    .collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));
            if (colFieldTokenMap.keySet().size() != header.size() - 3) {
                throw new ParseException("Excel表格中有列不属于该监测类型的基础属性", -1);
            }
            List<List<Object>> dataList = reader.read(1);
            if (dataList.stream().anyMatch(u -> u.size() <= 3)) {
                throw new ParseException("Excel表格内容错误", -1);
            }
            // find sensors
            List<String> manualSensorNameList = dataList.stream().map(u -> u.get(1)).map(Convert::toStr).distinct().toList();
            Map<String, Integer> manualSensorNameIDMap = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>()
                    .eq(TbSensor::getProjectID, projectID).eq(TbSensor::getMonitorType, monitorType)
                    .in(TbSensor::getAlias, manualSensorNameList)).stream().collect(Collectors.toMap(TbSensor::getAlias, TbSensor::getID));
            if (manualSensorNameList.size() != manualSensorNameIDMap.keySet().size()) {
                throw new ParseException("导入数据中有传感器不存在", -1);
            }

            // parse records then convert to {@code AddManualDataBatchParam}
            List<ManualDataItem> manualDataItemList = dataList.stream().map(u -> {
                Integer orderCode = Optional.ofNullable(u.get(0)).filter(ObjectUtil::isNotEmpty).map(Convert::toInt)
                        .orElseThrow(() -> new IllegalArgumentException("解析序号失败!"));
                Integer sensorID = Optional.ofNullable(u.get(1)).filter(ObjectUtil::isNotEmpty).map(Convert::toStr)
                        .map(manualSensorNameIDMap::get).orElseThrow(() ->
                                new IllegalArgumentException(StrUtil.format(FIELD_ERROR, orderCode, "传感器名称")));
                Date time = Optional.ofNullable(u.get(2)).filter(ObjectUtil::isNotEmpty).map(Convert::toDate).orElseThrow(() ->
                        new IllegalArgumentException(StrUtil.format(FIELD_ERROR, orderCode, "时间")));
                List<ManualDataItem> list = IntStream.range(3, u.size()).filter(w -> ObjectUtil.isNotEmpty(u.get(w))).mapToObj(w -> {
                    String fieldToken = colFieldTokenMap.get(w);
                    ManualDataItem item = new ManualDataItem();
                    item.setSensorID(sensorID);
                    item.setTime(time);
                    item.setFieldToken(fieldToken);
                    item.setValue(Optional.ofNullable(u.get(w)).map(s -> fieldTokenDataTypeMap.get(fieldToken).parseData(s)).map(Convert::toStr).orElse(null));
                    return item;
                }).toList();
                if (CollUtil.isEmpty(list)) {
                    throw new IllegalArgumentException(StrUtil.format("序号为:{}的行没有任何属性数据", orderCode));
                }
                return list;
            }).flatMap(Collection::stream).toList();
            AddManualDataBatchParam param = new AddManualDataBatchParam();
            param.setProjectID(projectID);
            param.setDataList(manualDataItemList);
            if (Objects.nonNull(param.validate())) {
                throw new IllegalArgumentException();
            }
            this.addManualDataBatch(param);
        } catch (IOException | ParseException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前表格中存在异常数据，请核实后重新导入！");
        }
        return ResultWrapper.successWithNothing();
    }

    @Override
    public CompareAnalysisDataInfo queryCompareAnalysisData(QueryCompareAnalysisDataParam param) {
        CompareAnalysisDataInfo.CompareAnalysisDataInfoBuilder builder = CompareAnalysisDataInfo.builder();

        // 分析间隔 millisecond
        Long interval = param.getInterval();
        String fieldToken = param.getFieldToken();
        MonitorTypeFieldV2 field = param.getMonitorTypeField();
        FieldDataType fieldDataType = param.getFieldDataType();
        Integer autoSensorID = param.getAutoSensorID();
        Integer manualSensorID = param.getManualSensorID();
        Integer monitorType = param.getMonitorType();

        // 误差值
        List<TbProjectConfig> tbProjectConfigList = tbProjectConfigMapper.selectList(new LambdaQueryWrapper<TbProjectConfig>()
                .eq(TbProjectConfig::getProjectID, param.getProjectID()).eq(TbProjectConfig::getKey, monitorType)
                .eq(TbProjectConfig::getGroup, DefaultConstant.ThematicMistakeConfig.GROUP));
        Double mistakeValue = Optional.of(tbProjectConfigList).filter(CollUtil::isNotEmpty).map(u -> u.get(0))
                .map(TbProjectConfig::getValue).map(u -> {
                    try {
                        JSONArray array = JSONUtil.parseArray(u);
                        return array.stream().map(JSONUtil::parseObj).toList();
                    } catch (JSONException e) {
                        log.error("解析json array失败,json array:{}", u);
                        return List.of();
                    }
                }).filter(CollUtil::isNotEmpty).flatMap(u -> u.stream().filter(w -> ((JSONObject) w).containsKey(fieldToken))
                        .findAny().map(w -> ((JSONObject) w).get(fieldToken))).map(Convert::toDouble).orElse(null);

        List<Map<String, Object>> dataList = sensorDataDao.querySensorData(param.getSensorIDList(), param.getStartTime(), param.getEndTime(), null, param.getFieldSelectInfoList(), false, monitorType, null);
        Map<Integer, Map<Long, Map<String, Object>>> collect = dataList.stream().filter(u -> u.containsKey(fieldToken))
                .collect(Collectors.groupingBy(u -> Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN)),
                        Collectors.toMap(w -> Convert.toDate(w.get(DbConstant.TIME_FIELD)).getTime(), Function.identity())));
        builder.autoCount(Optional.of(autoSensorID).map(collect::get).map(Map::values)
                .map(Collection::stream).map(Stream::count).orElse(0L).intValue());
        builder.manualCount(Optional.of(manualSensorID).map(collect::get).map(Map::values)
                .map(Collection::stream).map(Stream::count).orElse(0L).intValue());

        // 自动化数据,目前tuple中的{@code Object}仅可以取{@code Double}和{@code Long}
        Map<Long, Tuple<Date, Object>> autoDataMap = Optional.of(autoSensorID).map(collect::get).map(u ->
                u.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, w -> {
                    Map<String, Object> val = w.getValue();
                    return new Tuple<>(Convert.toDate(val.get(DbConstant.TIME_FIELD)), fieldDataType.parseData(val.get(fieldToken)));
                }))).orElse(Map.of());
        List<Long> autoDataTimestampList = autoDataMap.keySet().stream().sorted().toList();
        List<CompareAnalysisData> compareAnalysisDataList = Optional.ofNullable(collect.get(manualSensorID)).map(u -> {
            List<CompareAnalysisData> list = u.entrySet().stream().sorted(Comparator.comparingLong(Map.Entry::getKey))
                    .map(w -> {
                        Double manualData = Convert.toDouble(fieldDataType.parseData(w.getValue().get(fieldToken)));
                        CompareAnalysisData.CompareAnalysisDataBuilder dataBuilder = CompareAnalysisData.builder()
                                .manualTime(Convert.toDate(w.getValue().get(DbConstant.TIME_FIELD))).manualData(manualData)
                                .chnUnit(field.getChnUnit()).engUnit(field.getEngUnit()).normal(true);

                        Tuple<Date, Object> closeAutoData = findCloseData(w.getKey(), interval, autoDataTimestampList, autoDataMap);
                        Optional.ofNullable(closeAutoData).ifPresent(s -> {
                            Double autoData = Convert.toDouble(s.getItem2());
                            dataBuilder.autoTime(s.getItem1()).autoData(autoData);
                            Optional.ofNullable(mistakeValue).map(n -> n * 2).filter(n -> Math.abs(manualData - autoData) > n)
                                    .ifPresent(n -> dataBuilder.normal(false).abnormalValue(Math.abs(manualData - autoData) - n));
                        });
                        return dataBuilder.build();
                    }).toList();
            long abnormalCount = list.stream().filter(w -> !w.getNormal()).count();
            builder.abnormalCount(Convert.toInt(abnormalCount));
            builder.abnormalRatio(list.size() == 0 ? -1 : Convert.toDouble(abnormalCount) / list.size() * 100);
            return list;
        }).orElse(List.of());
        return builder.dataList(compareAnalysisDataList).build();
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
                u.get(DbConstant.SENSOR_ID_FIELD_TOKEN)) && Objects.nonNull(u.get(DbConstant.TIME_FIELD))
                && Objects.nonNull(u.get(DISTANCE))).map(u -> {
            Integer monitorPointID = Optional.of(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN)).map(Convert::toInt).map(sensorIDPointIDMap::get).orElse(null);
            ConfigBaseResponse config = Optional.ofNullable(monitorPointID).map(pointIDConfigMap::get)
                    .orElse(new ConfigBaseResponse());
            return StAnalysisData.builder().monitorPointID(monitorPointID).monitorPointName(config.getTargetName())
                    .time(Convert.toDate(u.get(DbConstant.TIME_FIELD)))
                    .distance(Optional.ofNullable(u.get(DISTANCE)).map(Convert::toDouble).orElse(null))
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
        final DateTimeFormatter sensorDateFormatter = TimeUtil.getDefaultDateTimeFormatter();
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
                u.getKey(), u.getValue().stream().collect(Collectors.groupingBy(w -> Convert.toInt(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN))))
                .entrySet().stream().map(w -> new Tuple<>(
                        w.getKey(), w.getValue().stream().filter(s -> Objects.nonNull(s.get(DISTANCE))).map(s -> Convert.toDouble(s.get(DISTANCE)))
                        .reduce(Double::sum).orElse(0d) / w.getValue().size())).toList())).collect(Collectors.toMap(
                Tuple::getItem1, Tuple::getItem2));
        Map<Date, Double> damFrontMap = damFrontMetadataList.stream().collect(Collectors.groupingBy(u -> groupFunc.apply(
                u.get(DbConstant.TIME_FIELD)))).entrySet().stream().map(u -> new Tuple<>(u.getKey(), u.getValue().stream()
                .filter(w -> Objects.nonNull(w.get(DISTANCE))).map(w -> Convert.toDouble(w.get(DISTANCE))).reduce(Double::sum).orElse(0d)
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
        final DateTimeFormatter sensorDateFormatter = TimeUtil.getDefaultDateTimeFormatter();
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
        final DateTimeFormatter sensorDateFormatter = TimeUtil.getDefaultDateTimeFormatter();
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
                || Objects.isNull(p.get(fieldToken)) || Objects.isNull(p.get(DbConstant.SENSOR_ID_FIELD_TOKEN)))) {
            log.error("InfluxDB contains illegal data,data:\n{}", metadataList);
            throw new RuntimeException("InfluxDB contains illegal data!");
        }
        Map<Date, List<DmAnalysisData>> dataMap = metadataList.stream().map(u -> {
                    int sensorID = Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN));
                    ConfigBaseResponse response = configMap.get(sensorID);
                    return DmAnalysisData.builder().sensorID(sensorID).totalValue(Optional.ofNullable(u.get(fieldToken)).map(Convert::toDouble).orElse(null))
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
                                                         Integer statisticalMethods, Date startTime, Date endTime) {
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
                    List<FieldBaseInfo> fieldSelectInfos = monitorTypeFieldMap.get(monitorType).stream()
                            .map(TbMonitorTypeField::getFieldToken).map(InfluxDBDataUtil::buildFieldBaseInfo).toList();
                    List<Integer> sensorIDList = entry.getValue();
                    List<Map<String, Object>> dataList = sensorDataDao.queryCommonSensorDataList(
                            sensorIDList, startTime, endTime, displayType.getValue(), statisticalMethods, fieldSelectInfos, monitorType);
                    res.addAll(DisplayDensity.needExtraGrouping(displayType) ?
                            InfluxDBDataUtil.calculateStatistics(dataList, displayType.getValue(), statisticalMethods, true) : dataList);
                }
            }
            return res;
        }).orElse(List.of());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<ThematicRainWaterDataInfo> queryRainWaterDataList(final QueryRainWaterDataBaseInfo param, final DateTimeFormatter formatter) {
        final Integer volumeFlowInputMonitorPointID = param.getVolumeFlowInputMonitorPointID();
        final Integer volumeFlowOutputMonitorPointID = param.getVolumeFlowOutputMonitorPointID();
        final String rainFallToken = param.getRainFallToken();
        List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(TbSensor::getMonitorPointID, param.getMonitorIDList()));
        Map<Integer, Integer> sensorPointIDMap = sensorList.stream().collect(Collectors.toMap(TbSensor::getID, TbSensor::getMonitorPointID));
        List<Map<String, Object>> dataList = queryRainWaterDataList(sensorList, DisplayDensity.fromValue(param.getDisplayDensity()), param.getStartTime(), param.getEndTime());
        return dataList.stream().filter(u -> u.containsKey(DbConstant.TIME_FIELD)).collect(Collectors.groupingBy(u ->
                DateUtil.format(Convert.toDate(u.get(DbConstant.TIME_FIELD)), formatter))).entrySet().stream().map(u -> {
            ThematicRainWaterDataInfo.ThematicRainWaterDataInfoBuilder builder = ThematicRainWaterDataInfo.builder().time(DateUtil.parse(u.getKey(), formatter));
            u.getValue().stream().filter(w -> w.containsKey(rainFallToken) || w.containsKey(DISTANCE) || w.containsKey(VOLUME_FLOW)).peek(w -> {
                if (w.containsKey(rainFallToken)) {
                    Optional.ofNullable(w.get(rainFallToken)).map(Convert::toDouble).ifPresent(builder::rainfall);
                } else if (w.containsKey(DISTANCE)) {
                    Optional.ofNullable(w.get(DISTANCE)).map(Convert::toDouble).ifPresent(builder::distance);
                } else if (w.containsKey(VOLUME_FLOW)) {
                    Optional.of(w.get(DbConstant.SENSOR_ID_FIELD_TOKEN)).map(Convert::toInt).filter(sensorPointIDMap::containsKey)
                            .map(sensorPointIDMap::get).ifPresent(s -> {
                                if (s.equals(volumeFlowInputMonitorPointID)) {
                                    Optional.ofNullable(w.get(VOLUME_FLOW)).map(Convert::toDouble).ifPresent(builder::volumeFlowInput);
                                } else if (s.equals(volumeFlowOutputMonitorPointID)) {
                                    Optional.ofNullable(w.get(VOLUME_FLOW)).map(Convert::toDouble).ifPresent(builder::volumeFlowOutput);
                                }
                            });
                }
            }).toList();
            return builder.build();
        }).sorted((o1, o2) -> DateUtil.compare(o1.getTime(), o2.getTime())).toList();
    }

    private List<Map<String, Object>> queryRainWaterDataList(List<TbSensor> sensorList, DisplayDensity displayDensity, Date startTime, Date endTime) {
        List<Map<String, Object>> res = new ArrayList<>();
        Map<Integer, List<TbSensor>> collect = sensorList.stream().collect(Collectors.groupingBy(TbSensor::getMonitorType));
        Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap = tbMonitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                .in(TbMonitorTypeField::getMonitorType, collect.keySet())).stream().collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));
        for (Map.Entry<Integer, List<TbSensor>> entry : collect.entrySet()) {
            Integer monitorType = entry.getKey();
            List<Integer> sensorIDList = entry.getValue().stream().map(TbSensor::getID).distinct().toList();
            // 库水位、流量：最新一条；降雨量：如果'显示密度'是全部则为'最新一条'，否则是'阶段变化'
            if (monitorType.equals(MonitorType.WT_RAINFALL.getKey())) {
                int statisticalMethods = displayDensity.equals(DisplayDensity.ALL) ? StatisticalMethods.LATEST.getValue() : StatisticalMethods.CHANGE.getValue();
                queryDataAddToRes(displayDensity, startTime, endTime, res, monitorType, sensorIDList, monitorTypeFieldMap, statisticalMethods);
            } else if (monitorType.equals(MonitorType.WATER_LEVEL.getKey())) {
                queryDataAddToRes(displayDensity, startTime, endTime, res, monitorType, sensorIDList, monitorTypeFieldMap, StatisticalMethods.LATEST.getValue());
            } else if (monitorType.equals(MonitorType.FLOW_CAPACITY.getKey())) {
                queryDataAddToRes(displayDensity, startTime, endTime, res, monitorType, sensorIDList, monitorTypeFieldMap, StatisticalMethods.LATEST.getValue());
            }
        }
        return res;
    }

    private List<Map<String, Object>> queryDryBeachDataList(final List<TbSensor> sensorList, final Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap,
                                                            DisplayDensity displayDensity, Date startTime, Date endTime) {
        List<Map<String, Object>> res = new ArrayList<>();
        Map<Integer, List<TbSensor>> monitorTypeSensorMap = sensorList.stream().collect(Collectors.groupingBy(TbSensor::getMonitorType));
        for (Map.Entry<Integer, List<TbSensor>> entry : monitorTypeSensorMap.entrySet()) {
            Integer monitorType = entry.getKey();
            Integer statisticsMethods = null;
            // 库水位、流量：最新一条；降雨量：如果'显示密度'是全部则为'最新一条'，否则是'阶段变化'
            if (monitorType.equals(MonitorType.DRY_BEACH.getKey()) || monitorType.equals(MonitorType.WATER_LEVEL.getKey())) {
                statisticsMethods = StatisticalMethods.LATEST.getValue();
            } else if (monitorType.equals(MonitorType.WT_RAINFALL.getKey()) || monitorType.equals(MonitorType.RAINFALL.getKey())) {
                statisticsMethods = displayDensity.equals(DisplayDensity.ALL) ? StatisticalMethods.LATEST.getValue() : StatisticalMethods.CHANGE.getValue();
            }
            if (Objects.nonNull(statisticsMethods) && monitorTypeFieldMap.containsKey(monitorType)) {
                queryDataAddToRes(displayDensity, startTime, endTime, res, monitorType,
                        entry.getValue().stream().map(TbSensor::getID).toList(), monitorTypeFieldMap, statisticsMethods);
            }
        }
        return res;
    }

    private void queryDataAddToRes(DisplayDensity displayDensity, Date startTime, Date endTime,
                                   List<Map<String, Object>> res, Integer monitorType, List<Integer> sensorIDList,
                                   Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap, Integer statisticsMethods) {
        List<FieldBaseInfo> fieldBaseInfoList = monitorTypeFieldMap.get(monitorType).stream()
                .map(TbMonitorTypeField::getFieldToken).map(InfluxDBDataUtil::buildFieldBaseInfo).toList();
        List<Map<String, Object>> dataList = sensorDataDao.queryCommonSensorDataList(sensorIDList, startTime, endTime,
                displayDensity.getValue(), statisticsMethods, fieldBaseInfoList, monitorType);
        res.addAll(DisplayDensity.needExtraGrouping(displayDensity) ?
                InfluxDBDataUtil.calculateStatistics(dataList, displayDensity.getValue(), statisticsMethods, true) : dataList);
    }


    private @Nullable Double getAbnormalValue(final Tuple<Double, Double> limitInfo, @Nullable Double value) {
        Double abnormalValue = null;
        if (Objects.nonNull(value)) {
            Double curUpper = limitInfo.getItem1();
            Double curLower = limitInfo.getItem2();
            if (Objects.nonNull(curUpper) && value > curUpper) {
                abnormalValue = value - curUpper;
            } else if (Objects.nonNull(curLower) && value < curLower) {
                abnormalValue = value - curLower;
            }
        }
        return abnormalValue;
    }

    /**
     * 查询单个属性的最新数据，解析成{@code double}后进行对应操作
     *
     * @param sensorID            传感器ID
     * @param monitorType         监测类型
     * @param fieldSelectInfoList 子类型列表
     * @param fieldToken          属性fieldToken
     * @param action              操作
     */
    private void queryNewDryBeachData(final Integer sensorID, final Integer monitorType, final List<FieldSelectInfo> fieldSelectInfoList,
                                      final String fieldToken, Consumer<? super Double> action) {
        sensorDataDao.querySensorNewData(List.of(sensorID), fieldSelectInfoList, false, monitorType)
                .stream().filter(w -> w.containsKey(fieldToken)).findFirst().map(w -> w.get(fieldToken)).map(Convert::toDouble).ifPresent(action);
    }

    private @Nullable ThematicLongitudinalEigenValueData getMaxEigenValueData(final Double value, @Nullable final List<ThematicEigenValueInfo> eigenValueList) {
        return Optional.ofNullable(eigenValueList).flatMap(u -> u.stream().filter(w -> w.getEigenValue() < value)
                .reduce((o1, o2) -> o1.getEigenValue() > o2.getEigenValue() ? o1 : o2).map(w -> {
                    ThematicLongitudinalEigenValueData res = new ThematicLongitudinalEigenValueData();
                    BeanUtils.copyProperties(w, res);
                    res.setAbnormalValue(value - w.getEigenValue());
                    return res;
                })).orElse(null);
    }

    /**
     * 如果该双边监测点需要进行操作,则将其封装处理并调用{@param action}
     *
     * @param pointIDEigenValueMap 监测点ID,特征值数据map
     * @param needDealPointSet     需要被处理的双边监测点集
     * @param data                 流水数据
     * @param point                监测点数据
     * @param osmoticValue         阈值（可能为null）
     * @param action               操作
     */
    private void dealBilateralData(final Map<Integer, List<ThematicEigenValueInfo>> pointIDEigenValueMap,
                                   final Set<Integer> needDealPointSet, final Map<String, Object> data, final TbMonitorPoint point,
                                   @Nullable final Double osmoticValue, final Consumer<? super ThematicPipeData> action) {
        Optional.of(point).filter(u -> needDealPointSet.contains(u.getID())).map(u -> {
            ThematicPipeData.ThematicPipeDataBuilder builder = ThematicPipeData.builder().monitorPointID(u.getID()).monitorPointName(u.getName());
            Optional.of(data).filter(w -> w.containsKey(EMPTY_PIPE_DISTANCE)).map(w -> w.get(EMPTY_PIPE_DISTANCE))
                    .map(Convert::toDouble).ifPresent(builder::emptyPipeDistance);
            Optional.of(data).filter(w -> w.containsKey(LEVEL_ELEVATION)).map(w -> w.get(LEVEL_ELEVATION))
                    // gradle编译插件有bug，这里如果不加这个强转会编译失败
                    .map(Convert::toDouble).map(w -> (ThematicLevelElevationInfo) ThematicLevelElevationInfo.builder()
                            .value(w).osmoticValue(osmoticValue).eigenValue(
                                    getMaxEigenValueData(w, pointIDEigenValueMap.get(u.getID()))).build()).ifPresent(builder::levelElevation);
            return builder.build();
        }).ifPresent(action);
    }

    private String getSheetDemoCellData(FieldDataType type) {
        String res;
        switch (type) {
            case DOUBLE -> res = "20.000";
            case LONG -> res = "20";
            case BOOLEAN -> res = "true";
            case ARRAY -> res = "[]";
            default -> res = "";
        }
        return res;
    }

    /**
     * 查询该时间戳的最近数据
     *
     * @param source        时间戳
     * @param interval      时间间隔(ms)，超过这个时间间隔的数据都将被忽略
     * @param timestampList 目标数据的时间戳列表(已排序)
     * @param dataMap       key-数据时间戳,value-目标数据(目前value中的{@code object}数据仅可以取{@code Double}和{@code Long}类型的数据)
     * @return 有两种情况会返回{@code null}，一种是自动化数据为空，另一种是超出时间间隔{@code interval}的限制了
     */
    private @Nullable Tuple<Date, Object> findCloseData(long source, long interval, final List<Long> timestampList,
                                                        final Map<Long, Tuple<Date, Object>> dataMap) {
        if (CollUtil.isEmpty(timestampList)) {
            return null;
        }
        Long first = timestampList.get(0);
        if (first > source) {
            return source + interval >= first ? dataMap.get(first) : null;
        }
        Long last = timestampList.get(timestampList.size() - 1);
        if (last < source) {
            return source - interval <= last ? dataMap.get(last) : null;
        }
        for (int i = 0; i < timestampList.size() - 1; i++) {
            Long left = timestampList.get(i);
            Long right = timestampList.get(i + 1);
            if (left <= source && right >= source) {
                // 是否取了left
                boolean getLeft = source - left <= right - source;
                if (!((!getLeft || source - interval <= left) && (getLeft || source + interval >= right))) {
                    return null;
                }
                return getLeft && source - interval <= left ? dataMap.get(left) : dataMap.get(right);
            }
        }

        // unnecessary return
        return null;
    }
}
