package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QueryEigenValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    private List<Integer> monitorPointIDList;
    private Integer monitorItemID;
    private Integer scope;

    @Override
    public ResultWrapper validate() {
        if (scope != null) {
            if (!ScopeType.isValidScopeType(scope)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:scope字段值的为作用范围非法类型");
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
