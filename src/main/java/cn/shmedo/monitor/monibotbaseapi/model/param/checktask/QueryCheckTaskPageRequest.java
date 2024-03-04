package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QueryCheckTaskPageRequest extends QueryCheckTaskListRequest {

    @NotNull
    @Min(value = 1, message = "当前页不能小于1")
    private Integer currentPage;

    @NotNull
    @Range(min = 1, max = 100, message = "分页大小必须在1~100")
    private Integer pageSize;

}