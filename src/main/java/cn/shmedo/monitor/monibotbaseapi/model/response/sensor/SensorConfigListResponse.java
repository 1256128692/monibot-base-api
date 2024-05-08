package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

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
    @JsonProperty("ID")
    private Integer ID;
    private String deviceToken;
    private String deviceName;
    private List<DeviceSensor> deviceSensorList;

    private static class DeviceSensor {
        @JsonProperty("ID")
        private Integer ID;
        private Integer projectID;
        private String name;
        private String alias;
        private String uniqueToken;
        private List<MonitorSensor> monitorSensorList;
    }
    public static class MonitorSensor{
        @JsonProperty("ID")
        private Integer ID;
        private String name;
        private String monitorTypeID;
        private String monitorTypeName;
        private String monitorPointID;
        private String monitorPointName;
        private String monitorGroupID;
        private String monitorGroupName;
    }
}
