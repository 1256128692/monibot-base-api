package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IThresholdConfigValueCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-24 17:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnThresholdConfigEnableBatchParam extends CompanyPlatformParam implements IThresholdConfigValueCheck {
    @NotNull(message = "监测项目ID不能为空")
    private Integer monitorItemID;
    @NotEmpty(message = "传感器ID不能为空")
    private List<Integer> sensorIDList;
    @NotNull(message = "启用/禁用不能为空")
    private Boolean enable;
    @JsonIgnore
    private List<TbWarnThresholdConfig> updateList;

    @Override
    public ResultWrapper<?> validate() {
        Date current = new Date();
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (!ContextHolder.getBean(TbMonitorItemMapper.class).exists(new LambdaQueryWrapper<TbMonitorItem>().eq(TbMonitorItem::getID, monitorItemID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        sensorIDList = sensorIDList.stream().distinct().toList();
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getID, sensorIDList).isNotNull(TbSensor::getMonitorPointID));
        if (sensorList.size() != sensorIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在或未关联监测点");
        }
        List<TbMonitorPoint> tbMonitorPointList = ContextHolder.getBean(TbMonitorPointMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorPoint>().in(TbMonitorPoint::getID, sensorList.stream()
                        .map(TbSensor::getMonitorPointID).collect(Collectors.toSet())));
        Set<Integer> monitorItemIDSet = tbMonitorPointList.stream().map(TbMonitorPoint::getMonitorItemID).collect(Collectors.toSet());
        if (monitorItemIDSet.size() != 1 || !monitorItemIDSet.stream().findAny().orElseThrow().equals(monitorItemID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不属于该监测项目");
        }

        // 拿到需要处理的数据，如果是未启用->启用，额外增加一层过滤过滤掉无法启用的数据
        // 然后批量编辑
        updateList = ContextHolder.getBean(TbWarnThresholdConfigMapper.class).selectList(new LambdaQueryWrapper<TbWarnThresholdConfig>()
                        .eq(TbWarnThresholdConfig::getPlatform, getPlatform()).eq(TbWarnThresholdConfig::getMonitorItemID, monitorItemID)
                        .eq(TbWarnThresholdConfig::getEnable, !enable).in(TbWarnThresholdConfig::getSensorID, sensorIDList))
                .stream().peek(u -> {
                    u.setEnable(enable);
                    u.setUpdateTime(current);
                }).toList();
        if (enable) {
            updateList = updateList.stream().filter(u -> queryConfigStatus(u.getValue(), true)).toList();
        }
        return null;
    }
}
