package cn.shmedo.monitor.monibotbaseapi.model.response.checktask;

import cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak.CheckTaskSimple;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckTaskPageResponse {

    private Collection<CheckTaskSimple> currentPageData;
    private long totalPage;
    private long totalCount;
    private Statis statis;

    @Data
    public static class Statis {
        private Long totalCount;
        private Long notStartCount;
        private Long endedCount;
        private Long ongoingCount;
        private Long expiredCount;

        public Statis() {
            totalCount = 0L;
            notStartCount = 0L;
            endedCount = 0L;
            ongoingCount = 0L;
            expiredCount = 0L;
        }
    }
}