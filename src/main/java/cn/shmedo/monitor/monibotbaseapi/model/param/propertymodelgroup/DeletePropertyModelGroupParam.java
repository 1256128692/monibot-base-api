package cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author wuxl
 * @Date 2023/9/15 17:14
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup
 * @ClassName: AddPropertyModelGroup
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class DeletePropertyModelGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotEmpty(message = "主键ID集合不能为空")
    @JsonProperty("IDList")
    private List<Integer> IDList;

    @Override
    public ResultWrapper<?> validate() {
        // 针对工程项目类型，如果其分组下存在模板，提示不允许删除
        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<TbPropertyModel>().lambda()
                .eq(TbPropertyModel::getModelType, PropertyModelType.BASE_PROJECT.getCode())
                .in(TbPropertyModel::getGroupID, IDList));
        if(CollectionUtil.isNotEmpty(tbPropertyModelList))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型为工程项目，其下存在模板，不允许删除");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
