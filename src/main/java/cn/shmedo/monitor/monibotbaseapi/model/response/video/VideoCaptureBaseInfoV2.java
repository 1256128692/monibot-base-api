package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkChannelInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsChannelInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class VideoCaptureBaseInfoV2 {

    // 可以为空
    private Integer sensorID;
    private String sensorName;
    private Boolean sensorEnable;
    // 可以为空
    private Integer projectID;
    private Integer channelNo;
    private String deviceSerial;
    private Boolean enable;
    private Integer monitorPointID;
    private Integer monitorItemID;

    private String projectName;
    private String monitorPointName;
    private String monitorItemName;
    private String gpsLocation;
    private String location;

    private String locationInfo;
    private Integer videoDeviceSourceID;

}
