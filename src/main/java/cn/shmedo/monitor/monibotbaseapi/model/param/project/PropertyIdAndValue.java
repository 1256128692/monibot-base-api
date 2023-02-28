package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyIdAndValue {
    @NotNull
    private Integer propertyID;
    private String value;
}
