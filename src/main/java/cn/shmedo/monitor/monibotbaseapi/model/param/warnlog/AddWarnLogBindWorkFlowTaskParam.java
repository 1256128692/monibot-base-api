package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-22 14:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddWarnLogBindWorkFlowTaskParam extends WarnHandleParam {
    @NotNull(message = "工单ID不能为空")
    @Positive(message = "工单ID必须大于1")
    private Integer workOrderID;
}
