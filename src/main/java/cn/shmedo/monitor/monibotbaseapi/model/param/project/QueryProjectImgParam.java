package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 15:02
 **/
@Data
public class QueryProjectImgParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;

    @NotBlank
    private String imgType;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);

        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(projectID);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不存在");
        }
        if (!ProjectImageType.isValidate(imgType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目图片类型非法");
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
