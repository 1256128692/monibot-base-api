package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 14:37
 */
@Data
public class DatumPointConfig {
    @Positive(message = "监测点ID必须大于0")
    @NotNull(message = "监测点ID不能为空")
    private Integer monitorPointID;
    @NotNull(message = "向上波动（米）不能为空")
    @Positive(message = "向上波动（米）必须大于0")
    private Double upper;
    @NotNull(message = "向下波动（米）不能为空")
    @Positive(message = "向下波动（米）必须大于0")
    private Double lower;
}
