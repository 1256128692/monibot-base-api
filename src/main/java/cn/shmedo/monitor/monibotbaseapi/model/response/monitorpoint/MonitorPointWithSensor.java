package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;


import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import lombok.Data;

import java.util.List;

@Data
public class MonitorPointWithSensor {

    private Integer projectID;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private List<SensorBaseInfoResponse> sensorList;

}
