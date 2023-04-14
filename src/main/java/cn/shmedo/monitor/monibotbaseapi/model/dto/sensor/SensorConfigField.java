package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensorConfigField {
    //Integer id, String name, String value

    @NotNull
    private Integer id;
    private String name;
    private String value;
}
