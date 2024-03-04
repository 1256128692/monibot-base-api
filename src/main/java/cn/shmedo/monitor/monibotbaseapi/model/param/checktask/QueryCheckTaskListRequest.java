package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryCheckTaskListRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    private String keyword;

    @Positive
    private Integer serviceID;

    @Positive
    private Integer projectID;

    private CheckTaskType checkType;

    private CheckTaskStatus status;

    @JsonIgnore
    private Collection<Integer> projectList;

    @JsonIgnore
    private List<Integer> checkerList;

    @Override
    public ResultWrapper<?> validate() {
        this.keyword = Optional.ofNullable(this.keyword).map(String::trim).orElse(null);
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