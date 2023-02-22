package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import cn.shmedo.iot.entity.api.Resource;

public class ResourceItemV2 {
    private Integer resourceType;
    private String resourceToken;

    public ResourceItemV2() {
    }

    public ResourceItemV2(Resource resource) {
        this.resourceType = resource.getResourceType().toInt();
        this.resourceToken = resource.getResourceToken();
    }

    public ResourceItemV2(Integer resourceType, String resourceToken) {
        this.resourceType = resourceType;
        this.resourceToken = resourceToken;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceToken() {
        return resourceToken;
    }

    public void setResourceToken(String resourceToken) {
        this.resourceToken = resourceToken;
    }

    @Override
    public String toString() {
        return "ResourceItemV2{" +
                "resourceType=" + resourceType +
                ", resourceToken='" + resourceToken + '\'' +
                '}';
    }
}
