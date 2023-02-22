package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
public interface ProjectService {
    ProjectInfoResult getProjectInfoData(int Id);
    void addProject(AddProjectParam pa, Integer userID);
}
