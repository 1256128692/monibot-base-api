package cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeleteBatchDataEventParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @NotEmpty
    private List<@NotNull Integer> eventIDList;

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
