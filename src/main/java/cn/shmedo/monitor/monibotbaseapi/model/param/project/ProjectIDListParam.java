package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author cyf
 * @Date 2023/2/28 14:12
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.project
 * @ClassName: ProjectIDListParam
 * @Description:
 * @Version 1.0
 */
@Data
public class ProjectIDListParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<Integer> dataIDList;

    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return dataIDList.stream().map(d ->
                new Resource(d.toString(), ResourceType.BASE_PROJECT)).collect(Collectors.toList());
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }


}
