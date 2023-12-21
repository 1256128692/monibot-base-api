package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.StatisticalMethods;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

@Data
public class GqQueryMonitorPointStatisticsDataPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    private Integer projectTypeID;
    private Integer kind;
    private String monitorPointName;
    @NotNull(message = "开始时间不能为空")
    private Date begin;
    @NotNull(message = "结束时间不能为空")
    private Date end;

    private Boolean dataSort;

    @NotNull(message = "密度不能为空")
    private Integer densityType;
    @NotNull(message = "统计方式不能为空")
    private Integer statisticsType;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;
    @NotNull
    @Positive
    private Integer currentPage;

    @JsonIgnore
    private Integer monitorType;

    @JsonIgnore
    private String tokenStr1;

    @JsonIgnore
    private String tokenStr2;

    @JsonIgnore
    private String tokenStr3;

    @Override
    public ResultWrapper<?> validate() {
        if (begin.after(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }

        if (projectTypeID != null) {
            if (!(Objects.equals(projectTypeID, ProjectType.SLUICE.getCode()) || Objects.equals(projectTypeID, ProjectType.CANAL_SYSTEM.getCode()) )) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仅支持工程类型为水闸或者渠系");
            }
            if (Objects.equals(projectTypeID, ProjectType.SLUICE.getCode())) {
                monitorType = MonitorType.SLUICE_REGIMEN.getKey();
            }

            if (Objects.equals(projectTypeID, ProjectType.CANAL_SYSTEM.getCode())) {
                monitorType = MonitorType.CHANNEL_WATER_LEVEL.getKey();
            }
        }

        if ( !(DisplayDensity.DAY.getValue() == densityType || DisplayDensity.MONTH.getValue() == densityType ||
                DisplayDensity.YEAR.getValue() == densityType) ) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "密度仅支持日,月,年");
        }

        if ( StatisticalMethods.AVERAGE.getValue() != statisticsType) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "统计方式仅支持平均");
        }

        tokenStr1 = "\"waterMeasureType\": 3";
        tokenStr2 = "\"waterMeasureType\": 4";
        if (projectTypeID != null) {
            if (Objects.equals(projectTypeID, ProjectType.SLUICE.getCode())) {
                tokenStr3 = "\"waterMeasureType\": 3";
            } else {
                tokenStr3 = "\"waterMeasureType\": 4";
            }
        }

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

}
