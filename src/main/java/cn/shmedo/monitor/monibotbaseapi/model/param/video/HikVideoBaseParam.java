package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IVideoCameraCheck;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
        ResultWrapper<Void> checkPointAndSensor = checkPointAndSensor();
        if (!checkPointAndSensor.apiSuccess()) {
            return checkPointAndSensor;
        }
        ResultWrapper<Void> resultWrapper = checkVideoDevice(AccessPlatformType.HAI_KANG);
        if (!resultWrapper.apiSuccess()) {
            return resultWrapper;
        }
        return null;
    }
}
