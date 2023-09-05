package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
public class QueryVideoDevicePageParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull(message = "公司不能为空")
    private Integer companyID;


    private String deviceSerial;
    private Integer ownedCompanyID;
    private Integer projectID;
    private Boolean deviceStatus;
    private Boolean allocationStatus;
    private Boolean captureStatus;
    private Date begin;
    private Date end;
    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;


    @Override
    public ResultWrapper validate() {
        if (begin != null && end != null) {
            if (begin.after(end)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }


}
