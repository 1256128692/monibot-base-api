package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;


import java.util.List;

public class BatchResourceParameter {
    private String serviceName;
    private String permissionToken;
    private List<ResourceItemV2> resourceList;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPermissionToken() {
        return permissionToken;
    }

    public void setPermissionToken(String permissionToken) {
        this.permissionToken = permissionToken;
    }

    public List<ResourceItemV2> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceItemV2> resourceList) {
        this.resourceList = resourceList;
    }
}
