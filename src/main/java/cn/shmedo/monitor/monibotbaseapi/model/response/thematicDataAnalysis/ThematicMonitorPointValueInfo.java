package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 16:18
 */
@Data
@Builder
public class ThematicMonitorPointValueInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Double value;
    private Double abnormalValue;
}
