package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.FieldDataType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.FormulaBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.SensorIDWithFormulaBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.util.InfluxDBDataUtil;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaData;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 10:00
 */
@Data
@Slf4j
public class AddManualDataBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "数据列表不能为空")
    private List<@Valid ManualDataItem> dataList;
    @JsonIgnore
    private List<AddManualItem> insertDataList;
    /**
     * InfluxDB使用的时间格式
     */
    @JsonIgnore
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper<?> validate() {
        List<Integer> sensorIDList = dataList.stream().map(ManualDataItem::getSensorID).distinct().toList();
        // sensorID,monitorType and extend fields with formulas.
        List<SensorIDWithFormulaBaseInfo> sensorFormulaDataList = ContextHolder.getBean(TbSensorMapper.class).selectManualSensorIDWithFormulaBaseInfoBySensorIDList(sensorIDList);
        if (sensorFormulaDataList.size() != sensorIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有人工传感器不存在!");
        }

        // key-{@code sensorID}, hashKey-{@code paramToken}
        Map<Integer, Map<String, Double>> sensorParamValueMap = sensorFormulaDataList.stream()
                .filter(u -> u.getSensorParameterList().stream().anyMatch(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue())))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, u ->
                        u.getSensorParameterList().stream().filter(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue()))
                                .collect(Collectors.toMap(TbParameter::getToken, w ->
                                        Convert.toDouble(FieldDataType.valueOfString(w.getDataType()).parseData(w.getPaValue()))))));

        // key-{@code sensorID}, hashKey-{@code paramToken}
        Map<Integer, Map<String, Double>> templateParamValueMap = sensorFormulaDataList.stream()
                .filter(u -> u.getTemplateParameterList().stream().anyMatch(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue())))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, u ->
                        u.getSensorParameterList().stream().filter(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue()))
                                .collect(Collectors.toMap(TbParameter::getToken, w ->
                                        Convert.toDouble(FieldDataType.valueOfString(w.getDataType()).parseData(w.getPaValue()))))));

        Map<Integer, SensorIDWithFormulaBaseInfo> sensorIDDataMap = sensorFormulaDataList.stream()
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, Function.identity()));

        // fill monitorType
        Map<Integer, Integer> sensorIDMonitorTypeMap = sensorFormulaDataList.stream().collect(Collectors
                .toMap(SensorIDWithFormulaBaseInfo::getSensorID, SensorIDWithFormulaBaseInfo::getMonitorType));
        dataList.stream().peek(u -> u.setMonitorType(sensorIDMonitorTypeMap.get(u.getSensorID()))).toList();

        // full fields
        List<TbMonitorTypeField> tbMonitorTypeFieldList = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .queryByMonitorTypes(sensorFormulaDataList.stream().map(SensorIDWithFormulaBaseInfo::getMonitorType).distinct().toList(), false);
        Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap = tbMonitorTypeFieldList.stream()
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));

        // only basic fields
        Map<Integer, Map<String, TbMonitorTypeField>> monitorTypeBasicFieldMap = tbMonitorTypeFieldList.stream()
                .filter(u -> Objects.nonNull(u.getFieldClass()) && u.getFieldClass().equals(FieldClass.BASIC.getCode()))
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType, Collectors.toMap(TbMonitorTypeField::getFieldToken, Function.identity())));
        // ensure all {@code fieldToken} legally.
        if (dataList.stream().anyMatch(u -> !monitorTypeBasicFieldMap.get(u.getMonitorType()).containsKey(u.getFieldToken()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前表格中存在异常数据，请核实后重新导入！");
        }

        //key-{@code sensorID}, value is contains {@code monitorType}&{@code fieldSelectInfoList} only(incomplete now).
        Map<Integer, QueryHistoryData> needQueryHistoryMap = sensorFormulaDataList.stream()
                .filter(u -> u.getFormulaBaseInfoList().stream().anyMatch(w -> w.getFormula().contains("history:")))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID,
                        v -> QueryHistoryData.builder().monitorType(v.getMonitorType()).fieldSelectInfoList(
                                monitorTypeFieldMap.get(v.getMonitorType()).stream().map(TbMonitorTypeField::getFieldToken)
                                        .map(InfluxDBDataUtil::buildFieldSelectInfo).toList()).build()));

        // only basic fields,key-{@code monitorType}, secondkey-{@code sensorID}, thirdKey-{@code date}, value-{@code data}
        Map<Integer, Map<Integer, Map<Date, Map<String, Object>>>> singleInsertDataList = dataList.stream().collect(
                Collectors.groupingBy(ManualDataItem::getMonitorType,
                        Collectors.groupingBy(ManualDataItem::getSensorID,
                                Collectors.groupingBy(ManualDataItem::getTime)))).entrySet().stream().map(u -> {
            Map<String, TbMonitorTypeField> fieldTokenMap = monitorTypeBasicFieldMap.get(u.getKey());
            Map<Integer, Map<Date, Map<String, Object>>> collect = u.getValue().entrySet().stream().map(w ->
                    new Tuple<>(w.getKey(), w.getValue().entrySet().stream().map(s -> {
                        Map<String, Object> data = s.getValue().stream().collect(Collectors.toMap(ManualDataItem::getFieldToken,
                                v -> FieldDataType.valueOfString(fieldTokenMap.get(v.getFieldToken()).getFieldDataType()).parseData(v.getValue()),
                                (o1, o2) -> o2));
                        data.put(DbConstant.TIME_FIELD, DateUtil.format(s.getKey(), FORMATTER));
                        data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, w.getKey());
                        return new Tuple<>(s.getKey(), data);
                    }).collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2)))).collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));
            return new Tuple<>(u.getKey(), collect);
        }).collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));

        // build two indexs which base on import dataList.
        Map<Integer, List<Map<String, Object>>> importDataMap = singleInsertDataList.values()
                .stream().map(Map::values).flatMap(Collection::stream).map(Map::values).flatMap(Collection::stream)
                .filter(u -> needQueryHistoryMap.containsKey(Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN))))
                .collect(Collectors.groupingBy(u -> Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN))));

        // key-{@code sensorID}, hashkey-{@code newDate}, value-{@code oldDate}
        Map<Integer, Map<String, String>> importHistoryIdxDate = importDataMap.entrySet().stream()
                .map(u -> {
                    List<Map<String, Object>> list = u.getValue().stream().sorted(Comparator.comparingLong(w ->
                            DateUtil.parse(w.get(DbConstant.TIME_FIELD).toString(), FORMATTER).getTime())).toList();
                    Map<String, String> map = IntStream.range(1, list.size()).boxed().collect(Collectors.toMap(
                            i -> list.get(i).get(DbConstant.TIME_FIELD).toString(),
                            i -> list.get(i - 1).get(DbConstant.TIME_FIELD).toString(), (o1, o2) -> o1, HashMap::new));
                    map.put(list.get(0).get(DbConstant.TIME_FIELD).toString(), null);
                    return new Tuple<>(u.getKey(), map);
                }).collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));

        // key-{@code sensorID}, hashkey-{@code date}, value-{@code data}
        Map<Integer, Map<String, Map<String, Object>>> importHistoryIdxData = importDataMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, u ->
                        u.getValue().stream().collect(Collectors.toMap(k ->
                                k.get(DbConstant.TIME_FIELD).toString(), Function.identity(), (o1, o2) -> o2))));

        // build two indexs which base on influxDB dataList.
        // key-{@code sensorID}, hashkey-{@code newDate}, value-{@code oldDate}
        Map<Integer, Map<String, String>> influxHistoryIdxDate = new HashMap<>();

        // key-{@code sensorID}, hashkey-{@code date}, value-{@code data}
        Map<Integer, Map<String, Map<String, Object>>> influxHistoryIdxData = new HashMap<>();
        List<QueryHistoryData> queryHistoryDataList = dataList.stream().filter(u -> needQueryHistoryMap.containsKey(u.getSensorID()))
                .collect(Collectors.groupingBy(ManualDataItem::getMonitorType,
                        Collectors.groupingBy(u -> DateUtil.format(u.getTime(), FORMATTER))))
                .entrySet().stream().map(u -> u.getValue().entrySet().stream().map(w -> {
                    QueryHistoryData queryHistoryData = QueryHistoryData.builder().build();
                    BeanUtil.copyProperties(needQueryHistoryMap.get(w.getValue().get(0).getSensorID()), queryHistoryData);
                    queryHistoryData.setMonitorType(u.getKey());
                    queryHistoryData.setEnd(new Timestamp(DateUtil.parse(w.getKey(), FORMATTER).getTime()));
                    queryHistoryData.setSensorIDList(w.getValue().stream().map(ManualDataItem::getSensorID).distinct().toList());
                    return queryHistoryData;
                }).toList()).flatMap(Collection::stream).toList();
        SensorDataDao sensorDataDao = ContextHolder.getBean(SensorDataDao.class);
        for (QueryHistoryData queryHistoryData : queryHistoryDataList) {
            Timestamp timestamp = queryHistoryData.getEnd();
            List<Map<String, Object>> maps = sensorDataDao.querySensorNewDataBefore(queryHistoryData.getSensorIDList(), timestamp, queryHistoryData.getFieldSelectInfoList(), false, queryHistoryData.getMonitorType());

            maps.stream().peek(u -> {
                Integer sensorID = Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN));
                String oldDateStr = u.get(DbConstant.TIME_FIELD).toString();

                Map<String, String> map1 = influxHistoryIdxDate.containsKey(sensorID) ? influxHistoryIdxDate.get(sensorID) : new HashMap<>();
                map1.put(DateUtil.format(timestamp, FORMATTER), oldDateStr);
                influxHistoryIdxDate.put(sensorID, map1);

                Map<String, Map<String, Object>> map2 = influxHistoryIdxData.containsKey(sensorID) ? influxHistoryIdxData.get(sensorID) : new HashMap<>();
                map2.put(oldDateStr, u);
                influxHistoryIdxData.put(sensorID, map2);
            }).toList();
        }

        // data parse
        try {
            insertDataList = singleInsertDataList.entrySet().stream().map(u -> {
                Integer monitorType = u.getKey();
                int fieldCount = monitorTypeFieldMap.get(monitorType).size();
                List<Map<String, Object>> list = u.getValue().entrySet().stream().map(w -> {
                    Integer sensorID = w.getKey();
                    Map<String, String> importIdxDate = importHistoryIdxDate.get(sensorID);
                    Map<String, Map<String, Object>> importIdxData = importHistoryIdxData.get(sensorID);
                    Map<String, String> influxIdxDate = influxHistoryIdxDate.get(sensorID);
                    Map<String, Map<String, Object>> influxIdxData = influxHistoryIdxData.get(sensorID);
                    Map<String, Double> sensorParamTokenValueMap = sensorParamValueMap.get(sensorID);
                    Map<String, Double> templateParamTokenValueMap = templateParamValueMap.get(sensorID);

                    return w.getValue().entrySet().stream().sorted(Comparator.comparingLong(s -> s.getKey().getTime())).map(s -> {
                        Date time = s.getKey();
                        Map<String, Object> data = s.getValue();
                        // parse extend fields
                        Optional.of(sensorID).map(sensorIDDataMap::get).filter(n -> CollUtil.isNotEmpty(n.getFormulaBaseInfoList())).ifPresent(n -> {
                            switch (CalType.codeOf(n.getCalType())) {
                                case FORMULA -> n.getFormulaBaseInfoList().stream()
                                        .sorted((o1, o2) -> Comparator.comparing(FormulaBaseInfo::getFieldCalOrder, Comparator.nullsLast(Integer::compareTo)).compare(o1, o2))
                                        .peek(m -> {
                                            Double calculate = FormulaUtil.calculate(m.getFormula(), process -> {
                                                // @author cfs
                                                process.forEach((type, dataList) -> {
                                                    switch (type) {
                                                        case SELF:
                                                            dataList.forEach(source -> {
                                                                if (FormulaData.Provide.DATA.equals(source.getProvide())) {
                                                                    //${self:self.cNowSpeed}
                                                                    source.setFieldValue(Convert.toDouble(data.get(source.getFieldToken())));
                                                                } else {
                                                                    //${self:self.time:format=unixSecond}
                                                                    source.setFieldValue(time);
                                                                }
                                                            });
                                                            break;
                                                        case HISTORY:
                                                            dataList.forEach(source -> {
                                                                String timeStr = DateUtil.format(time, FORMATTER);
                                                                Map<String, Object> historyData = Optional.ofNullable(importIdxDate).map(o -> o.get(timeStr)).map(s1 ->
                                                                                Optional.ofNullable(influxIdxDate).map(o -> o.get(timeStr)).map(s2 ->
                                                                                                DateUtil.parse(s1, FORMATTER).getTime() >= DateUtil.parse(s2, FORMATTER).getTime() ?
                                                                                                        Optional.ofNullable(importIdxData).map(o -> o.get(s1)) : Optional.ofNullable(influxIdxData).map(o -> o.get(s2)))
                                                                                        .orElse(Optional.ofNullable(importIdxData).map(o -> o.get(importIdxDate.get(timeStr)))))
                                                                        .orElse(Optional.ofNullable(influxIdxData).filter(o -> Objects.nonNull(influxIdxDate) && influxIdxDate.containsKey(timeStr)).map(o -> o.get(influxIdxDate.get(timeStr)))).orElse(Map.of());
                                                                if (FormulaData.Provide.DATA.equals(source.getProvide())) {
                                                                    source.setFieldValue(historyData.containsKey(source.getFieldToken()) ? Convert.toDouble(historyData.get(source.getFieldToken())) : 0D);
                                                                } else {
                                                                    if (historyData.containsKey(DbConstant.TIME_FIELD)) {
                                                                        source.setFieldValue(DateUtil.parse(Convert.toStr(historyData.get(DbConstant.TIME_FIELD)), FORMATTER));
                                                                    } else {
                                                                        source.setFieldValue(time);
                                                                    }
                                                                }
                                                            });
                                                            break;
                                                        case PARAM:
                                                            dataList.forEach(source -> {
                                                                if (sensorParamTokenValueMap.containsKey(source.getFieldToken())) {
                                                                    source.setFieldValue(sensorParamTokenValueMap.get(source.getFieldToken()));
                                                                } else if (templateParamTokenValueMap.containsKey(source.getFieldToken())) {
                                                                    source.setFieldValue(templateParamTokenValueMap.get(source.getFieldToken()));
                                                                }
                                                            });
                                                            break;
                                                        default:
                                                            throw new IllegalArgumentException("Unsupported type,type:" + type.name());
                                                    }
                                                });
                                            });
                                            // 这里有一个和中台的解算服务相异的业务场景，导入时可能会是第一条，因此要将需要历史数据的数据置为0D
                                            // 中台的解算服务是从物联网平台取历史数据（物联网平台仅有基础属性，不存在公式解算），物联网平台如果没有历史数据，解算报错；如果有则正常解算
                                            data.put(m.getFieldToken(), calculate.isNaN() ? 0D : calculate);
                                        }).toList();
                                case SCRIPT, HTTP -> log.error("脚本计算/HTTP计算暂未实现");
                                default -> {
                                    String errorCalTypeDesc = CalType.codeOf(n.getCalType()).getDesc();
                                    log.error("错误的计算类型:{},\t描述:{}", n.getCalType(), errorCalTypeDesc);
                                    throw new IllegalArgumentException(errorCalTypeDesc + " 类型不应该作为扩展配置的计算类型!");
                                }
                            }
                        });
                        return data;
                    }).toList();
                }).flatMap(Collection::stream).toList();
                // fieldCount + sensorID(1) + time(1)
                if (list.stream().anyMatch(w -> w.keySet().size() != fieldCount + 2)) {
                    throw new IllegalArgumentException("当前表格中存在异常数据，请核实后重新导入！");
                }
                return AddManualItem.builder().dataList(list).monitorType(monitorType)
                        .fieldTokenList(monitorTypeFieldMap.get(monitorType).stream().map(TbMonitorTypeField::getFieldToken).toList()).build();
            }).toList();
        } catch (IllegalArgumentException e) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, e.getMessage());
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class QueryHistoryData {
        private Integer monitorType;
        private List<Integer> sensorIDList;
        private List<FieldSelectInfo> fieldSelectInfoList;
        private Timestamp end;
    }
}