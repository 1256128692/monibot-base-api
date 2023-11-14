package cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 13:44
 **/
@Data
public class QueryMonitorGroupPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    private String queryCode;
    private Integer monitorItemID;
    @NotNull
    @Positive
    @Max(100)
    private Integer pageSize;
    @NotNull
    @Positive
    private Integer currentPage;
    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectById(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
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
