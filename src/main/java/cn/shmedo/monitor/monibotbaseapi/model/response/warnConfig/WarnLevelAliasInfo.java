package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 17:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnLevelAliasInfo {
    @NotNull(message = "报警等级枚举key不能为空")
    @Positive(message = "报警等级枚举key必须为正值")
    private Integer warnLevel;
    private String alias;
}
