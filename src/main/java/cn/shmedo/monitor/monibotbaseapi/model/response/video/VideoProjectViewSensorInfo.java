package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 11:29
 */
@Data
public class VideoProjectViewSensorInfo {
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private Integer videoDeviceID;
    private Boolean deviceStatus;
    private String deviceSerial;
    private String deviceName;
    private String deviceType;
    private Integer accessChannelNum;
    private Integer accessPlatform;
    private Integer accessProtocol;
    private String deviceToken;
    private Integer companyID;
    private Integer projectID;
    private Integer storageType;
    private Boolean captureStatus;
    private Boolean allocationStatus;
    @JsonIgnore
    private Integer channelNo;
}
