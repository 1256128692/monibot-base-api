package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-2023-04-18 18:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateWorkOrderStatusParam extends DeleteWorkOrderParam {
    @Range(min = 1, max = 4, message = "工单状态取值范围1~4")
    @NotNull(message = "工单状态不能为空")
    private Integer status;
}
