package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class VideoDeviceInfoV6 {

    private Integer projectID;

    private Integer monitorPointID;
    private Integer videoDeviceSourceID;

    private String deviceSerial;

}
