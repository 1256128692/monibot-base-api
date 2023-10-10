package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
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
    private List<SimpleProject> nextLevelProjectList;
    private List<SimpleProject> availableProjectList;

    public static QueryNextLevelAndAvailableProjectResult valueOf(List<TbProjectInfo> nextLevelProjectList, List<TbProjectInfo> canUsedProjctList) {
        QueryNextLevelAndAvailableProjectResult result = new QueryNextLevelAndAvailableProjectResult();
        result.setNextLevelProjectList(
                nextLevelProjectList.stream().map(e ->
                        SimpleProject.builder()
                                .id(e.getID()).name(e.getProjectName()).level(e.getLevel())
                                .platformTypeSet((String) e.getPlatformTypeSet())
                                .projectType(e.getProjectType())
                                .projectTypeStr(ProjectTypeCache.projectTypeMap.get(e.getProjectType()).getMainType())
                                .build()
                ).toList()
        );
        result.setAvailableProjectList(
                canUsedProjctList.stream().map(e ->
                        SimpleProject.builder()
                                .id(e.getID()).name(e.getProjectName()).level(e.getLevel())
                                .platformTypeSet((String) e.getPlatformTypeSet())
                                .projectType(e.getProjectType())
                                .projectTypeStr(ProjectTypeCache.projectTypeMap.get(e.getProjectType()).getMainType())
                                .build()
                ).toList()
        );
        return result;
    }
}
