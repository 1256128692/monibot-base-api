package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 15:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThematicPointListInfoV2 extends ThematicPointListInfo {
    private String sensorExValues;
    @JsonIgnore
    private Double emptyPipeDistance;
    private Double levelElevation;
    private List<ThematicProjectConfigInfo> monitorPointConfigList;
    private List<ThematicEigenValueData> eigenValueList;

    @JsonProperty("nozzleElevation")
    private Double nozzleElevation() {
        return Objects.nonNull(emptyPipeDistance) && Objects.nonNull(levelElevation) ? emptyPipeDistance + levelElevation : null;
    }

    @JsonProperty("emptyPipeDistance")
    private Double emptyPipeDistance() {
        return Objects.nonNull(emptyPipeDistance) && emptyPipeDistance < 0 ? 0 : emptyPipeDistance;
    }
}
