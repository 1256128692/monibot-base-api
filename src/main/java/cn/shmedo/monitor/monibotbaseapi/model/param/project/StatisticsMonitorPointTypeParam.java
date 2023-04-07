package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorQueryType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatisticsMonitorPointTypeParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    @Override
    public ResultWrapper<?> validate() {
        if (!MonitorQueryType.contains(queryType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "查询类型仅仅包含(0:环境监测, 1:安全监测, 2:工情监测 3:防洪调度指挥监测 4:视频监测)");
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
