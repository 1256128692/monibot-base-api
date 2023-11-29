package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Chengfs on 2023/11/29
 */
@Data
public class QuerySensorSimpleListRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    private Set<@NotNull @Positive Integer> idList;

    private Collection<@NotNull @Positive Integer> projectIDList;

    private Set<@NotNull @Positive Integer> monitorTypeList;

    @Override
    public ResultWrapper<?> validate() {
        if (!SubjectType.APPLICATION.equals(CurrentSubjectHolder.getCurrentSubject().getSubjectType())) {
            this.projectIDList = Optional.ofNullable(this.projectIDList).filter(Collection::isEmpty).orElse(null);
            this.projectIDList = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return projectIDList.isEmpty() ?
                List.of() : projectIDList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}