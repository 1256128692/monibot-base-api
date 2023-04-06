package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.Data;

import java.util.List;

/**
 * 带传感器的设备简单信息
 *
 * @author Chengfs on 2023/4/6
 */
@Data
public class DeviceWithSensor {

    private Integer ID;
    private Integer productID;
    private String deviceName;
    private String deviceToken;
    private String uniqueToken;

    private List<Sensor> sensorList;

    public record Sensor(Integer id, String sensorName, String iotSensorType, String alias) {
    }
}

    
    