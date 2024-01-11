package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 13:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryWarnNotifyConfigDetailParam extends CompanyPlatformParam {
    @NotNull(message = "报警通知配置ID不能为空")
    @Positive(message = "报警通知配置ID不能小于1")
    private Integer notifyConfigID;
}
