package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-04-28 09:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryMonitorPointDataListPageParam extends QueryMonitorPointDataParam {
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "页大小 1~100")
    private Integer pageSize;
    @NotNull(message = "当前页不能为空")
    @Positive(message = "当前页不能小于1")
    private Integer currentPage;
}
