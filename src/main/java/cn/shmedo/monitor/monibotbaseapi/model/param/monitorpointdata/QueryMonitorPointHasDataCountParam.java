package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryMonitorPointHasDataCountParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "projectID不能为空")
    private Integer projectID;
    @NotEmpty
    private List<@NotNull Integer> monitorPointIDList;
    @NotNull(message = "begin不能为空")
    private Date begin;
    @NotNull(message = "end不能为空")
    private Date end;
    @NotNull(message = "density不能为空")
    private Integer density;

    @JsonIgnore
    private List<TbMonitorPoint> tbMonitorPoints = null;

    @Override
    public ResultWrapper validate() {

        TbMonitorPointMapper pointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        tbMonitorPoints = pointMapper.selectPointListByIDList(monitorPointIDList);
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
        }
        if (tbMonitorPoints.size() != monitorPointIDList.stream().distinct().count()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表有不存在的数据");
        }
        // 监测点的监测类型 与参数监测类型校验
        if (tbMonitorPoints.stream().map(TbMonitorPoint::getMonitorType).distinct().count() != 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表必须是同一个监测类型");
        }

        if (tbMonitorPoints.stream().map(TbMonitorPoint::getMonitorItemID).distinct().count() != 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表必须在同一个监测项目下");
        }

        if ( !(density == 3 || density == 5 || density == 6) ) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "查询密度只能为日,月,年");
        }

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
