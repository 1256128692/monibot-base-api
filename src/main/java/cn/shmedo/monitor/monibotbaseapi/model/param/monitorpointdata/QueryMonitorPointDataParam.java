package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeConfigV1;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private Integer statisticsType;

    private Boolean filterEmptyData;
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

        TbMonitorPointMapper pointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        List<TbMonitorPoint> tbMonitorPoints = pointMapper.selectPointListByIDList(monitorPointIDList);
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
        }
        if (tbMonitorPoints.size() != monitorPointIDList.stream().distinct().count()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表有不存在的数据");
        }
        // 监测点的监测类型 与参数监测类型校验
        if (tbMonitorPoints.stream()
                .anyMatch(point -> !Objects.equals(point.getMonitorType(), monitorType))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表有与监测类型不相等的点位");
        }
        if (tbMonitorPoints.stream()
                .anyMatch(point -> !Objects.equals(point.getMonitorItemID(), monitorItemID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表有与监测项目不相等的点位");
        }

        // 监测类型的密度校验
        TbMonitorTypeMapper typeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        List<MonitorTypeBaseInfoV1> monitorTypeList = typeMapper.selectByMonitorTypeList(List.of(monitorType));
        if (CollectionUtil.isNullOrEmpty(monitorTypeList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "没有该监测类型");
        }

        if (densityType == DisplayDensity.ALL.getValue()) {
            if (statisticsType != null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "查询密度为全部时,参数statisticsType应为null");
            }
        } else {
            if (!StringUtils.isNotBlank(monitorTypeList.get(0).getExValues())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测类型没有配置预定义密度与统计方式");
            } else {
                MonitorTypeConfigV1 monitorTypeConfigV1 = JSONUtil.toBean(monitorTypeList.get(0).getExValues(), MonitorTypeConfigV1.class);
                if (densityType == DisplayDensity.TWO_HOUR.getValue()
                        || densityType == DisplayDensity.FOUR_HOUR.getValue()
                        || densityType == DisplayDensity.SIX_HOUR.getValue()
                        || densityType == DisplayDensity.TWELVE_HOUR.getValue()) {
                    // 实时数据不做校验

                } else {
                    // 监测类型的预定义查询方式校验(历史数据)
                    if (!monitorTypeConfigV1.getDisplayDensity().contains(densityType)) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数densityType不符合该监测类型的预定义配置");
                    }
                    if (!monitorTypeConfigV1.getStatisticalMethods().contains(statisticsType)) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数statisticsType不符合该监测类型的预定义配置");
                    }
                }

            }
        }


        if (filterEmptyData == null) {
            filterEmptyData = false;
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
