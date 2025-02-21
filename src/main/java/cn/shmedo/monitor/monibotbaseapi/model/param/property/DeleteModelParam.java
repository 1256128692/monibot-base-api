package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 13:34
 **/
@Data
@ToString
public class DeleteModelParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotEmpty(message = "模板ID集合不能为空")
    private List<Integer> modelIDList;

    @JsonIgnore
    private List<TbPropertyModel> tbPropertyModelList;

    @Override
    public ResultWrapper<?> validate() {
        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        tbPropertyModelList = tbPropertyModelMapper.selectBatchIds(modelIDList);
        if(CollectionUtil.isEmpty(tbPropertyModelList)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到对应模板");
        }

        // 预定义模板不支持删除
        Set<Integer> createTypeSet = tbPropertyModelList.stream().map(TbPropertyModel::getCreateType).collect(Collectors.toSet());
        if(createTypeSet.contains(Integer.valueOf(CreateType.PREDEFINED.getType()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在预定义模板，预定义模板不支持删除");
        }

        // 设备表单和工作流支持分组管理，默认分组不可删除
        Map<Integer, List<TbPropertyModel>> modelGroup = tbPropertyModelList.stream().collect(Collectors.groupingBy(TbPropertyModel::getModelType));
        if(modelGroup.containsKey(DefaultConstant.PROPERTY_MODEL_DEFAULT_GROUP)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "表单下默认分组不可删除");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
