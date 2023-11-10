package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class VideoBaseInfo {

    private Integer videoDeviceID;

    @JsonIgnore
    private Integer projectID;
    private String deviceSerial;
    private Byte accessPlatform;
    private String accessPlatformStr;
    private String deviceName;

    private Boolean deviceStatus;

    private List<VideoDeviceSourceBaseInfo> videoSourceInfoList;


}
