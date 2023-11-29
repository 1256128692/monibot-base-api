package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 10:03
 */
@Data
public class ManualDataItem {
    @NotNull(message = "传感器ID不能为空")
    @Positive(message = "传感器ID不能小于0")
    private Integer sensorID;
    @NotNull(message = "时间不能为空")
    private Date time;
    @NotEmpty(message = "属性标识不能为空")
    private String fieldToken;
    @NotEmpty(message = "值不能为空")
    private String value;
    @JsonIgnore
    private Integer monitorType;
}
