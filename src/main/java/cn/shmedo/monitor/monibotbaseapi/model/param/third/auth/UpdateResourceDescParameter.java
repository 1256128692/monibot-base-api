package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateResourceDescParameter {
    @NotEmpty
    @Size(max = 100, min = 1)
    private List<ResourceItemV3> resourceList;

    public List<ResourceItemV3> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceItemV3> resourceList) {
        this.resourceList = resourceList;
    }


    public UpdateResourceDescParameter() {
    }

    public UpdateResourceDescParameter(List<ResourceItemV3> resourceList) {
        this.resourceList = resourceList;
    }

    @Override
    public String toString() {
        return "UpdateResourceDescParameter{" +
                "resourceList=" + resourceList +
                '}';
    }
}
