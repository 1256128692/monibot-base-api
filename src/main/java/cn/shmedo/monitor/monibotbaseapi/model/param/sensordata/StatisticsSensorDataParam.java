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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: iot-manager
 * @author: gaoxu
 * @create: 2021-08-27 09:45
 **/
public class StatisticsSensorDataParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @JsonIgnore
    List<TbSensor> sensorList;
    @NotEmpty
    @Size(max = 100)
    private List<@NotNull Integer> sensorIDList;
    @NotNull
    private Date begin;
    @NotNull
    private Date end;
    @NotNull
    private Boolean raw;
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
    public List<Resource> parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

    public List<Integer> getSensorIDList() {
        return sensorIDList;
    }

    public void setSensorIDList(List<Integer> sensorIDList) {
        this.sensorIDList = sensorIDList;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Boolean getRaw() {
        return raw;
    }

    public void setRaw(Boolean raw) {
        this.raw = raw;
    }

    public List<TbSensor> getSensorList() {
        return sensorList;
    }

    public Integer getMonitorType() {
        return monitorType;
    }
}
