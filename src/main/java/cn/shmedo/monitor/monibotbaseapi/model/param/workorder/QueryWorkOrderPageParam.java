package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-2023-04-18 18:03
 */
@Data
public class QueryWorkOrderPageParam {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    //TODO
}
