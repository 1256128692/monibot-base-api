package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.MonitorTypeFieldV2;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-22 15:23
 */
@Data
public class QueryCompareAnalysisDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "属性标识不能为空")
    private String fieldToken;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @NotNull(message = "自动传感器ID不能为空")
    @Positive(message = "自动传感器ID不能小于1")
    private Integer autoSensorID;
    @NotNull(message = "手动传感器ID不能为空")
    @Positive(message = "手动传感器ID不能小于1")
    private Integer manualSensorID;
    @JsonIgnore
    private List<Integer> sensorIDList = List.of(autoSensorID, manualSensorID);

    @Override
    public ResultWrapper<?> validate() {
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getProjectID, projectID).in(TbSensor::getID, sensorIDList));
        if (sensorList.size() != 2) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在!");
        }
        //TODO 校验自动传感器ID是否是自动传感器的ID、手动传感器ID是否是手动传感器的ID
        List<Integer> monitorTypeList = sensorList.stream().map(TbSensor::getMonitorType).distinct().toList();
        if (monitorTypeList.size() > 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "自动传感器与手动传感器的监测类型不同!");
        }
        List<MonitorTypeFieldV2> monitorTypeFieldV2List = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .queryByMonitorTypesV2(monitorTypeList, false);
        if (monitorTypeFieldV2List.stream().filter(u -> Objects.nonNull(u.getFieldClass()) && u.getFieldClass().equals(FieldClass.BASIC.getCode()))
                .noneMatch(u -> u.getFieldToken().equals(fieldToken))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测类型没有这种标识的子属性!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
