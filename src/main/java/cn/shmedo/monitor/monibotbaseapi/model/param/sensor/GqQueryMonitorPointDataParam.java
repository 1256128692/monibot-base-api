package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GqQueryMonitorPointDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotNull(message = "项目类型不能为空")
    private Integer projectTypeID;
    private Integer kind;
    @NotNull(message = "量水类型不能为空")
    private Integer token;
    private String monitorPointName;
    @NotNull(message = "开始时间不能为空")
    private Date begin;
    @NotNull(message = "结束时间不能为空")
    private Date end;

    @JsonIgnore
    private Integer monitorType;

    @JsonIgnore
    private String tokenStr;

    @Override
    public ResultWrapper<?> validate() {
        if (begin.after(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }
        long between = DateUtil.between(begin, end, DateUnit.HOUR);
        if (between >= 24) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "查询时间范围不能超过24小时");
        }

        begin = DateUtil.beginOfDay(begin).offset(DateField.HOUR, 8);
        end = DateUtil.beginOfDay(end).offset(DateField.HOUR, 20);

        if (!(Objects.equals(projectTypeID, ProjectType.SLUICE.getCode()) || Objects.equals(projectTypeID, ProjectType.CANAL_SYSTEM.getCode()) )) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仅支持工程类型为水闸或者渠系");
        }
        if (Objects.equals(projectTypeID, ProjectType.SLUICE.getCode())) {
            monitorType = MonitorType.SLUICE_REGIMEN.getKey();
        }

        if (Objects.equals(projectTypeID, ProjectType.CANAL_SYSTEM.getCode())) {
            monitorType = MonitorType.CHANNEL_WATER_LEVEL.getKey();
        }


        if (! (Objects.equals(token, WaterMeasureType.WATERWORK.getCode()) || Objects.equals(token, WaterMeasureType.STANDARD_SECTION.getCode()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仅支持量水类型为水工建筑量水或者标准断面量水");
        }
        if (Objects.equals(token, WaterMeasureType.WATERWORK.getCode())) {
            tokenStr = "\"waterMeasureType\": 3";
        } else {
            tokenStr = "\"waterMeasureType\": 4";
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

}
