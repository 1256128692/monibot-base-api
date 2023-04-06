package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyIdAndValue {
    @NotNull
    private Integer ID;
    private String value;
}
