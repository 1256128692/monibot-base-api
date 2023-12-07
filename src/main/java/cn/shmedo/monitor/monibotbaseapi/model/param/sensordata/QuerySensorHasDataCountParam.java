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
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.enums.RainDensityType;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.SensorBaseInfoV2;
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
    private Integer rawDensity;

    @JsonIgnore
    private List<TbSensor> sensorList;

    @JsonIgnore
    private Map<Integer, List<TbSensor>> groupedByMonitorType;

    @Override
    public ResultWrapper validate() {
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        sensorList = tbSensorMapper.selectBatchIds(sensorIDList);
        if (sensorList.size() < sensorIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在");
        }

        // 按监测类型进行分组
        groupedByMonitorType = sensorList.stream()
                .collect(Collectors.groupingBy(TbSensor::getMonitorType));

        boolean validDensity = RainDensityType.isValidDensity(density);
        if (!validDensity) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测密度参数错误");
        }

        rawDensity = density;

        if (density == 0 || density == 1 || density == 4 || density == 5 || density == 6 || density == 7) {
            density = DisplayDensity.DAY.getValue();
        } else if (density == 2) {
            density = DisplayDensity.MONTH.getValue();
        } else {
            density = DisplayDensity.YEAR.getValue();
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
