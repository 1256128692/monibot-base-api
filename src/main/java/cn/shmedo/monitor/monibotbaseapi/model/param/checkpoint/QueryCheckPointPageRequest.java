package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryCheckPointPageRequest extends QueryCheckPointListRequest {

    @NotNull
    @Min(value = 1, message = "当前页不能小于1")
    private Integer currentPage;

    @NotNull
    @Range(min = 1, max = 100, message = "分页大小必须在1~100")
    private Integer pageSize;

}