package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 18:16
 */
@Data
@Builder
public class ThematicDryBeachInfo {
    private Date time;
    private Double slopeRratio;
    private Double rainfall;
    private Map<String, Double> dryBeach;
    private Map<String, Double> distance;
}
