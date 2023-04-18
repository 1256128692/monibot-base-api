package cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 13:20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointIDAndLocation {
    @NotNull
    private Integer pointID;
    @Pattern(regexp = "\\d+(.\\d+)?,\\d+(.\\d+)?", message = "坐标格式错误")
    @NotBlank
    private String location;
}
