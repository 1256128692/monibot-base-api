package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyType;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.ModelItem;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 16:08
 **/
public class PropertyUtil {
    public static boolean validate(List<ModelItem> modelPropertyList){
        return modelPropertyList.stream().noneMatch(
                item -> {
                    // 非数值却又单位
                    if (!item.getType().equals(PropertyType.TYPE_NUMBER.getType()) && ObjectUtil.isNotEmpty(item.getUnit())){
                        return true;
                    }
                    if (item.getType().equals(PropertyType.TYPE_ENUM.getType())){
                       if (ObjectUtil.hasEmpty(item.getMultiSelect(),item.getEnumField())){
                           return true;
                       }
                       if (!JSONUtil.isTypeJSONArray(item.getEnumField())){
                           return true;
                       }
                    }else {
                        if (!ObjectUtil.isAllEmpty(item.getMultiSelect(),item.getEnumField())){
                            return true;
                        }
                    }
                    return false;
                }
        );
    }
}
