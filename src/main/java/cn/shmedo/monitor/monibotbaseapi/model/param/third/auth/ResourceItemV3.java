package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

/**
 * Created on 2021/6/4 16:10
 *
 * @Author Liuwei
 */
public class ResourceItemV3 {
    private Integer resourceType;
    private String resourceToken;
    private String resourceDesc;

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

    public String getResourceDesc() {
        return resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc;
    }


    public ResourceItemV3() {
    }

    public ResourceItemV3(Integer resourceType, String resourceToken, String resourceDesc) {
        this.resourceType = resourceType;
        this.resourceToken = resourceToken;
        this.resourceDesc = resourceDesc;
    }

    @Override
    public String toString() {
        return "ResourceItemV3{" +
                "resourceType=" + resourceType +
                ", resourceToken='" + resourceToken + '\'' +
                ", resourceDesc='" + resourceDesc + '\'' +
                '}';
    }
}
