package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty
    @Valid
    private List< @NotNull ModelItem> modelPropertyList;

    @Override
    public ResultWrapper validate() {
        if (ProjectTypeCache.projectTypeMap.get(projectType) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        if (!PropertyUtil.validate(modelPropertyList)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性不合法");
        }
        if (modelPropertyList.stream().map(ModelItem::getName).distinct().count() >1){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性名称存在重复");
        }
        if (modelPropertyList.stream().anyMatch(item -> ObjectUtil.isNotEmpty(item.getExValue()) && !JSONUtil.isTypeJSON(item.getExValue()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性的额外属性应为json字符串");
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
