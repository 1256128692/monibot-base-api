package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IVideoCameraCheck;
import cn.shmedo.monitor.monibotbaseapi.util.CustomWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-04 14:02
 */
@Data
@Slf4j
public class HikVideoBaseParam implements ParameterValidator, IVideoCameraCheck {
    @Positive(message = "视频设备ID不能小于1")
    private Integer videoDeviceID;
    @Positive(message = "传感器ID不能小于1")
    private Integer sensorID;
    @Positive(message = "监测点ID不能小于1")
    private Integer monitorPointID;
    @JsonIgnore
    private TbSensor tbSensor;
    @JsonIgnore
    private TbVideoDevice tbVideoDevice;

    @Override
    public ResultWrapper validate() {
        if (!valid()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备ID、传感器ID、监测点ID三组数据要求仅有一组不为空!");
        }
        final TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        if (Objects.nonNull(this.monitorPointID)) {
            ResultWrapper<List<TbSensor>> resultWrapper = checkMonitorPoint();
            if (!resultWrapper.apiSuccess()) {
                return resultWrapper;
            }
            this.tbSensor = resultWrapper.getData().get(0);
            this.sensorID = this.tbSensor.getID();
            this.videoDeviceID = this.tbSensor.getVideoDeviceID();
        }
        if (Objects.nonNull(this.videoDeviceID)) {
            List<TbVideoDevice> tbVideoDeviceList = ContextHolder.getBean(TbVideoDeviceMapper.class)
                    .selectList(new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getID, this.videoDeviceID));
            if (CollUtil.isEmpty(tbVideoDeviceList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在!");
            }
            tbVideoDevice = tbVideoDeviceList.get(0);
            if (!tbVideoDevice.getAccessPlatform().equals(AccessPlatformType.HAI_KANG.getValue())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不是海康设备!");
            }
            List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getVideoDeviceID, this.videoDeviceID));
            if (CollUtil.isEmpty(sensorList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备未关联传感器!");
            }
            this.tbSensor = sensorList.get(0);
            this.sensorID = this.tbSensor.getID();
        }
        if (Objects.isNull(tbSensor)) {
            List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getID, this.sensorID));
            if (CollUtil.isEmpty(sensorList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不存在!");
            }
            this.tbSensor = sensorList.get(0);
            this.videoDeviceID = this.tbSensor.getVideoDeviceID();
        }
        if (Objects.isNull(this.videoDeviceID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器未关联视频设备!");
        }
        if (!tbSensor.getMonitorType().equals(MonitorType.VIDEO.getKey())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不是视频传感器!");
        }
        return null;
    }

    @Override
    public boolean valid() {
        final CustomWrapper<Integer> wrapper = new CustomWrapper<>(1);
        Optional.ofNullable(getVideoDeviceID()).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(getSensorID()).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(getMonitorPointID()).ifPresent(u -> wrapper.setValue(v -> v - 1));
        return wrapper.get() == 0;
    }
}
