package cn.shmedo.monitor.monibotbaseapi.model.response.video;


import lombok.Data;

import java.io.Serializable;

@Data
public class VideoDeviceBaseInfoV1 implements Serializable {


    private String deviceSerial;
    private String deviceType;
    private String deviceName;


}
