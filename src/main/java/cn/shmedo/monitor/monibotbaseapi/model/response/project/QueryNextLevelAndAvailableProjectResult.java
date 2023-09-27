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
    private List<IDAndName> nextLevelProjectList;
    private List<IDAndName> availableProjectList;

    public static QueryNextLevelAndAvailableProjectResult valueOf(List<TbProjectInfo> nextLevelProjectList, List<TbProjectInfo> canUsedProjctList) {
        QueryNextLevelAndAvailableProjectResult result = new QueryNextLevelAndAvailableProjectResult();
        result.setNextLevelProjectList(
                nextLevelProjectList.stream().map(e ->
                        IDAndName.builder()
                                .id(e.getID()).name(e.getProjectName())
                                .build()
                ).toList()
        );
        result.setAvailableProjectList(
                canUsedProjctList.stream().map(e ->
                        IDAndName.builder()
                                .id(e.getID()).name(e.getProjectName())
                                .build()
                ).toList()
        );
        return result;
    }
}
