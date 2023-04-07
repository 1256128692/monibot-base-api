package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class DeleteResourcesParameter {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<ResourceItemV2> resourceList;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public List<ResourceItemV2> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceItemV2> resourceList) {
        this.resourceList = resourceList;
    }

    public DeleteResourcesParameter() {
    }

    public DeleteResourcesParameter(Integer companyID, List<ResourceItemV2> resourceList) {
        this.companyID = companyID;
        this.resourceList = resourceList;
    }

    @Override
    public String toString() {
        return "DeleteResourcesParameter{" +
                "companyID=" + companyID +
                ", resourceList=" + resourceList +
                '}';
    }
}
