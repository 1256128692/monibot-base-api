package cn.shmedo.monitor.monibotbaseapi.model.response.workorder;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-2023-04-19 14:31
 */
@Data
@Builder
public class WtWorkOrderStatisticsInfo {
    private Integer todoCount;
    private Integer processingCount;
    private Integer completedCount;
}
