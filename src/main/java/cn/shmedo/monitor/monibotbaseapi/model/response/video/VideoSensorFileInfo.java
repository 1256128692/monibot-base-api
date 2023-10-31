package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
public class VideoSensorFileInfo {


    private Integer ID;
    private Date uploadTime;
    private Integer videoDeviceSourceID;
    @JsonIgnore
    private String filePath;
    private String path;
    private String fileType;
    private Integer fileSize;
}
