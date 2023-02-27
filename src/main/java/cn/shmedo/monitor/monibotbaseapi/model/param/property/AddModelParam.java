package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:08
 **/
public class AddModelParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private String modelName;
    @NotNull
    private Byte projectType;
    private String desc;
    private List<ModelItem> modelPropertyList;

    @Override
    public ResultWrapper validate() {
        if (ProjectTypeCache.projectTypeMap.get(Integer.valueOf(projectType)) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        if (!PropertyUtil.validate(modelPropertyList)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性不合法");
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


    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Byte getProjectType() {
        return projectType;
    }

    public void setProjectType(Byte projectType) {
        this.projectType = projectType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ModelItem> getModelPropertyList() {
        return modelPropertyList;
    }

    public void setModelPropertyList(List<ModelItem> modelPropertyList) {
        this.modelPropertyList = modelPropertyList;
    }
}
