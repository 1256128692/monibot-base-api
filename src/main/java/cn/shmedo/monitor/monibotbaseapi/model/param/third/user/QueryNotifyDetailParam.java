package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-02 17:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryNotifyDetailParam {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotNull(message = "通知ID不能为空")
    private Integer notifyID;
}
