package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * 唯一确定某个视频摄像头的三组入参校验接口，校验方法{@link #valid()}。<br>
 * 如果需要确定某个摄像头，可以直接通过监测点ID/传感器ID确定（视频摄像头监测点仅能绑定一个传感器），也可以通过设备信息确定。<br>
 * <p>设备信息:<br>
 * 单设备对应多通道，每个通道都是一个摄像头的设备: 设备ID+通道号；（如萤石）<br>
 * 单设备对应一通道的设备: 设备ID。（如海康）<br>
 * </p>
 *
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-04 11:08
 * @see #valid()
 */
public interface IVideoCameraCheck {
    Integer getDeviceVideoID();

    Integer getMonitorPointID();

    Integer getSensorID();

    boolean valid();

    default ResultWrapper<List<TbSensor>> checkMonitorPoint() {
        if (!ContextHolder.getBean(TbMonitorPointMapper.class).exists(new LambdaQueryWrapper<TbMonitorPoint>()
                .eq(TbMonitorPoint::getID, getMonitorPointID()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点不存在!");
        }
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getMonitorPointID, getMonitorPointID()));
        if (CollUtil.isEmpty(sensorList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点未绑定传感器!");
        }
        if (sensorList.size() > 1) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "视频监测点绑定了多个传感器!");
        }
        return ResultWrapper.success(sensorList);
    }
}
