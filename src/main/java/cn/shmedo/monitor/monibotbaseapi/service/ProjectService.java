package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;
/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
public interface ProjectService {

    /**
     * 新增项目
     * @param pa
     * @param userID
     */
    void addProject(AddProjectParam pa, Integer userID);

    /**
     * 查询项目类型
     * @return
     */
    List<TbProjectType> getProjectType();

    void transferProject(TransferProjectParam param, CurrentSubject currentSubject);

    void raiseExpiryDate(RaiseExpiryDateParam param, Integer userID);

    /**
     * 批量删除
     * @param IDS
     * @return
     */
    void deleteProjectList(ProjectIDListParam IDS);

    void updateProject(UpdateProjectParameter pa, Integer userID);

    void updateProjectImage(UpdateProjectImageParam pa, Integer userID);

    /**
     * 查询项目列表
     * @param pa    参数 {@link QueryProjectListRequest}
     * @return  {@link PageUtil.Page <ProjectInfo>}
     */
    PageUtil.Page<ProjectInfo> queryProjectList(QueryProjectListRequest pa);

    ProjectInfo queryProjectInfo(QueryProjectInfoParam pa);

    List<ProjectBaseInfo> queryProjectListByProjectName(QueryProjectListParam pa);

    /**
     * 获取用户的项目ID
     * 高频访问方法，使用Spring AOP做特殊缓存
     *
     * @param companyID 获取用户在该公司中的项目，如果为null,则获取用户在所有具有权限的公司中的项目
     * @param accessToken    访问id
     * @return
     */
    List<Integer> getUserProjectIDs(Integer companyID, String accessToken);
}
