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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:04
 **/
@Data
public class SetProjectRelationParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @JsonIgnore
    TbProjectInfo tbProjectInfo;
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;
    @NotBlank
    @Valid
    private List<@NotNull Integer> nextLevelPIDList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        tbProjectInfo = tbProjectInfoMapper.selectByPrimaryKey(projectID);
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
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该工程不是一级或二级工程");
        }
        if (nextProjectList.stream().anyMatch(item -> item.getLevel().equals(ProjectLevel.One.getLevel()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "nextLevelPIDList中存在一级工程");
        }
        if (tbProjectInfo.getLevel().equals(ProjectLevel.One.getLevel())
                && nextProjectList.stream().anyMatch(item -> !item.getLevel().equals(ProjectLevel.Two.getLevel()) || !item.getLevel().equals(ProjectLevel.Unallocated.getLevel()))
        ) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一级工程只能关联二级或未分配工程");
        }
        if (tbProjectInfo.getLevel().equals(ProjectLevel.Two.getLevel())
                && nextProjectList.stream().anyMatch(item -> !item.getLevel().equals(ProjectLevel.Son.getLevel())
        )) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "二级工程只能关联子工程");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
