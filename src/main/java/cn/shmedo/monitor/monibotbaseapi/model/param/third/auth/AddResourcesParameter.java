package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


public class AddResourcesParameter {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(max = 100, min = 1)
    private List<ResourceItemV3> resourceList;
    @JsonIgnore
    private Integer resourceGroupID;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public List<ResourceItemV3> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceItemV3> resourceList) {
        this.resourceList = resourceList;
    }


    public AddResourcesParameter() {
    }

    public AddResourcesParameter(Integer companyID, List<ResourceItemV3> resourceList) {
        this.companyID = companyID;
        this.resourceList = resourceList;
    }

    @Override
    public String toString() {
        return "AddResourceParameter{" +
                "companyID=" + companyID +
                ", resourceList=" + resourceList +
                '}';
    }
}
