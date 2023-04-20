package cn.shmedo.monitor.monibotbaseapi.model.param.warn;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-14 16:47
 */
@Data
public class QueryWtWarnDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "报警记录ID不能为空")
    @Min(value = 1, message = "报警记录ID不能小于1")
    private Integer warnID;

    @Override
    public ResultWrapper validate() {
        TbWarnLogMapper tbWarnLogMapper = ContextHolder.getBean(TbWarnLogMapper.class);
        if (ObjectUtil.isEmpty(tbWarnLogMapper.selectById(warnID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警记录不存在");
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
