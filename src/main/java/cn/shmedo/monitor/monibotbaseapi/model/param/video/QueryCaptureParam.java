package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class QueryCaptureParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司不能为空")
    private Integer companyID;

    private Integer videoDeviceSourceID;
    private Integer sensorID;
    private Date begin;
    private Date end;

    @Override
    public ResultWrapper validate() {
        if (begin != null && end != null) {
            if (begin.after(end)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
            }
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
