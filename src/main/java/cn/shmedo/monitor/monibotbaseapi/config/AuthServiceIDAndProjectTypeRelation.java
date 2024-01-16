package cn.shmedo.monitor.monibotbaseapi.config;

import java.util.List;
import java.util.Map;

/**
 * 项目所属服务ID和项目类型的关联
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-09 15:10
 **/
public class AuthServiceIDAndProjectTypeRelation {
    private static Map<Integer, List<String>> relation;
    private static List<Integer> allTypeServiceList;
    static {
        relation = Map.of(
                // 灌区
                15, List.of("水库", "堤防", "河道", "灌区", "流域"),
                // 流域
                16, List.of("灌区", "水库", "河道", "渠首", "机井", "渠系", "水闸", "泵站", "田间"),
                // 水库
                17, List.of("水库")
        );
        // MDNET3.0
        allTypeServiceList = List.of(21);
    }

    public static boolean isLegalServiceID(Integer serviceID) {
        return relation.containsKey(serviceID);
    }

    public static boolean isAllTypeService(Integer serviceID) {
        return allTypeServiceList.contains(serviceID);
    }
    public static boolean isLegalProjectType(Integer serviceID, String projectType) {
        return relation.get(serviceID).contains(projectType);
    }
}
