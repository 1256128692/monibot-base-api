package cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 17:54
 **/
@Data
public class DeleteMonitorGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotEmpty
    @Valid
    @Size(min = 1)
    private List< @NotNull Integer> groupIDList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectById(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorGroupMapper tbMonitorGroupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
        List<TbMonitorGroup> tbMonitorGroups = tbMonitorGroupMapper.selectBatchIds(groupIDList);
        if (tbMonitorGroups.size() != groupIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组有不存在");
        }
        if (tbMonitorGroups.stream().anyMatch(tbMonitorGroup -> !tbMonitorGroup.getProjectID().equals(projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组有不属于该项目");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
