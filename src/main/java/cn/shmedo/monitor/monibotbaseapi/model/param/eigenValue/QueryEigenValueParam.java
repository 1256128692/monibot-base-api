package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QueryEigenValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    private List<Integer> monitorPointIDList;
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
