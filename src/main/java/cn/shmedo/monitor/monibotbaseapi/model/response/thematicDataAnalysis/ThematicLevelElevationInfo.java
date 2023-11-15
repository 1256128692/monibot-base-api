package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 14:34
 */
@Data
@Builder
public class ThematicLevelElevationInfo {
    private Double value;
    private Double osmoticValue;
    private ThematicLongitudinalEigenValueData eigenValue;
}
