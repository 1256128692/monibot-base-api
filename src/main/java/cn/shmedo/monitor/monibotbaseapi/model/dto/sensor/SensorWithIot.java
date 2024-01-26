package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/1/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorWithIot {

    private Integer projectID;
    private Integer monitorType;
    private String iotUniqueToken;
    private String iotSensorName;
}