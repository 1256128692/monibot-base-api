package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 14:44
 **/
@Data
public class ConfigMonitorPointSensorsParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer pointID;
    @NotEmpty
    @Valid
    @Size(max = 100)
    private List<Integer> sensorIDList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        TbMonitorPoint tbMonitorPoint = tbMonitorPointMapper.selectByPrimaryKey(pointID);
        if (!tbMonitorPoint.getProjectID().equals(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "点不属于该项目");
        }


        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        if (tbSensorMapper.selectCount(new QueryWrapper<TbSensor>().eq("MonitorPointID", pointID)) > 0

        ) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "点已经设置过传感器");

        }
        List<TbSensor> tbSensorList = tbSensorMapper.selectList(new QueryWrapper<TbSensor>().in("ID", sensorIDList));
        if (tbSensorList.size() != sensorIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在");

        }
        if (tbSensorList.stream().anyMatch(item -> !item.getProjectID().equals(projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不属于该项目");
        }
        if (tbSensorList.stream().anyMatch(item -> !item.getMonitorType().equals(tbMonitorPoint.getMonitorType()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器的监测类型与点不相符");
        }
        if (tbSensorList.stream().anyMatch(item -> item.getMonitorPointID() != null)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器已经配置过");
        }
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.queryByType(tbMonitorPoint.getMonitorType());
        if (!tbMonitorType.getMultiSensor() && sensorIDList.size() > 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测类型不支持多传感器");
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
