package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

public class QueryHasPermissionParameter {
    private String serviceName;
    private String permissionToken;
    private String resourceToken;
    private Integer resourceType;
    private Boolean allowInherit;


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

    public String getResourceToken() {
        return resourceToken;
    }

    public void setResourceToken(String resourceToken) {
        this.resourceToken = resourceToken;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Boolean getAllowInherit() {
        return allowInherit;
    }

    public void setAllowInherit(Boolean allowInherit) {
        this.allowInherit = allowInherit;
    }
}
