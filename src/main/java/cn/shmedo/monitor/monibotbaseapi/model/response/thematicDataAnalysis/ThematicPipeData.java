package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-15 10:35
 */
@Data
@Builder
public class ThematicPipeData {
    private Integer monitorPointID;
    private String monitorPointName;
    @JsonIgnore
    private Double emptyPipeDistance;
    private ThematicLevelElevationInfo levelElevation;

    @JsonProperty("emptyPipeDistance")
    private Double emptyPipeDistance() {
        return Objects.nonNull(emptyPipeDistance) && emptyPipeDistance < 0 ? 0 : emptyPipeDistance;
    }
}
