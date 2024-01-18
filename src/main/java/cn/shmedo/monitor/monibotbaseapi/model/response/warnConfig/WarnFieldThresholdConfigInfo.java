package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

import java.util.function.Function;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 11:11
 */
@Data
public class WarnFieldThresholdConfigInfo {
    private Integer fieldID;
    private String fieldName;
    private String fieldToken;
    private Integer configID;
    private String warnName;
    private Integer compareMode;
    private Boolean enable;
    private String value;

    /**
     * set all config data to {@code null} if matchs the {@code judge}.
     *
     * @param judge 判断条件
     */
    public void dealStatusFilter(final Function<String, Boolean> judge) {
        if (judge.apply(this.value)) {
            this.configID = null;
            this.warnName = null;
            this.compareMode = null;
            this.enable = null;
            this.value = null;
        }
    }
}
