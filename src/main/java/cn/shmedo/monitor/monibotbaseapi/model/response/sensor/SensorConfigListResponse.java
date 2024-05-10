package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author wuxl
 * @Date 2024/5/7 17:23
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.sensor
 * @ClassName: SensorConfigListResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class SensorConfigListResponse {
    @JsonProperty("deviceID")
    private Integer ID;
    private String deviceToken;
    private String deviceName;
    private String uniqueToken;
    @JsonProperty("deviceSensorList")
    private List<DeviceSensor> sensorList;

    @Data
    public static class DeviceSensor {
        @JsonProperty("deviceSensorID")
        private Integer id;
        @JsonProperty("name")
        private String sensorName;
        private String alias;
        private List<MonitorSensor> monitorSensorList;
    }

    @Data
    public static class MonitorSensor{
        @JsonProperty("monitorSensorID")
        private Integer ID;
        private String name;
        private Integer monitorType;
        private String monitorTypeName;
        private Integer monitorItemID;
        private String monitorItemName;
        private Integer monitorPointID;
        private String monitorPointName;
        @JsonIgnore
        private String dataSourceToken;
        private List<MonitorGroup> monitorGroupList;
    }

    @Data
    public static class MonitorGroup{
        private Integer monitorGroupID;
        private String monitorGroupName;
    }
}
