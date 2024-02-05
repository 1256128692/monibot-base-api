package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.List;


@Data
public class ReservoirNewSensorDataParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {


    @NotNull
    private Integer companyID;
    @NotEmpty
    private List<Integer> projectIDList;

    @JsonIgnore
    private Collection<Integer> projects;

    @Override
    public ResultWrapper<?> validate() {

        projects = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);

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
