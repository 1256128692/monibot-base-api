package cn.shmedo.monitor.monibotbaseapi.model.param.project;


import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuerySingleProjectMonitorPointInfoListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer projectID;

    private List<Integer> monitorStatusList;

    private List<Integer> monitorItemIDList;

    private String monitorPointName;

    private Boolean monitorPointCollection;
    @JsonIgnore
    private Integer userID;

    @Override
    public ResultWrapper validate() {

        this.userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
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
