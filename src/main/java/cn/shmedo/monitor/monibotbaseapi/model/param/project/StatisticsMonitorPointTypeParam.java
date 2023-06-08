package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorQueryType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class StatisticsMonitorPointTypeParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    @JsonIgnore
    private List<Integer> projectIDList;

    @Override
    public ResultWrapper<?> validate() {
        Collection<Integer> permissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID, null);
        // 当前用户下没有任何工程则直接返回空
        if (permissionProjectList.isEmpty()) {
            return ResultWrapper.withCode(ResultCode.SUCCESS, null);
        }
        this.projectIDList = permissionProjectList.stream().toList();
        if (!MonitorQueryType.contains(queryType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "查询类型仅仅包含(0:环境监测, 1:安全监测, 2:工情监测 3:防洪调度指挥监测 4:视频监测)");
        }
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
