package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

@Data
public class VideoDeviceOnlineInfo {


    private Integer videoCount;
    private Integer videoOnlineCount;
    private Integer videoOfflineCount;

}
