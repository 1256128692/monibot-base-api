package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.FieldDataType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 10:00
 */
@Data
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
    private Map<Integer, Map<String, TbMonitorTypeField>> monitorTypeFieldMap;
    @JsonIgnore
    private List<TbSensor> sensorList;
    @JsonIgnore
    private List<AddManualItem> insertDataList = new ArrayList<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper<?> validate() {
        List<Integer> list = dataList.stream().map(ManualDataItem::getSensorID).distinct().toList();
        sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getProjectID, projectID).in(TbSensor::getID, list));
        if (sensorList.size() != list.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在!");
        }
        Map<Integer, Integer> sensorIDMonitorTypeMap = sensorList.stream().collect(Collectors.toMap(TbSensor::getID, TbSensor::getMonitorType));
        dataList.stream().peek(u -> u.setMonitorType(sensorIDMonitorTypeMap.get(u.getSensorID()))).toList();
        List<TbMonitorTypeField> tbMonitorTypeFieldList = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .queryByMonitorTypes(sensorList.stream().map(TbSensor::getMonitorType).distinct().toList(), false);
        monitorTypeFieldMap = tbMonitorTypeFieldList.stream()
                .filter(u -> Objects.nonNull(u.getFieldClass()) && u.getFieldClass().equals(FieldClass.BASIC.getCode()))
                .collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType)).entrySet().stream()
                .map(u -> new Tuple<>(u.getKey(), u.getValue().stream().collect(Collectors.toMap(TbMonitorTypeField::getFieldToken, Function.identity()))))
                .collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));
        // ensure all {@code fieldToken} legally.
        if (dataList.stream().anyMatch(u -> !monitorTypeFieldMap.get(u.getMonitorType()).containsKey(u.getFieldToken()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前表格中存在异常数据，请核实后重新导入！");
        }

        Map<Integer, List<ManualDataItem>> collect = dataList.stream().collect(Collectors.groupingBy(ManualDataItem::getMonitorType));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        for (Map.Entry<Integer, List<ManualDataItem>> entry : collect.entrySet()) {
            Integer monitorType = entry.getKey();
            Map<String, TbMonitorTypeField> fieldTokenMap = monitorTypeFieldMap.get(monitorType);
            List<Map<String, Object>> singleInsertDataList = entry.getValue().stream().collect(Collectors.groupingBy(u ->
                    DateUtil.format(u.getTime(), formatter))).entrySet().stream().map(u -> {
                // overrides item if same.
                return u.getValue().stream().collect(Collectors.groupingBy(ManualDataItem::getSensorID)).entrySet().stream()
                        .map(w -> {
                            Map<String, Object> data = w.getValue().stream().collect(Collectors.toMap(ManualDataItem::getFieldToken,
                                    v -> FieldDataType.valueOfString(fieldTokenMap.get(v.getFieldToken()).getFieldDataType()).parseData(v.getValue()),
                                    (o, o2) -> o2));
                            data.put(DbConstant.TIME_FIELD, u.getKey());
                            data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, w.getKey());
                            return data;
                        }).toList();
            }).flatMap(Collection::stream).toList();
            // ensure each item in {@code singleInsertDataList} contains all {@code filedToken}
            if (singleInsertDataList.stream().anyMatch(u -> u.keySet().size() != fieldTokenMap.keySet().size())) {
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
