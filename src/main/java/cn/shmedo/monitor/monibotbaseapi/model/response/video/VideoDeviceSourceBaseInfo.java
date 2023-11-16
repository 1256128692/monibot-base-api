package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class VideoDeviceSourceBaseInfo {

    private String videoDeviceSourceID;

    @JsonIgnore
    private String deviceSerial;
    private Integer channelCode;

}
