package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import jakarta.validation.constraints.NotNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-02 10:23
 **/
public class QueryModelListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    private Integer modelID;
    @NotNull
    private Byte projectType;
    private Byte createType;

    @Override
    public ResultWrapper validate() {
        if (!ProjectTypeCache.projectTypeMap.containsKey(projectType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        if (createType!=null &&!CreateType.validate(createType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "创建类型不合法");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }



    public Integer getModelID() {
        return modelID;
    }

    public void setModelID(Integer modelID) {
        this.modelID = modelID;
    }

    public Byte getProjectType() {
        return projectType;
    }

    public void setProjectType(Byte projectType) {
        this.projectType = projectType;
    }

    public Byte getCreateType() {
        return createType;
    }

    public void setCreateType(Byte createType) {
        this.createType = createType;
    }
}
