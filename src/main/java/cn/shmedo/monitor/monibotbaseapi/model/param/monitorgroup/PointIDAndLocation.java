package cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 13:20
 **/
@Data
public class PointIDAndLocation {
    @NonNull
    private Integer pointID;
    // regex : "12.13,12,13"
    @Pattern(regexp = "^(\\d+\\.\\d+,){2}\\d+\\.\\d+$")
    private String location;
}
