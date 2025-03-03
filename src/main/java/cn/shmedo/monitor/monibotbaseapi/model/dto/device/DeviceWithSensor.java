package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 带传感器的设备简单信息
 *
 * @author Chengfs on 2023/4/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceWithSensor {

    private Integer ID;
    private Integer productID;
    private String deviceName;
    private String deviceToken;
    private String uniqueToken;
    private List<Sensor> sensorList;

    @Data
    public static class Sensor{
        private Integer id;
        private String sensorName;
        private String iotSensorType;
        private String alias;
    }
}

    
    