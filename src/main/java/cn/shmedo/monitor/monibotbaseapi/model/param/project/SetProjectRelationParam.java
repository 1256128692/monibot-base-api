package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:04
 **/
@Data
public class SetProjectRelationParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;
    @NotEmpty
    @Valid
    private List<@NotNull Integer> nextLevelPIDList;

    @JsonIgnore
    @ToString.Exclude
    TbProjectInfo tbProjectInfo;
    @JsonIgnore
    @ToString.Exclude
    private Byte raltionType;

    @Override
    public ResultWrapper validate() {
        if (nextLevelPIDList.contains(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "nextLevelPIDList中不能包含projectID");
        }
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        tbProjectInfo = tbProjectInfoMapper.selectById(projectID);
        List<TbProjectInfo> nextProjectList = tbProjectInfoMapper.selectBatchIds(nextLevelPIDList);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不存在");
        }
        if (!tbProjectInfo.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不属于该companyID");
        }
        if (nextProjectList.size() != nextLevelPIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "nextLevelPIDList中存在不存在的projectID");
        }
        if (nextProjectList.stream().anyMatch(item -> !item.getCompanyID().equals(companyID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "nextLevelPIDList中存在不属于该companyID的projectID");
        }
        if (tbProjectInfo.getLevel().equals(ProjectLevel.Son.getLevel())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该工程不能是子工程");
        }
        if (nextProjectList.stream().anyMatch(item -> item.getLevel().equals(ProjectLevel.One.getLevel()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "nextLevelPIDList中存在一级工程");
        }
        if (nextProjectList.stream().allMatch(item -> item.getLevel().equals(ProjectLevel.Son.getLevel()))) {
            if (tbProjectInfo.getLevel().equals(ProjectLevel.One.getLevel())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一级工程不能关联子工程");
            }
            raltionType = ProjectLevel.Two.getLevel();
        } else if (nextProjectList.stream().allMatch(item -> item.getLevel().equals(ProjectLevel.Unallocated.getLevel()) || item.getLevel().equals(ProjectLevel.Two.getLevel()))) {
            if (tbProjectInfo.getLevel().equals(ProjectLevel.Two.getLevel())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "二级工程不能关联二级工程");
            }
            raltionType = ProjectLevel.One.getLevel();
        } else {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "关联关系不合法");
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        List<Resource> list = new java.util.ArrayList<>(nextLevelPIDList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList());
        list.add(new Resource(projectID.toString(), ResourceType.BASE_PROJECT));
        return list;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}
