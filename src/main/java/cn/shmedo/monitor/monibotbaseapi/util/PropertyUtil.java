package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.ModelItem;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 16:08
 **/
public class PropertyUtil {
    /**
     * 校验新增的模板的属性
     * @param modelPropertyList
     * @return
     */
    public static ResultWrapper validate(List<ModelItem> modelPropertyList){
        if (modelPropertyList.stream().anyMatch(item -> {
            if (!item.getType().equals(PropertyType.TYPE_NUMBER.getType()) && ObjectUtil.isNotEmpty(item.getUnit())){
                return true;
            }
            return false;
        })){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "非数值类型不能设置单位");
        }
        if (modelPropertyList.stream().anyMatch(item -> {
            // 枚举类型
            if (item.getType().equals(PropertyType.TYPE_ENUM.getType())){
                if (ObjectUtil.hasEmpty(item.getMultiSelect(),item.getEnumField())){
                    return true;
                }
            }else {
                if (!ObjectUtil.isAllEmpty(item.getMultiSelect(),item.getEnumField())){
                    return true;
                }
            }
            return false;
        })){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "枚举类型需要设置多选和枚举内容，其他类型则不可设置");
        }
        if (modelPropertyList.stream().anyMatch(item -> {
            // 枚举类型
            if (item.getType().equals(PropertyType.TYPE_ENUM.getType())){
                if (!JSONUtil.isTypeJSONArray(item.getEnumField())){
                    return true;
                }
            }
            return false;
        })){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "枚举类型的枚举内容需要是json数组");
        }
        if (modelPropertyList.stream().anyMatch(item -> {
            // 枚举类型
            if (item.getType().equals(PropertyType.TYPE_ENUM.getType())){
                // 枚举类型上限为10
                if (JSONUtil.toList(item.getEnumField(), String.class).size() >10){
                    return true;
                }
            }
            return false;
        })){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "枚举类型的枚举内容上限为10");
        }
        return  null;

    }

    /**
     * 校验模板与值, 包括必填，枚举，日期，数值
     * @param modelValueList
     * @param properties
     * @param required 必填项必须设置,
     * @return
     */
    public static  ResultWrapper  validPropertyValue(List<PropertyIdAndValue> modelValueList, List<TbProperty> properties , boolean required){
        Map<Integer, PropertyIdAndValue> idAndValueMap = modelValueList.stream().collect(Collectors.toMap(PropertyIdAndValue::getID, Function.identity()));
        // 校验必填
        boolean b1 =required? properties.stream().filter(item -> !item.getRequired())
                .anyMatch(item -> {
                    PropertyIdAndValue temp = idAndValueMap.get(item.getID());
                    if (temp == null || ObjectUtil.isEmpty(temp.getValue())) {
                        return true;
                    }
                    return false;
                }):
                properties.stream().filter(item -> !item.getRequired())
                        .anyMatch(item -> {
                            PropertyIdAndValue temp = idAndValueMap.get(item.getID());
                            if (temp!=null && ObjectUtil.isEmpty(temp.getValue())) {
                                return true;
                            }
                            return false;
                        });
        if (b1){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性值必填项未填入");
        }
        // 校验枚举
        boolean b2  = required?
                 properties.stream().filter(item -> item.getType().equals(PropertyType.TYPE_ENUM.getType()))
                .anyMatch(item -> {
                    PropertyIdAndValue temp = idAndValueMap.get(item.getID());
                    if (item.getRequired() && temp == null){
                        return false;
                    }
                    JSONArray enums = JSONUtil.parseArray(item.getEnumField());

                    if (item.getMultiSelect()){
                        if (temp == null || !JSONUtil.isTypeJSONArray(temp.getValue()) ||!enums.contains(JSONUtil.parseArray(temp.getValue()))) {
                            return true;
                        }
                    }else {
                        if (temp == null || !enums.contains(temp.getValue())) {
                            return true;
                        }
                    }
                    return false;
                })
                :
                 properties.stream().filter(item -> item.getType().equals(PropertyType.TYPE_ENUM.getType()))
                .anyMatch(item -> {
                    PropertyIdAndValue temp = idAndValueMap.get(item.getID());
                    if (temp != null&& temp.getValue()!=null) {
                        JSONArray enums = JSONUtil.parseArray(item.getEnumField());
                        if (item.getMultiSelect()){
                            if ( !JSONUtil.isTypeJSONArray(temp.getValue()) ||!enums.contains(JSONUtil.parseArray(temp.getValue()))) {
                                return true;
                            }
                        }else {
                            if ( !enums.contains(temp.getValue())) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
        if (b2){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性值的枚举格式不合法或者非范围内");
        }
        // 校验日期
        boolean b3 = properties.stream().filter(item -> item.getType().equals(PropertyType.TYPE_DATE.getType()))
                .anyMatch(item -> {
                    PropertyIdAndValue temp = idAndValueMap.get(item.getID());
                    if (temp != null && temp.getValue()!=null) {
                        try {
                            DateUtil.parse(temp.getValue(), DefaultConstant.SYSTEM_DATE_FORMAT);
                        }catch (Exception e){
                            return true;
                        }
                        return false;
                    }
                    return false;
                });
        if (b3) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性值的日期格式不正确");
        }
        // 校验数值
        boolean b4 = properties.stream().filter(item -> item.getType().equals(PropertyType.TYPE_NUMBER.getType()))
                .anyMatch(item -> {
                    PropertyIdAndValue temp = idAndValueMap.get(item.getID());
                    if (temp != null && temp.getValue()!=null) {
                        try {
                            Double.valueOf(temp.getValue());
                        }catch (Exception e){
                            return true;
                        }
                        return false;
                    }
                    return false;
                });
        if (b4) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性值的数值格式不正确");
        }


        return null;
    }
}
