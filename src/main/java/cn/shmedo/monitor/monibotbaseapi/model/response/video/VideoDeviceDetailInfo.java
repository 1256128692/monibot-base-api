package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VideoDeviceDetailInfo {

    private Integer companyID;
    private Integer videoDeviceID;
    private String deviceSerial;
    private String deviceType;
    private String deviceName;
    private Byte accessPlatform;
    private String accessPlatformStr;

    private Boolean deviceStatus;
    /**
     * 海康默认为1,萤石云根据具体的ysChannelInfoList数量
     */
    private Integer deviceChannelNum;
    private Integer accessChannelNum;
    private Date createTime;

    private List<VideoCaptureBaseInfoV2> videoDeviceSourceList;

}
