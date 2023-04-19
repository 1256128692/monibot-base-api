package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWorkOrderMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWorkOrder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-2023-04-18 18:03
 */
@Data
public class DeleteWorkOrderParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Min(value = 1, message = "公司ID不能小于1")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotEmpty(message = "需要操作的工单ID List不能为空")
    private List<Integer> workOrderIDList;

    @Override
    public ResultWrapper validate() {
        if (workOrderIDList.size() == 1) {
            TbWorkOrderMapper tbWorkOrderMapper = ContextHolder.getBean(TbWorkOrderMapper.class);
            if (tbWorkOrderMapper.selectCount(new LambdaQueryWrapper<TbWorkOrder>().eq(TbWorkOrder::getID, workOrderIDList.get(0))) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "操作的工单不存在");
            }
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
