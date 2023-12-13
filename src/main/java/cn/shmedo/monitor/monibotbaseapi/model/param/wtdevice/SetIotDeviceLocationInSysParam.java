package cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-12-13 15:46
 **/
@Data
public class SetIotDeviceLocationInSysParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotBlank
    private String deviceToken;
    private String locationJson;
    @NotBlank
    private String address;

    @Override
    public ResultWrapper validate() {
        if (ObjectUtil.isNotEmpty(locationJson) && !JSONUtil.isTypeJSON(locationJson)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "位置扩展格式错误");
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
