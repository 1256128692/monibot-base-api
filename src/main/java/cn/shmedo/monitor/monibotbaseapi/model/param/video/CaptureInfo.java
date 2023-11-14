package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import lombok.Data;

@Data
public class CaptureInfo {

    private String deviceSerial;
    private Integer videoDeviceSourceID;
    private Boolean imageCapture;
    private Integer captureInterval;

}
