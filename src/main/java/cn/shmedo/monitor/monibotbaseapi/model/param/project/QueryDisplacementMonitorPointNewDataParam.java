package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryDisplacementMonitorPointNewDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Integer projectTypeID;

    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    private Integer monitorItemID;

    private String monitorItemName;

    @NotNull(message = "监测类别不能为空")
    private Integer monitorClassType;

    private String areaCode;

    @Override
    public ResultWrapper<?> validate() {
        if (!monitorType.equals(MonitorType.INTERNAL_TRIAXIAL_DISPLACEMENT.getKey())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仅支持查询内部三轴位移类型");
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
