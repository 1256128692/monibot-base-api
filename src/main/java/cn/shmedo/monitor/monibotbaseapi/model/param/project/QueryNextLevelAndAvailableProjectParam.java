package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:43
 **/
@Data
public class QueryNextLevelAndAvailableProjectParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @JsonIgnore
    TbProjectInfo tbProjectInfo;
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        tbProjectInfo = tbProjectInfoMapper.selectById(projectID);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不存在");
        }
        if (!tbProjectInfo.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不属于该companyID");
        }
        if (tbProjectInfo.getLevel().equals(ProjectLevel.Son.getLevel()) || tbProjectInfo.getLevel().equals(ProjectLevel.Unallocated.getLevel())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该工程不是一级或二级工程");
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
