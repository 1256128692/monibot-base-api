package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

@Data
public class SensorBaseInfoResponse {

    private Integer projectID;
    private Integer monitorPointID;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;

}
