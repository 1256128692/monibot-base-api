package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class QueryMonitorPointIncludeSensorParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    private Integer companyID;
    private Integer projectType;
    private Integer monitorType;

    @JsonIgnore
    private Collection<Integer> projectList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectList = PermissionUtil.getHavePermissionProjectList(companyID);
        if (projectType != null && !ProjectTypeCache.projectTypeMap.containsKey(projectType.byteValue()) ) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程类型不存在");
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return projectList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

}
