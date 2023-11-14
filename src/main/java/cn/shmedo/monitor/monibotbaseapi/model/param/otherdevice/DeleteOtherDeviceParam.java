package cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-27 18:05
 **/
@Data
public class DeleteOtherDeviceParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Valid
    private List<@NotNull Integer> deviceIDList;

    @Override
    public ResultWrapper validate() {
        TbOtherDeviceMapper tbOtherDeviceMapper = ContextHolder.getBean(TbOtherDeviceMapper.class);
        List<TbOtherDevice> tbOtherDevices = tbOtherDeviceMapper.selectBatchIds(deviceIDList);
        if (tbOtherDevices.size() != deviceIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有设备不存在");
        }
        if (tbOtherDevices.stream().anyMatch(item -> !item.getCompanyID().equals(companyID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有设备不属于该公司");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
