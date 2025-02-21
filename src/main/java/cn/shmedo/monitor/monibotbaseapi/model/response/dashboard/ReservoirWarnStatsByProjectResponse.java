package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.WarnPointStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Chengfs on 2024/1/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservoirWarnStatsByProjectResponse {

    private Map<String, Object> dict;
    private Item overview;
    private List<Project> project;

    public record Item(Integer level1, Integer level2, Integer level3, Integer level4, Integer offline) {

        public static Item from(Collection<WarnPointStats> list) {
            return new Item(list.stream().mapToInt(WarnPointStats::getLevel1).sum(),
                    list.stream().mapToInt(WarnPointStats::getLevel2).sum(),
                    list.stream().mapToInt(WarnPointStats::getLevel3).sum(),
                    list.stream().mapToInt(WarnPointStats::getLevel4).sum(),
                    list.stream().mapToInt(WarnPointStats::getOffline).sum());
        }

        public static Item empty() {
            return new Item(0, 0, 0, 0, 0);
        }
    }

    public record Project(Integer id, String projectName, Item detail) {

        public Integer warnCount() {
            return detail.level1 + detail.level2 + detail.level3 + detail.level4 + detail.offline;
        }
    }
}