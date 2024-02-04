package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-14 11:45
 */
@Data
@Builder
public class DryBeachDataInfo {
    private Double slopeRratio;
    private Double rainfall;
    private Double dryBeach;
    private Double distance;
}
