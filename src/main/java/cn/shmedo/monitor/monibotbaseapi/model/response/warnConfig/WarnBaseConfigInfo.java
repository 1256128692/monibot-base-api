package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-15 15:49
 */
@Data
public class WarnBaseConfigInfo {
    private Integer warnTag;
    private Integer warnLevelType;
    private Integer warnLevelStyle;
}
