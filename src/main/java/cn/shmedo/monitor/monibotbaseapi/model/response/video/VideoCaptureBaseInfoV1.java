package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

@Data
public class VideoCaptureBaseInfoV1 {

    private Integer channelNo;

    private Boolean enable;

    private Integer captureInterval;

    private Boolean imageCapture;
}
