package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;


public class QueryPermissionInServiceParameter {
    private Integer companyID;
    private String serviceName;
    private String permissionToken;
    private Boolean allowInherit;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

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

    public Boolean getAllowInherit() {
        return allowInherit;
    }

    public void setAllowInherit(Boolean allowInherit) {
        this.allowInherit = allowInherit;
    }
}
