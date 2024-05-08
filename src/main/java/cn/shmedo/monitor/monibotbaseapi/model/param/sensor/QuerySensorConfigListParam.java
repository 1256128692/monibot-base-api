package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;

/**
 * @author Chengfs on 2023/11/29
 */
@Data
public class QuerySensorConfigListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    @NotNull
    private Integer projectID;

    @NotNull
    private Integer sendType;

    @Override
    public ResultWrapper<?> validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(projectID);
        if (Objects.isNull(tbProjectInfo))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程项目不存在");
        if (Objects.isNull(SendType.valueOf(sendType)))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "推送类型不合法");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}