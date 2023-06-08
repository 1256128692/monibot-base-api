package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-06-08 16:52
 */
@Data
@Builder
public class QueryMonitorPointRuleWarnStatusInfo {
    private String upperName;
    private Double upperLimit;
}
