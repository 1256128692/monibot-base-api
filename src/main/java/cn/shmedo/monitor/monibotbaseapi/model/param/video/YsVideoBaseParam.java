package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
 * @date 2023-09-04 14:01
 */
@Data
@Slf4j
public class YsVideoBaseParam implements ParameterValidator, IVideoCameraCheck {
    @Positive(message = "视频设备ID不能小于1")
    private Integer deviceVideoID;
    @Positive(message = "传感器ID不能小于1")
    private Integer sensorID;
    @Positive(message = "监测点ID不能小于1")
    private Integer monitorPointID;
    @Positive(message = "通道号不能小于1")
    private Integer deviceChannel;
    @JsonIgnore
    private TbSensor tbSensor;
    @JsonIgnore
    private TbVideoDevice tbVideoDevice;

    @Override
    public ResultWrapper validate() {
        if (!valid()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备通道、传感器ID、监测点ID三组数据要求仅有一组不为空!");
        }
        final TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        if (Objects.nonNull(this.monitorPointID)) {
            ResultWrapper<List<TbSensor>> resultWrapper = checkMonitorPoint();
            if (!resultWrapper.apiSuccess()) {
                return resultWrapper;
            }
            tbSensor = resultWrapper.getData().get(0);
            this.sensorID = tbSensor.getID();
        }
        if (Objects.nonNull(this.sensorID)) {
            if (Objects.isNull(tbSensor)) {
                List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getID, this.sensorID));
                if (CollUtil.isEmpty(sensorList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不存在!");
                }
                tbSensor = sensorList.get(0);
            }
            this.deviceVideoID = tbSensor.getVideoDeviceID();
            final String exValues = tbSensor.getExValues();
            if (Objects.isNull(this.deviceVideoID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器未关联视频设备!");
            }
            if (ObjectUtil.isEmpty(exValues)) {
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "该视频设备未配置通道信息!");
            }
        }
        List<TbVideoDevice> tbVideoDeviceList = ContextHolder.getBean(TbVideoDeviceMapper.class)
                .selectList(new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getID, this.deviceVideoID));
        if (CollUtil.isEmpty(tbVideoDeviceList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在!");
        }
        tbVideoDevice = tbVideoDeviceList.get(0);
        final Byte accessPlatform = tbVideoDevice.getAccessPlatform();
        if (!accessPlatform.equals(AccessPlatformType.YING_SHI.getValue())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不是萤石设备!");
        }
        if (Objects.isNull(tbSensor)) {
            List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getVideoDeviceID, this.deviceVideoID));
            if (CollUtil.isEmpty(sensorList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备未关联传感器!");
            }
            try {
                // {@code exValueList} might be empty,for setting {@code ExValue} mistakenly which is occurred in table `tb_sensor`.
                tbSensor = sensorList.stream().filter(u -> Objects.nonNull(u.getExValues())).filter(u -> this
                        .deviceChannel.equals(Integer.parseInt(JSONUtil.parseObj(u.getExValues())
                                .getStr("ysChannelNo")))).findAny().orElse(null);
                if (Objects.isNull(tbSensor)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备通道号不存在!");
                }
            } catch (JSONException e) {
                log.error("解析视频设备关联的传感器ExValue失败,exValue list: {}", JSONUtil.toJsonStr(sensorList.stream()
                        .map(TbSensor::getExValues).filter(ObjectUtil::isNotEmpty).toList()));
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "服务端解析视频设备通道号失败!");
            }
        }
        if (!tbSensor.getMonitorType().equals(MonitorType.VIDEO.getKey())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不是视频传感器!");
        }
        return null;
    }

    @Override
    public boolean valid() {
        final CustomWrapper<Integer> wrapper = new CustomWrapper<>(3);
        Optional.ofNullable(this.deviceVideoID).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(this.deviceChannel).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(this.sensorID).ifPresent(u -> wrapper.setValue(v -> v - 2));
        Optional.ofNullable(this.monitorPointID).ifPresent(u -> wrapper.setValue(v -> v - 2));
        return wrapper.get() == 1;
    }
}
