package cn.shmedo.monitor.monibotbaseapi.model.param.tag;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:43
 **/
public class QueryTagListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    private String fuzzyKey;
    private String fuzzyValue;
    @Override
    public ResultWrapper validate() {
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


    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getFuzzyKey() {
        return fuzzyKey;
    }

    public void setFuzzyKey(String fuzzyKey) {
        this.fuzzyKey = fuzzyKey;
    }

    public String getFuzzyValue() {
        return fuzzyValue;
    }

    public void setFuzzyValue(String fuzzyValue) {
        this.fuzzyValue = fuzzyValue;
    }
}
