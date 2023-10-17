package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SensorBaseInfoV2 {

    private Integer sensorID;

    private String sensorName;

    private Boolean sensorEnable;

    private Integer projectID;

    @JsonIgnore
    private Integer videoDeviceSourceID;
}
