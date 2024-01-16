package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:52
 */
@Data
public class MonitorWithThresholdConfigCountInfo {
    private Integer id;
    private String name;
    private String alias;
    private Boolean enable;
    private Integer monitorType;
    private Integer monitorClass;
    private Integer configCount;
}
