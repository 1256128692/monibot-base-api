package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 15:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryRainWaterDataPageParam extends QueryRainWaterDataBaseInfo {
    @NotNull(message = "页大小不能为空")
    @Positive(message = "页大小必须大于0")
    private Integer pageSize;
    @NotNull(message = "当前页不能为空")
    @Positive(message = "当前页必须大于0")
    private Integer currentPage;
}
