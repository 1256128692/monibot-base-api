package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

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
 * @author Chengfs on 2023/11/27
 */
@Data
public class ListSluiceGateRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    @Positive
    private Integer projectID;

    @Positive
    private Integer gateID;

    @JsonIgnore
    private Collection<Integer> projectList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectList = PermissionUtil.getHavePermissionProjectList(companyID, projectID == null ? null : List.of(projectID));
        return projectList.isEmpty() ? ResultWrapper.success(List.of()) : null;
    }

    @Override
    public List<Resource> parameter() {
        return this.projectList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}