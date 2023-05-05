package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

import java.util.Date;

@Data
public class VideoMonitorPointPictureInfo {

    private Date uploadTime;

    private String filePath;


    private Integer sensorID;

}
