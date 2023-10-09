package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:46
 **/
@Data
public class QueryNextLevelAndAvailableProjectResult {
    private List<IDNameLevel> nextLevelProjectList;
    private List<IDNameLevel> availableProjectList;

    public static QueryNextLevelAndAvailableProjectResult valueOf(List<TbProjectInfo> nextLevelProjectList, List<TbProjectInfo> canUsedProjctList) {
        QueryNextLevelAndAvailableProjectResult result = new QueryNextLevelAndAvailableProjectResult();
        result.setNextLevelProjectList(
                nextLevelProjectList.stream().map(e ->
                        IDNameLevel.builder()
                                .id(e.getID()).name(e.getProjectName()).level(e.getLevel())
                                .build()
                ).toList()
        );
        result.setAvailableProjectList(
                canUsedProjctList.stream().map(e ->
                        IDNameLevel.builder()
                                .id(e.getID()).name(e.getProjectName()).level(e.getLevel())
                                .build()
                ).toList()
        );
        return result;
    }
}
