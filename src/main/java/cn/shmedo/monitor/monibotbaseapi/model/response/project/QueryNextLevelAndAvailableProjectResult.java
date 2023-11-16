package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.AuthService;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:46
 **/
@Data
public class QueryNextLevelAndAvailableProjectResult {
    private List<SimpleProject> nextLevelProjectList;
    private List<SimpleProject> availableProjectList;

    public static QueryNextLevelAndAvailableProjectResult valueOf(List<TbProjectInfo> nextLevelProjectList, List<TbProjectInfo> canUsedProjctList,
                                                                  Map<Integer, List<TbProjectInfo>> nnMap, Map<Integer, List<Integer>> pidServiceIDListMap, Map<Integer, AuthService> serviceMap) {
        QueryNextLevelAndAvailableProjectResult result = new QueryNextLevelAndAvailableProjectResult();
        result.setNextLevelProjectList(
                nextLevelProjectList.stream().map(e ->
                        SimpleProject.builder()
                                .id(e.getID()).name(e.getProjectName()).level(e.getLevel())
                                .projectType(e.getProjectType())
                                .projectTypeStr(ProjectTypeCache.projectTypeMap.get(e.getProjectType()).getTypeName())
                                .nnLevelProjectList(nnMap.getOrDefault(e.getID(), List.of()).stream()
                                        .map(nn -> SimpleProject.builder()
                                                .id(nn.getID()).name(nn.getProjectName()).level(nn.getLevel())
                                                .projectType(nn.getProjectType())
                                                .projectTypeStr(ProjectTypeCache.projectTypeMap.get(nn.getProjectType()).getTypeName())
                                                .build()
                                        ).toList()
                                )
                                .build()
                ).toList()
        );
        result.getNextLevelProjectList().forEach(
                e -> {
                    List<Integer> serviceIDList = pidServiceIDListMap.getOrDefault(e.getId(), List.of());
                    e.setServiceIDList(serviceIDList);
                    e.setServiceList(serviceIDList.stream().map(serviceMap::get).toList());
                    if (ObjectUtil.isNotEmpty(e.getNnLevelProjectList())) {
                        e.getNnLevelProjectList().forEach(
                                nn -> {
                                    List<Integer> nnServiceIDList = pidServiceIDListMap.getOrDefault(nn.getId(), List.of());
                                    nn.setServiceIDList(nnServiceIDList);
                                    nn.setServiceList(nnServiceIDList.stream().map(serviceMap::get).toList());
                                }
                        );
                    }
                }
        );
        result.setAvailableProjectList(
                canUsedProjctList.stream().map(e ->
                        SimpleProject.builder()
                                .id(e.getID()).name(e.getProjectName()).level(e.getLevel())
                                .projectType(e.getProjectType())
                                .projectTypeStr(ProjectTypeCache.projectTypeMap.get(e.getProjectType()).getTypeName())
                                .build()
                ).toList()
        );
        result.getAvailableProjectList().forEach(
                e -> {
                    List<Integer> serviceIDList = pidServiceIDListMap.getOrDefault(e.getId(), List.of());
                    e.setServiceIDList(serviceIDList);
                    e.setServiceList(serviceIDList.stream().map(serviceMap::get).toList());
                });
        return result;
    }
}
