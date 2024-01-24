package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class QueryDeviceStatisticByMonitorProjectListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<Integer> projectIDList;
    @NotNull
    private Timestamp beginTime;
    @NotNull
    private Timestamp endTime;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public List<Integer> getProjectIDList() {
        return projectIDList;
    }

    public void setProjectIDList(List<Integer> projectIDList) {
        this.projectIDList = projectIDList;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    /**
     * 资源权限认证类型
     *
     * @return 资源权限认证类型
     */
    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public String toString() {
        return "QueryDeviceStatisticByMonitorProjectListParam{" +
                "companyID=" + companyID +
                ", projectIDList=" + projectIDList +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                '}';
    }
}
