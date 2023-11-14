package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import lombok.Data;

@Data
public class VideoDeviceInfoV4 {


    private Integer videoDeviceID;

    private String deviceToken;

    private Integer projectID;

    private Integer companyID;

    private Integer iotDeviceID;
}
