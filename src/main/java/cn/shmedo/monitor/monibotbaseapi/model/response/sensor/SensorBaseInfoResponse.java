package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SensorBaseInfoResponse {

    private Integer projectID;
    private Integer monitorPointID;
    private Integer monitorType;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private String configFieldValue;
    /**
     * 批量传感器的数据信息
     */
    private List<Map<String, Object>> multiSensorData;

}
