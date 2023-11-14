package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:08
 **/
@Data
@ToString
public class AddPropertyValuesParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "对象类型不能为空")
    private Integer subjectType;

    @NotNull(message = "对象ID不能为空")
    private Long subjectID;

    @NotEmpty(message = "模板属性值不能为空")
    private List<PropertyIdAndValue> propertyValueList;

    @Override
    public ResultWrapper<?> validate() {
        List<Integer> propertyIdList = propertyValueList.stream().map(PropertyIdAndValue::getID).toList();
        TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
        List<TbProperty> tbPropertyList = tbPropertyMapper.selectBatchIds(propertyIdList);
        if(propertyIdList.size() != tbPropertyList.size())
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在错误的属性ID");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    /**
     * 包装模板属性值
     * @return List<TbProjectProperty>
     */
    public List<TbProjectProperty> wrapperToPropertyValues() {
        List<TbProjectProperty> tbProjectPropertyList = Lists.newArrayList();
        for(PropertyIdAndValue propertyIdAndValue : propertyValueList){
            TbProjectProperty tbProjectProperty = new TbProjectProperty();
            tbProjectProperty.setSubjectType(subjectType);
            tbProjectProperty.setProjectID(subjectID);
            tbProjectProperty.setPropertyID(propertyIdAndValue.getID());
            tbProjectProperty.setValue(propertyIdAndValue.getValue());
            tbProjectPropertyList.add(tbProjectProperty);
        }
        return tbProjectPropertyList;
    }
}
