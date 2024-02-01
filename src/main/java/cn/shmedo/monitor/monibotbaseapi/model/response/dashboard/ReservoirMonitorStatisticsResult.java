package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 15:18
 **/
@Data
@Builder
public class ReservoirMonitorStatisticsResult {
    private Integer monitorPointCount;

    private List<TypeStatItem> monitorTypeStatisticsList;

    @Data
    @Builder
    public static class TypeStatItem {
        private Integer monitorType;
        private String monitorTypeName;
        private Integer count;
    }

}
