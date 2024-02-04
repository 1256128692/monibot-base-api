package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 17:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThresholdBaseConfigInfo extends WarnBaseConfigInfo {
    private Integer triggerType;
    private Integer triggerTimes;
    private List<ThresholdBaseConfigFieldInfo> fieldList;
}
