package cn.shmedo.monitor.monibotbaseapi.model.param.sensordata;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AvgDensityType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.RainDensityType;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class QuerySensorHasDataCountParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty(message = "工程ID列表不能为空")
    @Size(max = 100)
    private List<Integer> projectIDList;
    @NotEmpty(message = "传感器ID列表不能为空")
    @Size(max = 100)
    private List<Integer> sensorIDList;


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

        // 校验监测点的监测项目名称,如果监测项目名称有1个以上,则这是跨监测项目,返回相应错误
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        List<TbMonitorItem> monitorItemList = tbMonitorItemMapper.selectListBySensorIDsAndProjectIDs(sensorIDList, projectIDList);
        if (CollectionUtil.isNullOrEmpty(monitorItemList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前传感器列表没有对应的监测项目");
        } else {
            long count = monitorItemList.stream().map(TbMonitorItem::getName).distinct().count();
            if (count > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前传感器列表所属不同监测项目");
            }
        }

        boolean validDensity = RainDensityType.isValidDensity(density);
        if (!validDensity) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测密度参数错误");
        }

        if (begin.after(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        Set<Resource> collect = projectIDList.stream().map(item -> {
            if (item != null) {
                return new Resource(item.toString(), ResourceType.BASE_PROJECT);
            }
            return null;
        }).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(item -> ResourceType.BASE_PROJECT + item.toString()))));

        return new ArrayList<>(collect);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}
