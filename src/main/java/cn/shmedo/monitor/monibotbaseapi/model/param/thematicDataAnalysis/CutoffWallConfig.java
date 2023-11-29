package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 13:30
 */
@Data
public class CutoffWallConfig {
    @NotNull(message = "阈值不能为空")
    @Positive(message = "阈值必须大于0")
    private Double value;
    @Size(min = 2, max = 2, message = "阈值两侧监测点IDList的长度必须为2")
    @NotEmpty(message = "阈值两侧监测点ID List不能为空")
    private List<Integer> monitorPointIDList;
}
