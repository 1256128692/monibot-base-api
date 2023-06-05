package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorClassType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryWtMonitorItemListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer projectID;

    private Integer monitorClass;
    private Boolean enable;

    @Override
    public ResultWrapper<?> validate() {
        if (monitorClass != null) {
            if (!MonitorClassType.isValidValue(monitorClass)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前查询监测类别条件错误");
            }
        }

        return null;
    }


    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
