package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IVideoCameraCheck;
import cn.shmedo.monitor.monibotbaseapi.util.CustomWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-04 14:01
 */
@Data
@Slf4j
public class YsVideoBaseParam implements ParameterValidator, IVideoCameraCheck {
    @Positive(message = "视频设备ID不能小于1")
    private Integer videoDeviceID;
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
        ResultWrapper<Void> checkPointAndSensor = checkPointAndSensor();
        if (!checkPointAndSensor.apiSuccess()) {
            return checkPointAndSensor;
        }
        ResultWrapper<Void> resultWrapper = checkVideoDevice(AccessPlatformType.YING_SHI);
        if (!resultWrapper.apiSuccess()) {
            return resultWrapper;
        }
        return null;
    }

    @Override
    public boolean valid() {
        final CustomWrapper<Integer> wrapper = new CustomWrapper<>(3);
        Optional.ofNullable(this.videoDeviceID).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(this.deviceChannel).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(this.sensorID).ifPresent(u -> wrapper.setValue(v -> v - 2));
        Optional.ofNullable(this.monitorPointID).ifPresent(u -> wrapper.setValue(v -> v - 2));
        return wrapper.get() == 1;
    }

    public String channelNo() {
        return this.deviceChannel.toString();
    }
}
