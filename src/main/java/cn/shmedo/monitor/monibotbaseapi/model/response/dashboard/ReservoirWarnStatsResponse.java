package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.WarnPointStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * @author Chengfs on 2024/1/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservoirWarnStatsResponse {

    private Dict dict;
    private Item overview;
    private List<MonitorType> monitorType;

    public record Dict(String level1, String level2, String level3, String level4, String offline) {
    }

    public record Item(Integer level1, Integer level2, Integer level3, Integer level4, Integer offline) {

        public static Item from(Collection<WarnPointStats> list) {
            return new Item(list.stream().mapToInt(WarnPointStats::getLevel1).sum(),
                    list.stream().mapToInt(WarnPointStats::getLevel2).sum(),
                    list.stream().mapToInt(WarnPointStats::getLevel3).sum(),
                    list.stream().mapToInt(WarnPointStats::getLevel4).sum(),
                    list.stream().mapToInt(WarnPointStats::getOffline).sum());
        }
    }

    public record MonitorType(Integer monitorType, String typeName, Item detail) {
    }
}