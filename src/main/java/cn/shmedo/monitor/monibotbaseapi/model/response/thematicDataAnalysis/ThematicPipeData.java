package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-15 10:35
 */
@Data
@Builder
public class ThematicPipeData {
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer emptyPipeDistance;
    private ThematicLevelElevationInfo levelElevation;
}
