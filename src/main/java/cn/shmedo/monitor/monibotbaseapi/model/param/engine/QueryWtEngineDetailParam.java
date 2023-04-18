package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnRuleMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryWtEngineDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "引擎ID不能为空")
    @Min(value = 1, message = "引擎ID不能小于1")
    private Integer engineID;

    @Override
    public ResultWrapper validate() {
        TbWarnRuleMapper tbWarnRuleMapper = ContextHolder.getBean(TbWarnRuleMapper.class);
        if (tbWarnRuleMapper.selectCount(new LambdaQueryWrapper<TbWarnRule>().eq(TbWarnRule::getID, engineID)
                .select(TbWarnRule::getID)) < 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "引擎不存在");
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
