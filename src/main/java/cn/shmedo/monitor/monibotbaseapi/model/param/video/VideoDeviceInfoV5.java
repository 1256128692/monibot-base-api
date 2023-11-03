package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class VideoDeviceInfoV5 extends TbVideoDevice {

    private Integer channelCode;
}
