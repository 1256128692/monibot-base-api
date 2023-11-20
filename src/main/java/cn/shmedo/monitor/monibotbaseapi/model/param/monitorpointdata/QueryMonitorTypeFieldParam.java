package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryMonitorTypeFieldParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "projectID不能为空")
    private Integer projectID;
    @NotNull(message = "monitorItemID不能为空")
    private Integer monitorItemID;


    @Override
    public ResultWrapper validate() {
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
