package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddPresetPointParam extends QueryPresetPointListParam {
    @NotEmpty(message = "预置点名称不能为空")
    private String presetPointName;
    @JsonIgnore
    private Integer presetPointIndex;
}
