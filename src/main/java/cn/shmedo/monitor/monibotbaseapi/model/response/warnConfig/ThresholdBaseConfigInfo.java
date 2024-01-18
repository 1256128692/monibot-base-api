package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 17:08
 */
@Data
public class ThresholdBaseConfigInfo {
    private Integer triggerType;
    private Integer triggerTimes;
    private Integer warnTag;
    private Integer warnLevelType;
    private Integer warnLevelStyle;
    private List<ThresholdBaseConfigFieldInfo> fieldList;
}
