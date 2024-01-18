package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 10:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryWarnThresholdConfigListParam extends QueryThresholdBaseConfigParam {
    private Boolean status;
    private List<@Valid @Positive(message = "监测点ID必须为正值") Integer> monitorPointIDList;
    private List<@Valid @Positive(message = "传感器ID必须为正值") Integer> sensorIDList;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (CollUtil.isNotEmpty(monitorPointIDList) && !ContextHolder.getBean(TbMonitorPointMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorPoint>().in(TbMonitorPoint::getID, monitorPointIDList)
                        .eq(TbMonitorPoint::getMonitorItemID, getMonitorItemID()))
                .stream().map(TbMonitorPoint::getID).collect(Collectors.toSet()).containsAll(monitorPointIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点ID列表中存在不属于监测项目的监测点");
        }
        if (CollUtil.isNotEmpty(sensorIDList) && !ContextHolder.getBean(TbSensorMapper.class).selectList(
                        new LambdaQueryWrapper<TbSensor>().in(TbSensor::getID, sensorIDList).eq(TbSensor::getProjectID, getProjectID()))
                .stream().map(TbSensor::getID).collect(Collectors.toSet()).containsAll(sensorIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器ID列表中存在不属于工程的传感器");
        }
        return null;
    }
}
