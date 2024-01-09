package cn.shmedo.monitor.monibotbaseapi.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 平台和项目类型的关联
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-09 15:10
 **/
public class PlatformAndProjectTypeRelation {
    private static Map<String, List<String>> relation;

    static {
        relation = Map.of(
                "流域平台", List.of("水库", "堤防", "河道", "灌区", "流域"),
                "灌区平台", List.of("灌区", "水库", "河道", "渠首", "机井", "渠系", "水闸", "泵站"),
                "水库平台", List.of("水库")
        );
    }

    public static boolean isLegalPlatform(String platform) {
        return relation.containsKey(platform);
    }

    public static boolean isLegalProjectType(String platform, String projectType) {
        return relation.get(platform).contains(projectType);
    }
}
