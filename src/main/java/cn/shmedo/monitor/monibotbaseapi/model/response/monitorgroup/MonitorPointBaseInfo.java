package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-23 10:21
 */
@Data
@Builder(toBuilder = true)
public class MonitorPointBaseInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Boolean monitorPointEnable;
    private Boolean multiSensor;
    private Integer monitorType;
    private Integer monitorItemID;
    private List<SensorBaseInfo> sensorDataList;
}
