package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

import java.util.Date;

@Data
public class VideoDevicePageInfo {

    private Integer videoDeviceID;
    private String deviceSerial;
    private String deviceType;
    private Integer companyID;
    private String companyName;
    private Boolean deviceStatus;
    private Integer accessChannelNum;
    private Integer sensorNum;
    private Byte accessPlatform;
    private String accessPlatformStr;
    private Byte accessProtocol;
    private String accessProtocolStr;
    private Integer projectID;
    private String projectName;
    private Boolean captureStatus;
    private Boolean allocationStatus;
    private Integer createUserID;
    private Date createTime;
    private Integer updateUserID;
    private Date updateTime;


}
