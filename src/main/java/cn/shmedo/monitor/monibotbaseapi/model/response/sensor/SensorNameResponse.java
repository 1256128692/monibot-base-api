package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-17 14:49
 */
@Data
@Builder
public class SensorNameResponse {
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
}
