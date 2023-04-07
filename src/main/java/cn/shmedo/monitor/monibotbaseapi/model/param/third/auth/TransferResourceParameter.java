package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 资源转移参数
 * 将资源从一个公司转移到另一个公司
 */
public class TransferResourceParameter {
    /**
     * 资源所在公司
     */
    @NotNull
    private Integer fromCompanyID;
    /**
     * 资源转移到目标公司
     */
    @NotNull
    private Integer toCompanyID;
    /**
     * 资源列表
     */
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<ResourceItemV2> resourceList;

    @JsonIgnore
    private Integer targetResourceGroupID;

    public Integer getFromCompanyID() {
        return fromCompanyID;
    }

    public void setFromCompanyID(Integer fromCompanyID) {
        this.fromCompanyID = fromCompanyID;
    }

    public Integer getToCompanyID() {
        return toCompanyID;
    }

    public void setToCompanyID(Integer toCompanyID) {
        this.toCompanyID = toCompanyID;
    }

    public List<ResourceItemV2> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceItemV2> resourceList) {
        this.resourceList = resourceList;
    }

    public Integer getTargetResourceGroupID() {
        return targetResourceGroupID;
    }

    public void setTargetResourceGroupID(Integer targetResourceGroupID) {
        this.targetResourceGroupID = targetResourceGroupID;
    }


    public TransferResourceParameter() {
    }

    public TransferResourceParameter(Integer fromCompanyID, Integer toCompanyID, List<ResourceItemV2> resourceList) {
        this.fromCompanyID = fromCompanyID;
        this.toCompanyID = toCompanyID;
        this.resourceList = resourceList;
    }

    @Override
    public String toString() {
        return "TransferResourceParameter{" +
                "fromCompanyID=" + fromCompanyID +
                ", toCompanyID=" + toCompanyID +
                ", resourceList=" + resourceList +
                '}';
    }
}
