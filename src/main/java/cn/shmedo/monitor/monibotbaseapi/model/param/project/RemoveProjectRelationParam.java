package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-11 11:50
 **/
@Data
public class RemoveProjectRelationParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;
    @NotEmpty
    @Valid
    private List<@NotNull Integer> nextLevelPIDList;


    @Override
    public ResultWrapper validate() {
        if (nextLevelPIDList.contains(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "nextLevelPIDList中不能包含projectID");
        }
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(projectID);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不存在");
        }
        if (!tbProjectInfo.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不属于该companyID");
        }
        if (tbProjectInfo.getLevel().equals(ProjectLevel.Son.getLevel())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID为子项目");
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
