package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class QueryMonitorPointListParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private List<Integer> projectIDList;

    private Integer projectTypeID;

    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    private Integer monitorItemID;

    private String areaCode;

    @JsonIgnore
    private String accessToken;

    @Override
    public ResultWrapper<?> validate() {
        if (CollectionUtil.isEmpty(projectIDList)) {
            ProjectService projectService = ContextHolder.getBean(ProjectService.class);
            accessToken = CurrentSubjectHolder.getCurrentSubjectExtractData();
            projectIDList = projectService.getUserProjectIDs(companyID, accessToken);
            if (CollectionUtil.isEmpty(projectIDList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前用户下不存在有权限的工程");
            }
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
