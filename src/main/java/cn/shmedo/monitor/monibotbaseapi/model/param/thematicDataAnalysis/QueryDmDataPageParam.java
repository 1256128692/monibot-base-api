package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-26 18:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDmDataPageParam extends QueryDmDataParam {
    //用户所选的数据列表,为空时查询全部(如果)
    private List<Date> dataList;
    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;
    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
}
