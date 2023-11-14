package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryPropertyValuesResponse;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:08
 **/
@Data
@ToString
public class QueryPropertyValuesParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "对象类型不能为空")
    private Integer subjectType;

    @NotNull(message = "对象ID不能为空")
    private Long subjectID;

    @NotEmpty(message = "模板ID列表不能为空")
    private List<Integer> modelIDList;

    @JsonIgnore
    private List<TbPropertyModel> tbPropertyModelList;

    @Override
    public ResultWrapper<?> validate() {
        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<TbPropertyModel>().lambda()
                .eq(TbPropertyModel::getCompanyID, companyID)
                .eq(TbPropertyModel::getModelType, subjectType)
                .in(TbPropertyModel::getID, modelIDList));
        if(modelIDList.size() != tbPropertyModelList.size())
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在匹配失败的模板ID");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    public void wrapperToPropertyValues(List<QueryPropertyValuesResponse> modelList, Map<Integer, List<TbProperty>> propertyGroup, List<TbProjectProperty> tbProjectPropertyList){
        if(CollectionUtil.isEmpty(tbProjectPropertyList)){
            return;
        }
        Map<Integer, String> propertyValueMap = tbProjectPropertyList.stream().collect(Collectors.toMap(TbProjectProperty::getPropertyID, TbProjectProperty::getValue));
        for (TbPropertyModel tbPropertyModel : tbPropertyModelList) {
            QueryPropertyValuesResponse queryPropertyValuesResponse = new QueryPropertyValuesResponse();
            queryPropertyValuesResponse.setModelID(tbPropertyModel.getID());
            queryPropertyValuesResponse.setModelName(tbPropertyModel.getName());
            List<PropertyIdAndValue> propertyValueList = Lists.newArrayList();
            List<TbProperty> propertyList = propertyGroup.get(tbPropertyModel.getID());
            if (CollectionUtil.isNotEmpty(propertyList)) {
                for (TbProperty tbProperty : propertyList) {
                    PropertyIdAndValue propertyIdAndValue = new PropertyIdAndValue();
                    if (propertyValueMap.containsKey(tbProperty.getID())) {
                        propertyIdAndValue.setID(tbProperty.getID());
                        propertyIdAndValue.setValue(propertyValueMap.get(tbProperty.getID()));
                        propertyValueList.add(propertyIdAndValue);
                    }
                }
                queryPropertyValuesResponse.setPropertyIdAndValueList(propertyValueList);
            }
            modelList.add(queryPropertyValuesResponse);
        }
    }

}
