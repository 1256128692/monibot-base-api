package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

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
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class QueryCheckPointListRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    @Size(max = 255)
    private String keyword;

    @Positive
    private Integer serviceID;

    @Positive
    private Integer projectID;

    private Boolean enable;

    @Positive
    private Integer groupID;

    private Boolean allowUngrouped;

    @JsonIgnore
    private Collection<Integer> projectList;

    @Override
    public ResultWrapper<?> validate() {
        this.allowUngrouped = Optional.ofNullable(this.allowUngrouped).orElse(Boolean.TRUE);
        this.projectList = PermissionUtil.getHavePermissionProjectList(this.companyID, projectID == null ? null : List.of(projectID));
        return null;
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