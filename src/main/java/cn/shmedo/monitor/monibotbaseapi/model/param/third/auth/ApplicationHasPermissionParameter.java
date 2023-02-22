package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

public class ApplicationHasPermissionParameter {
    private String serviceName;
    private String permissionToken;

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
}
