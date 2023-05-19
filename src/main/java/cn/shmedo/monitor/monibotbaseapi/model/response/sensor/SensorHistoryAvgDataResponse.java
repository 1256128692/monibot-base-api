package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class SensorHistoryAvgDataResponse implements Serializable {

    private Integer projectID;
    private String projectName;
    private String projectShortName;
    private Integer monitorPointID;
    private Integer monitorType;
    private String monitorPointName;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private String configFieldValue;
    /**
     * 传感器历史数据
     */
    Map<String, Object> sensorData;

    private Date time;
}
