package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.FieldDataType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.FormulaBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.SensorIDWithFormulaBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaUtil;
import cn.shmedo.monitor.monibotbaseapi.util.formula.Origin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import lombok.extern.slf4j.Slf4j;

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

        // key - sensorID, hashKey - param token, value
        Map<Integer, Map<String, Double>> sensorParamValueMap = sensorFormulaDataList.stream()
                .filter(u -> u.getParameterList().stream().anyMatch(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue())))
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, u ->
                        u.getParameterList().stream().filter(w -> !DbConstant.PARAM_PAVALUE_EMPTY.equals(w.getPaValue()))
                                .collect(Collectors.toMap(TbParameter::getToken, w ->
                                        Convert.toDouble(FieldDataType.valueOf(w.getDataType()).parseData(w.getPaValue()))))));

//        // key - sensorID, hashkey - param token, value
//        Map<Integer, Map<String, Double>> sensorIDTypeMap = sensorFormulaDataList.stream().flatMap(u -> {
//            Integer sensorID = u.getSensorID();
//
//            Optional.ofNullable(sensorParamValueMap.get(sensorID)).map(w -> {
//                Map<Origin.Type, Set<FormulaData>> map = u.getFormulaBaseInfoList().stream().map(FormulaBaseInfo::getFormula)
//                        .map(FormulaUtil::parse).flatMap(Collection::stream).distinct()
//                        .collect(Collectors.toMap(FormulaData::getType,
//                                s -> {
//                                    // manual sensor contains not {@code sourceToken},so acquire the {@code token} which belongs param is enough.
//                                    String paramToken = StrUtil.subAfter(s.getSourceToken(), StrUtil.UNDERLINE, false);
//                                    Double val = w.get(paramToken);
//                                }
//                        ))
//                        .map(s -> {}).map()
//            })
//        }).collect(Collectors.toMap());

        Map<Integer, SensorIDWithFormulaBaseInfo> sensorIDDataMap = sensorFormulaDataList.stream()
                .collect(Collectors.toMap(SensorIDWithFormulaBaseInfo::getSensorID, Function.identity()));
        Map<Integer, Integer> sensorIDMonitorTypeMap = sensorFormulaDataList.stream().collect(Collectors
                .toMap(SensorIDWithFormulaBaseInfo::getSensorID, SensorIDWithFormulaBaseInfo::getMonitorType));
        dataList.stream().peek(u -> u.setMonitorType(sensorIDMonitorTypeMap.get(u.getSensorID()))).toList();

        // full fields
        List<TbMonitorTypeField> tbMonitorTypeFieldList = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .queryByMonitorTypes(sensorFormulaDataList.stream().map(SensorIDWithFormulaBaseInfo::getMonitorType).distinct().toList(), false);
        Map<Integer, Long> monitorTypeFieldCountMap = tbMonitorTypeFieldList.stream()
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType, Collectors.counting()));

        // only basic fields
        monitorTypeBasicFieldMap = tbMonitorTypeFieldList.stream()
                .filter(u -> Objects.nonNull(u.getFieldClass()) && u.getFieldClass().equals(FieldClass.BASIC.getCode()))
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType, Collectors.toMap(TbMonitorTypeField::getFieldToken, Function.identity())));
        // ensure all {@code fieldToken} legally.
        if (dataList.stream().anyMatch(u -> !monitorTypeBasicFieldMap.get(u.getMonitorType()).containsKey(u.getFieldToken()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前表格中存在异常数据，请核实后重新导入！");
        }

        Map<Integer, List<ManualDataItem>> collect = dataList.stream().collect(Collectors.groupingBy(ManualDataItem::getMonitorType));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        for (Map.Entry<Integer, List<ManualDataItem>> entry : collect.entrySet()) {
            Integer monitorType = entry.getKey();
            Long fieldCount = monitorTypeFieldCountMap.get(monitorType);
            Map<String, TbMonitorTypeField> fieldTokenMap = monitorTypeBasicFieldMap.get(monitorType);
            List<Map<String, Object>> singleInsertDataList;
            try {
                singleInsertDataList = entry.getValue().stream().collect(Collectors.groupingBy(u ->
                        DateUtil.format(u.getTime(), formatter))).entrySet().stream().map(u -> {
                    // overrides item if same.
                    return u.getValue().stream().collect(Collectors.groupingBy(ManualDataItem::getSensorID)).entrySet().stream()
                            .map(w -> {
                                Integer sensorID = w.getKey();
                                Map<String, Double> paramTokenValueMap = sensorParamValueMap.get(sensorID);

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
                                                //TODO 不能直接使用该方法，因为该方法没有处理过历史数据，因此需要重写一个方法进行计算
                                                FormulaUtil.calculate(n.getFormula(), c -> c.forEach((k, v) ->
                                                        v.forEach(m -> m.setFieldValue(Origin.Type.SELF.equals(k) ?
                                                                Convert.toDouble(data.get(m.getFieldToken())) :
                                                                Optional.ofNullable(paramTokenValueMap.get(m.getFieldToken()))
                                                                        .orElseThrow(() -> new IllegalArgumentException("参数表中没有" + m.getFieldToken() + "参数或其默认值为空!")))))))).toList();
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

            //TODO 对{@code insertDataList}的历史数据进行处理
            //先
            //先尝试从获取所需的全部历史数据（没有历史的记录，历史扩展属性值置为0；本次导入数据中有能作为）
            //

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
}
