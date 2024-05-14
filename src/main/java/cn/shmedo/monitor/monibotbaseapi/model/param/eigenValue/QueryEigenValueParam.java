package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class QueryEigenValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @Positive
    @NotNull(message = "工程ID不能为空")
    private Integer projectID;

    @Valid
    private List<@NotBlank String> fieldTokenList;

    @Valid
    private List<@Positive @NotNull Integer> monitorPointIDList;

    @Positive
    private Integer monitorItemID;

    private ScopeType scope;

    @Override
    public ResultWrapper<?> validate() {
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
