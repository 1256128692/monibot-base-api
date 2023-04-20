package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWorkOrderMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWorkOrder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-18 18:03
 */
@Data
public class QueryWorkOrderWarnDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "工单ID不能为空")
    @Min(value = 1, message = "工单ID不能小于1")
    private Integer workOrderID;

    @Override
    public ResultWrapper validate() {
        TbWorkOrderMapper tbWorkOrderMapper = ContextHolder.getBean(TbWorkOrderMapper.class);
        if (tbWorkOrderMapper.selectCount(new LambdaQueryWrapper<TbWorkOrder>().eq(TbWorkOrder::getID, workOrderID)) < 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工单不存在");
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
