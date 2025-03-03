package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * @author Chengfs on 2024/1/25
 */
@Data
public class QueryDeviceOnlineStatsParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    @Positive
    private Integer projectID;

    @JsonIgnore
    private Collection<Integer> projects;

    @Override
    public ResultWrapper<?> validate() {
        if (projectID != null) {
            projects = PermissionUtil.getHavePermissionProjectList(companyID, List.of(projectID));
        } else {
            projects = PermissionUtil.getHavePermissionProjectList(companyID);
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return projects.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}