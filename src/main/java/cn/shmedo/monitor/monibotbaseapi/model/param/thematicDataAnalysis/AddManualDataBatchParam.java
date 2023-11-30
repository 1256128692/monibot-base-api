package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.FieldDataType;
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
import lombok.Builder;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    /**
     * key: {@code monitorType}<br>
     * hash key: {@code fieldToken}<br>
     * value: {@code field}
     */
    @JsonIgnore
    private Map<Integer, Map<String, TbMonitorTypeField>> monitorTypeBasicFieldMap;
    @JsonIgnore
    private List<SensorIDWithFormulaBaseInfo> sensorFormulaDataList;
    @JsonIgnore
    private List<AddManualItem> insertDataList = new ArrayList<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper<?> validate() {
        List<Integer> list = dataList.stream().map(ManualDataItem::getSensorID).distinct().toList();
        // sensorID,monitorType and extend fields with formulas.
        sensorFormulaDataList = ContextHolder.getBean(TbSensorMapper.class).selectSensorIDWithFormulaBaseInfoBySensorIDList(list);
        if (sensorFormulaDataList.size() != list.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有人工传感器不存在!");
        }

        // 传感器参数map key - sensorID, hashKey - param token, value
        Map<Integer, Map<String, Double>> sensorParamValueMap = sensorFormulaDataList.stream()
                .filter(u -> u.getSensorParameterList().stream().anyMatch(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue())))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, u ->
                        u.getSensorParameterList().stream().filter(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue()))
                                .collect(Collectors.toMap(TbParameter::getToken, w ->
                                        Convert.toDouble(FieldDataType.valueOf(w.getDataType()).parseData(w.getPaValue()))))));

        // 模板参数map key - sensorID, hashKey - param token, value
        Map<Integer, Map<String, Double>> templateParamValueMap = sensorFormulaDataList.stream()
                .filter(u -> u.getTemplateParameterList().stream().anyMatch(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue())))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, u ->
                        u.getSensorParameterList().stream().filter(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue()))
                                .collect(Collectors.toMap(TbParameter::getToken, w ->
                                        Convert.toDouble(FieldDataType.valueOf(w.getDataType()).parseData(w.getPaValue()))))));

        Map<Integer, SensorIDWithFormulaBaseInfo> sensorIDDataMap = sensorFormulaDataList.stream()
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, Function.identity()));
        Map<Integer, Integer> sensorIDMonitorTypeMap = sensorFormulaDataList.stream().collect(Collectors
                .toMap(SensorIDWithFormulaBaseInfo::getSensorID, SensorIDWithFormulaBaseInfo::getMonitorType));
        dataList.stream().peek(u -> u.setMonitorType(sensorIDMonitorTypeMap.get(u.getSensorID()))).toList();

        // full fields
        List<TbMonitorTypeField> tbMonitorTypeFieldList = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .queryByMonitorTypes(sensorFormulaDataList.stream().map(SensorIDWithFormulaBaseInfo::getMonitorType).distinct().toList(), false);
        Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap = tbMonitorTypeFieldList.stream()
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));

        // only basic fields
        monitorTypeBasicFieldMap = tbMonitorTypeFieldList.stream()
                .filter(u -> Objects.nonNull(u.getFieldClass()) && u.getFieldClass().equals(FieldClass.BASIC.getCode()))
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType, Collectors.toMap(TbMonitorTypeField::getFieldToken, Function.identity())));
        // ensure all {@code fieldToken} legally.
        if (dataList.stream().anyMatch(u -> !monitorTypeBasicFieldMap.get(u.getMonitorType()).containsKey(u.getFieldToken()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前表格中存在异常数据，请核实后重新导入！");
        }

        //需要获取历史数据的传感器ID及部分查询参数的map
        //key - sensorID, value - 'monitorType'&&'fieldSelectInfoList'
        Map<Integer, QueryHistoryData> needQueryHistoryMap = sensorFormulaDataList.stream()
                .filter(u -> u.getFormulaBaseInfoList().stream().anyMatch(w -> w.getFormula().contains("history:")))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID,
                        v -> QueryHistoryData.builder().monitorType(v.getMonitorType()).fieldSelectInfoList(
                                monitorTypeFieldMap.get(v.getMonitorType()).stream().map(TbMonitorTypeField::getFieldToken)
                                        .map(InfluxDBDataUtil::buildFieldSelectInfo).toList()).build()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Map<Integer, Map<String, List<ManualDataItem>>> collect1 = dataList.stream()
                .filter(u -> needQueryHistoryMap.containsKey(u.getSensorID()))
                .collect(Collectors.groupingBy(ManualDataItem::getMonitorType,
                        Collectors.groupingBy(u -> DateUtil.format(u.getTime(), formatter))));
        List<QueryHistoryData> queryHistoryDataList = collect1.entrySet().stream().map(u -> u.getValue().entrySet().stream().map(w -> {
            QueryHistoryData queryHistoryData = needQueryHistoryMap.get(w.getValue().get(0).getSensorID());
            queryHistoryData.setMonitorType(u.getKey());
            queryHistoryData.setEnd(new Timestamp(DateUtil.parse(w.getKey(), formatter).getTime()));
            queryHistoryData.setSensorIDList(w.getValue().stream().map(ManualDataItem::getSensorID).distinct().toList());
            return queryHistoryData;
        }).toList()).flatMap(Collection::stream).toList();

        // TODO start
        // 尝试从influxDB获取所需的全部历史数据（公式表中含有'history:'的传感器即认为是需要查询上传时间前最新一条历史数据的传感器）
        // 分析出当前记录里可作为历史数据的数据并封装到 map: key-sensorID::yyyy-MM-dd HH:mm:ss.SSS  value-sensorID::yyyy-MM-dd HH:mm:ss.SSS 中（当前数据,历史数据）
        // 查询到的历史数据中，如果导入数据中有sensorID都相同、导入时间大于等于历史记录的数据，以当前导入的数据为历史数据(插入时会覆盖)
        // 最终都没有历史的记录，历史扩展属性值置为0

        // importList --> sid, time, other_data
        // historyList --> sid, time, history_data


        // import(sensorID && time), history(object)
        Map<String, Map<String, Object>> historyDataMap = new HashMap<>();


        // history(sensorID && time), import(sensorID && time)
        Map<String, String> historyImportDataMap = new HashMap<>();
        // history( sensorID,timestamp_list )
        Map<Integer, List<Long>> sensorIDHistoryTimeMap = new HashMap<>();
        SensorDataDao sensorDataDao = ContextHolder.getBean(SensorDataDao.class);
        for (QueryHistoryData queryHistoryData : queryHistoryDataList) {
            List<Integer> sensorIDList = queryHistoryData.getSensorIDList();
            Timestamp time = queryHistoryData.getEnd();

            // sensorID, historyData
            Map<Integer, Map<String, Object>> collect = sensorDataDao.querySensorNewDataBefore(
                    sensorIDList, time, queryHistoryData.getFieldSelectInfoList(),
                    false, queryHistoryData.getMonitorType()).stream().collect(Collectors
                    .toMap(u -> Convert.toInt(u.get(DbConstant.SENSOR_ID_FIELD_TOKEN)), Function.identity()));

            //TODO
            List<Integer> containsHistorySensorIDList = sensorIDList.stream().filter(collect::containsKey).toList();
            historyDataMap.putAll(containsHistorySensorIDList.stream().collect(Collectors.toMap(k ->
                            k + "&&" + DateUtil.parse(collect.get(k).get(DbConstant.TIME_FIELD).toString(), formatter)
                    , collect::get)));



//            historyImportDataMap.putAll();
//            sensorIDHistoryTimeMap.putAll();
        }
        historyDataMap.keySet().stream().collect(Collectors.toMap(
                k -> Convert.toInt(StrUtil.subBefore(k, "&&", false)),
                v -> DateUtil.parse(StrUtil.subAfter(v, "&&", false)).getTime()));

        // TODO end
        Map<Integer, List<ManualDataItem>> collect = dataList.stream().collect(Collectors.groupingBy(ManualDataItem::getMonitorType));
        for (Map.Entry<Integer, List<ManualDataItem>> entry : collect.entrySet()) {
            Integer monitorType = entry.getKey();
            int fieldCount = monitorTypeFieldMap.get(monitorType).size();
            Map<String, TbMonitorTypeField> fieldTokenMap = monitorTypeBasicFieldMap.get(monitorType);
            List<Map<String, Object>> singleInsertDataList;
            try {
                singleInsertDataList = entry.getValue().stream().collect(Collectors.groupingBy(u ->
                        DateUtil.format(u.getTime(), formatter))).entrySet().stream().map(u -> {
                    // overrides item if same.
                    return u.getValue().stream().collect(Collectors.groupingBy(ManualDataItem::getSensorID)).entrySet().stream()
                            .map(w -> {
                                Integer sensorID = w.getKey();
                                Map<String, Double> sensorParamTokenValueMap = sensorParamValueMap.get(sensorID);
                                Map<String, Double> templateParamTokenValueMap = templateParamValueMap.get(sensorID);

                                // basic field
                                Map<String, Object> data = w.getValue().stream().collect(Collectors.toMap(ManualDataItem::getFieldToken,
                                        v -> FieldDataType.valueOfString(fieldTokenMap.get(v.getFieldToken()).getFieldDataType()).parseData(v.getValue()),
                                        (o, o2) -> o2));

                                // extend field
                                Optional.of(sensorID).map(sensorIDDataMap::get).filter(s -> CollUtil.isNotEmpty(s.getFormulaBaseInfoList())).ifPresent(s -> {
                                    switch (CalType.codeOf(s.getCalType())) {
                                        case FORMULA -> s.getFormulaBaseInfoList().stream().sorted((o1, o2) ->
                                                Comparator.comparing(FormulaBaseInfo::getFieldCalOrder, Comparator.nullsLast(Integer::compareTo))
                                                        .compare(o1, o2)).peek(n -> data.put(n.getFieldToken(),
                                                FormulaUtil.calculate(n.getFormula(), c -> {
                                                    // @author cfs
                                                    c.forEach((type, dataList) -> {
                                                                switch (type) {
                                                                    case SELF:
                                                                        dataList.forEach(source -> {
                                                                            if (FormulaData.Provide.DATA.equals(source.getProvide())) {
                                                                                //${self:self.cNowSpeed}
                                                                                source.setFieldValue(Convert.toDouble(data.get(source.getFieldToken())));
                                                                            } else {
                                                                                //${self:self.time:format=unixSecond}
                                                                                source.setFieldValue(DateUtil.parse(u.getKey(), formatter));
                                                                            }
                                                                        });
                                                                        break;
                                                                    case HISTORY:
                                                                        dataList.forEach(source -> {
                                                                            //TODO get history {@code data2}
                                                                            Map<String, Object> data2 = Map.of();

                                                                            if (FormulaData.Provide.DATA.equals(source.getProvide())) {
                                                                                source.setFieldValue(data2.containsKey(source.getFieldToken()) ?
                                                                                        Convert.toDouble(data2.get(source.getFieldToken())) : 0D);
                                                                            } else {
                                                                                if (data2.containsKey(DbConstant.TIME_FIELD)) {
                                                                                    source.setFieldValue(Convert.toDouble(data2.get(DbConstant.TIME_FIELD)));
                                                                                } else {
                                                                                    source.setFieldValue(DateUtil.parse(u.getKey(), formatter));
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
                                                                        throw new IllegalArgumentException("--");
                                                                }
                                                            }
                                                    );
                                                }))).toList();
                                        case SCRIPT, HTTP -> log.error("脚本计算/HTTP计算暂未实现");
                                        default -> {
                                            String errorCalTypeDesc = CalType.codeOf(s.getCalType()).getDesc();
                                            log.error("错误的计算类型:{},\t描述:{}", s.getCalType(), errorCalTypeDesc);
                                            throw new IllegalArgumentException(errorCalTypeDesc + " 类型不应该作为扩展配置的计算类型!");
                                        }
                                    }
                                });
                                data.put(DbConstant.TIME_FIELD, u.getKey());
                                data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, sensorID);
                                return data;
                            }).toList();
                }).flatMap(Collection::stream).toList();
            } catch (IllegalArgumentException e) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, e.getMessage());
            }

            // ensure each item in {@code singleInsertDataList} contains all {@code filedToken}
            if (singleInsertDataList.stream().anyMatch(u -> u.keySet().size() != fieldCount)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前表格中存在异常数据，请核实后重新导入！");
            }
            List<String> fieldTokenList = fieldTokenMap.values().stream().map(TbMonitorTypeField::getFieldToken).toList();
            insertDataList.add(AddManualItem.builder().dataList(singleInsertDataList).fieldTokenList(fieldTokenList).monitorType(monitorType).build());
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Data
    @Builder
    private static class QueryHistoryData {
        private Integer monitorType;
        private List<Integer> sensorIDList;
        private List<FieldSelectInfo> fieldSelectInfoList;
        private Timestamp end;
    }
}