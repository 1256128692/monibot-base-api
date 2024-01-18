package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 11:10
 */
@Data
public class WarnThresholdSensorInfo {
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private List<WarnFieldThresholdConfigInfo> fieldList;
}
