package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 11:13
 **/
@Data
@Builder
public class ReservoirProjectStatisticsResult {
    private Integer reservoirCount;
    private Integer typeOneCount;
    private Integer typeTwoCount;
    private Integer typeThreeCount;
    private Integer typeFourCount;
    private Integer typeFiveCount;


    private List<AreaStatItem> areaStatisticsList;

    @Data
    @Builder
    public static class AreaStatItem {
        private String areaCode;
        private String areaName;
        private Integer reservoirCount;
        private Integer typeOneCount;
        private Integer typeTwoCount;
        private Integer typeThreeCount;
        private Integer typeFourCount;
        private Integer typeFiveCount;
    }
}
