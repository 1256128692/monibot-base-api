package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-23 10:23
 */
@Data
@Builder(toBuilder = true)
public class SensorBaseInfo {
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
}
