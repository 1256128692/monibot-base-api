package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
public class QueryMonitorPointWithProjectTypeParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    private Integer companyID;
    private Set<@NotNull Integer> projectTypes;

    @JsonIgnore
    private Collection<Integer> projectList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectList = PermissionUtil.getHavePermissionProjectList(companyID);
        Optional.ofNullable(projectTypes).filter(e -> !e.isEmpty()).ifPresent(e -> {
            e.forEach(item -> Assert.isTrue(ProjectTypeCache.projectTypeMap.containsKey(item.byteValue()),
                    "项目类型: {} 不存在", item));
        });
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
