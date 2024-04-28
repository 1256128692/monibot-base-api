package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 14:26
 */
@Data
public class QueryWetLineConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotNull(message = "监测点组ID不能为空!")
    @Positive(message = "监测点组ID不能小于1")
    private Integer monitorGroupID;
    @NotEmpty(message = "监测点ID列表不能为空")
    private List<Integer> monitorPointIDList;
    @Positive(message = "库水位监测点ID不能小于1")
    private Integer wtPointID;
    @JsonIgnore
    private TbSensor wtSensor;
    @JsonIgnore
    private TbMonitorPoint wtMonitorPoint;
    @JsonIgnore
    private Integer monitorType = MonitorType.WET_LINE.getKey();

    @Override
    public ResultWrapper<?> validate() {
        final TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        final TbMonitorGroupPointMapper tbMonitorGroupPointMapper = ContextHolder.getBean(TbMonitorGroupPointMapper.class);
        if (tbMonitorPointMapper.selectCount(new LambdaQueryWrapper<TbMonitorPoint>()
                .in(TbMonitorPoint::getID, monitorPointIDList).eq(TbMonitorPoint::getMonitorType, monitorType)).intValue()
                != monitorPointIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不是浸润线监测点!");
        }
        if (!ContextHolder.getBean(TbMonitorGroupMapper.class).exists(new LambdaQueryWrapper<TbMonitorGroup>()
                .eq(TbMonitorGroup::getID, monitorGroupID).eq(TbMonitorGroup::getProjectID, projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点组不存在!");
        }
        if (tbMonitorGroupPointMapper.selectCount(new LambdaQueryWrapper<TbMonitorGroupPoint>()
                .eq(TbMonitorGroupPoint::getMonitorGroupID, monitorGroupID)
                .in(TbMonitorGroupPoint::getMonitorPointID, monitorPointIDList)) != monitorPointIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有部分监测点不属于该监测点组!");
        }
        if (Objects.nonNull(wtPointID)) {
            List<TbMonitorPoint> tbWtMonitorPointList = tbMonitorPointMapper.selectList(new LambdaQueryWrapper<TbMonitorPoint>()
                    .eq(TbMonitorPoint::getID, wtPointID).in(TbMonitorPoint::getMonitorType, MonitorType.WATER_LEVEL.getKey(), MonitorType.LEVEL.getKey()));
            if (CollUtil.isEmpty(tbWtMonitorPointList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "选中的库水位监测点不存在或者不是库水位监测点");
            } else {
                this.wtMonitorPoint = tbWtMonitorPointList.stream().findAny().orElseThrow();
            }
            if (!tbMonitorGroupPointMapper.exists(new LambdaQueryWrapper<TbMonitorGroupPoint>()
                    .eq(TbMonitorGroupPoint::getMonitorGroupID, getMonitorGroupID()).eq(TbMonitorGroupPoint::getMonitorPointID, wtPointID))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "库水位监测点不属于该监测点组");
            }
            List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class)
                    .selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getMonitorPointID, wtPointID));
            if (CollUtil.isEmpty(sensorList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "库水位监测点未绑定传感器");
            }
            if (sensorList.size() > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "库水位监测点绑定了多个传感器");
            }
            this.wtSensor = sensorList.stream().findAny().orElseThrow();
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
