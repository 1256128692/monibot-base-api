package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryMonitorPointDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "projectID不能为空")
    private Integer projectID;
    @NotNull(message = "monitorType不能为空")
    private Integer monitorType;
    @NotNull(message = "monitorItemID不能为空")
    private Integer monitorItemID;
    @NotEmpty
    private List<@NotNull Integer> monitorPointIDList;
    @NotNull(message = "begin不能为空")
    private Date begin;
    @NotNull(message = "end不能为空")
    private Date end;
    @NotNull(message = "densityType不能为空")
    private Integer densityType;
    @NotNull(message = "statisticsType不能为空")
    private Integer statisticsType;

    /**
     * 特征值ID列表
     */
    private List<Integer> eigenValueIDList;
    /**
     * 大事记ID列表
     */
    private List<Integer> eventIDList;
    @Override
    public ResultWrapper validate() {
        // 时间校验
        if (begin.after(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }

        // 监测点的监测类型 与参数监测类型校验


        // 监测类型的密度校验

        // 监测类型的统计方式校验
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

}
