package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceSourceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDeviceSource;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCaptureBaseInfoV2;
import cn.shmedo.monitor.monibotbaseapi.util.CustomWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    Integer getVideoDeviceID();

    Integer getMonitorPointID();

    Integer getSensorID();

    void setVideoDeviceID(Integer videoDeviceID);

    void setMonitorPointID(Integer monitorPointID);

    void setSensorID(Integer sensorID);

    void setTbSensor(TbSensor tbSensor);

    TbSensor getTbSensor();

    void setTbVideoDevice(TbVideoDevice tbVideoDevice);

    /**
     * 这里提供一种等权重实现，可适用于海康设备<br>
     */
    default boolean valid() {
        final CustomWrapper<Integer> wrapper = new CustomWrapper<>(1);
        Optional.ofNullable(getVideoDeviceID()).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(getSensorID()).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(getMonitorPointID()).ifPresent(u -> wrapper.setValue(v -> v - 1));
        return wrapper.get() == 0;
    }

    default ResultWrapper<Void> checkMonitorPoint() {
        if (!ContextHolder.getBean(TbMonitorPointMapper.class).exists(new LambdaQueryWrapper<TbMonitorPoint>()
                .eq(TbMonitorPoint::getID, getMonitorPointID()).eq(TbMonitorPoint::getMonitorType, MonitorType.VIDEO.getKey()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点不存在!");
        }
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getMonitorPointID, getMonitorPointID()));
        if (CollUtil.isEmpty(sensorList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点未关联传感器!");
        }
        if (sensorList.size() > 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点关联了多个传感器!");
        }
        TbSensor tbSensor = sensorList.get(0);
        Integer monitorType = tbSensor.getMonitorType();
        if (Objects.isNull(monitorType) || !monitorType.equals(MonitorType.VIDEO.getKey())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点关联的传感器不是视频传感器!");
        }
        Integer videoDeviceSourceID = tbSensor.getVideoDeviceSourceID();
        if (ObjectUtil.isEmpty(videoDeviceSourceID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点关联的传感器未关联视频设备!");
        }
        ResultWrapper<Void> resultWrapper = checkVideoSource(videoDeviceSourceID);
        if (!resultWrapper.apiSuccess()) {
            return resultWrapper;
        }
        setTbSensor(tbSensor);
        setSensorID(tbSensor.getID());
        return ResultWrapper.successWithNothing();
    }

    default ResultWrapper<Void> basicCheckSensor() {
        List<TbSensor> tbSensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getMonitorType, MonitorType.VIDEO.getKey()).eq(TbSensor::getID, getSensorID()));
        if (CollUtil.isEmpty(tbSensorList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频传感器不存在!");
        }
        ResultWrapper<Void> resultWrapper = checkVideoSource(tbSensorList.get(0).getVideoDeviceSourceID());
        if (!resultWrapper.apiSuccess()) {
            return resultWrapper;
        }
        return ResultWrapper.successWithNothing();
    }

    default ResultWrapper<Void> checkVideoSource(Integer videoDeviceSourceID) {
        if (ObjectUtil.isEmpty(videoDeviceSourceID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频传感器未关联视频数据源!");
        }
        TbVideoDeviceSource tbVideoDeviceSource = ContextHolder.getBean(TbVideoDeviceSourceMapper.class).selectByPrimaryKey(videoDeviceSourceID);
        if (Objects.isNull(tbVideoDeviceSource)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频数据源不存在!");
        }
        setDeviceChannel(tbVideoDeviceSource.getChannelNo());
        String deviceSerial = tbVideoDeviceSource.getDeviceSerial();
        if (ObjectUtil.isEmpty(deviceSerial)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备唯一标识为空!");
        }
        List<TbVideoDevice> tbVideoDeviceList = ContextHolder.getBean(TbVideoDeviceMapper.class)
                .selectList(new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getDeviceSerial, deviceSerial));
        if (CollUtil.isEmpty(tbVideoDeviceList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在!");
        }

        setVideoDeviceID(tbVideoDeviceList.get(0).getID());
        return ResultWrapper.successWithNothing();
    }

    default ResultWrapper<Void> checkVideoDevice(AccessPlatformType type) {
        List<TbVideoDevice> tbVideoDeviceList = ContextHolder.getBean(TbVideoDeviceMapper.class)
                .selectList(new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getID, getVideoDeviceID()));
        if (CollUtil.isEmpty(tbVideoDeviceList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在!");
        }
        TbVideoDevice tbVideoDevice = tbVideoDeviceList.get(0);
        if (ObjectUtil.isEmpty(tbVideoDevice.getDeviceSerial())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备序列号/唯一标识为空!");
        }
        Byte accessPlatform = tbVideoDevice.getAccessPlatform();
        setTbVideoDevice(tbVideoDevice);
        if (ObjectUtil.isEmpty(accessPlatform) || !accessPlatform.equals(type.getValue())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不是" + type.getDescription() + "设备!");
        }
        List<VideoCaptureBaseInfoV2> videoCaptureBaseInfoV2s = ContextHolder.getBean(TbVideoDeviceSourceMapper.class).selectByDeviceSerial(tbVideoDevice.getDeviceSerial());
        if (CollUtil.isEmpty(videoCaptureBaseInfoV2s)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备未关联视频源!");
        }
        Integer deviceChannel = getDeviceChannel();
        if (Objects.nonNull(deviceChannel)) {
            Integer channelNo = videoCaptureBaseInfoV2s.stream().filter(u -> u.getChannelNo().equals(deviceChannel))
                    .findFirst().map(VideoCaptureBaseInfoV2::getChannelNo).orElse(null);
            if (Objects.isNull(channelNo)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "通道号错误!");
            }
        }
        return ResultWrapper.successWithNothing();
    }

    default ResultWrapper<Void> checkPointAndSensor() {
        if (Objects.nonNull(getMonitorPointID())) {
            ResultWrapper<Void> resultWrapper = checkMonitorPoint();
            if (!resultWrapper.apiSuccess()) {
                return resultWrapper;
            }
        }
        if (Objects.isNull(getTbSensor()) && Objects.nonNull(getSensorID())) {
            ResultWrapper<Void> resultWrapper = basicCheckSensor();
            if (!resultWrapper.apiSuccess()) {
                return resultWrapper;
            }
        }
        return ResultWrapper.successWithNothing();
    }

    default Integer getDeviceChannel() {
        return null;
    }

    default void setDeviceChannel(Integer deviceChannel) {
    }
}
