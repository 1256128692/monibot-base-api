package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorWithThresholdConfigCountInfo extends MonitorItemV1 {
    private Integer monitorClass;
    private Integer configCount;
}
