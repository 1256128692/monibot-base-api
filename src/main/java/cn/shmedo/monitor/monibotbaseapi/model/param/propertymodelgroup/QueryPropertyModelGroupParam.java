package cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

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
public class QueryPropertyModelGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "属性模板组类型不能为空")
    private Integer groupType;

    @NotNull(message = "主键ID不能为空")
    @JsonProperty("ID")
    private Integer ID;

    @JsonIgnore
    private TbPropertyModelGroup tbPropertyModelGroup;

    @Override
    public ResultWrapper<?> validate() {
        TbPropertyModelGroupMapper tbPropertyModelGroupMapper = ContextHolder.getBean(TbPropertyModelGroupMapper.class);
        LambdaQueryWrapper<TbPropertyModelGroup> queryWrapper = new QueryWrapper<TbPropertyModelGroup>().lambda()
                .eq(TbPropertyModelGroup::getID, ID)
                .eq(TbPropertyModelGroup::getGroupType, groupType);
        tbPropertyModelGroup = tbPropertyModelGroupMapper.selectOne(queryWrapper);
        if (Objects.isNull(tbPropertyModelGroup)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到对应属性模板组");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
