package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import com.alibaba.nacos.shaded.org.checkerframework.checker.index.qual.NonNegative;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 17:11
 **/
@Data
public class QueryMonitorPointPageListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    private String queryCode;
    private Integer monitorType;
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
