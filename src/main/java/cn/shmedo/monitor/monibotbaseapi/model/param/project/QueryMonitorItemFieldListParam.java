package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class QueryMonitorItemFieldListParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {


    @NotEmpty(message = "工程ID列表不能为空")
    private List<Integer> projectIDList;

    @NotEmpty(message = "监测点ID列表不能为空")
    private List<Integer> monitorPointIDList;


    @Override
    public ResultWrapper<?> validate() {
        // TODO:加校验
        return null;
    }


    @Override
    public List<Resource> parameter() {

        Set<Resource> collect = projectIDList.stream().map(item -> {
            if (item != null) {
                return new Resource(item.toString(), ResourceType.BASE_PROJECT);
            }
            return null;
        }).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(item -> ResourceType.BASE_PROJECT + item.toString()))));

        return new ArrayList<>(collect);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}
