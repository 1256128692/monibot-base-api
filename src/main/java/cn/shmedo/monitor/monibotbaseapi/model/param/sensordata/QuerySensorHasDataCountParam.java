package cn.shmedo.monitor.monibotbaseapi.model.param.sensordata;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class QuerySensorHasDataCountParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer projectID;
    @NotEmpty
    @Size(max = 100)
    private List<@NotNull Integer> sensorIDList;


    @NotNull
    private Timestamp begin;
    @NotNull
    private Timestamp end;

    @NotNull
    private Integer density;

    @JsonIgnore
    private List<TbSensor> sensorList;

    @JsonIgnore
    private Integer monitorType;


    @Override
    public ResultWrapper validate() {
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        sensorList = tbSensorMapper.selectBatchIds(sensorIDList);
        if (sensorList.size() < sensorIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在");
        }
        Set<Integer> collect = sensorList.stream().map(TbSensor::getMonitorType).collect(Collectors.toSet());
        if (collect.size() > 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器类型不一致");
        }
        monitorType = sensorList.get(0).getMonitorType();
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
