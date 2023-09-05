package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-05 10:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoDeviceWithSensorIDInfo extends TbVideoDevice {
    private Integer sensorID;
}
