package cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 14:00
 **/
@Data
public class TbOtherDeviceWithProperty extends TbOtherDevice {
    private String projectLocation;
    private String projectName;
    private String templateName;
    private List<PropertyWithValue> propertyList;

    public static TbOtherDeviceWithProperty valueOf(TbOtherDevice tbOtherDevice, List<TbProperty> tbProperties, List<TbProjectProperty> tbProjectProperties, String location, String projectName, String templateName) {

        TbOtherDeviceWithProperty obj = BeanUtil.copyProperties(tbOtherDevice, TbOtherDeviceWithProperty.class);
        Map<Integer, TbProjectProperty> valueMap = tbProjectProperties.stream().collect(Collectors.toMap(TbProjectProperty::getPropertyID, Function.identity()));
        obj.setPropertyList(tbProperties.stream().map(tbProperty -> PropertyWithValue.builder()
                .ID(tbProperty.getID())
                .name(tbProperty.getName())
                .unit(tbProperty.getUnit())
                .className(tbProperty.getClassName())
                .type(tbProperty.getType())
                .required(tbProperty.getRequired())
                .enumField(tbProperty.getEnumField())
                .multiSelect(tbProperty.getMultiSelect())
                .value(valueMap.containsKey(tbProperty.getID()) ? valueMap.get(tbProperty.getID()).getValue() : null)
                .build()).collect(Collectors.toList()));
        obj.setProjectLocation(location);
        obj.setProjectName(projectName);
        obj.setTemplateName(templateName);
        return obj;
    }
}
