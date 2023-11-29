package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

import java.util.LinkedList;
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

    /**
     * 单个传感器每个属性的最大值以及对应时间段
     */
    private Map<String, Map<String, Object>> maxSensorDataList;

    /**
     * 单个传感器每个属性的最小值以及对应时间段
     */
    private Map<String, Map<String, Object>> minSensorDataList;

}
