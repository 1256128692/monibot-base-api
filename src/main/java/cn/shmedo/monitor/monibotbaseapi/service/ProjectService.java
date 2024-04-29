package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.ProjectWithNext;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryNextLevelAndAvailableProjectResult;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryProjectBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryWtProjectResponse;
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
    Integer addProject(AddProjectParam pa, Integer userID);

    /**
     * 查询项目类型
     * @return
     */
    List<TbProjectType> getProjectType(Integer serviceID);

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
     *
     * @param pa     参数 {@link QueryProjectListRequest}
     * @return {@link PageUtil.Page <ProjectInfo>}
     */
    PageUtil.Page<ProjectInfo> queryProjectList(QueryProjectListRequest pa);

    ProjectInfo queryProjectInfo(QueryProjectInfoParam pa);

    List<ProjectBaseInfo> queryProjectListByProjectName(QueryProjectListParam pa);

    String setProjectImg(SetProjectImgParam pa, Integer userID);

    String queryProjectImg(QueryProjectImgParam pa);

    QueryWtProjectResponse queryWtProjectSimpleList(QueryWtProjectParam pa);

    List<QueryProjectBaseInfoResponse> queryProjectBaseInfoList(QueryProjectBaseInfoListParam pa);

    Boolean checkProjectName(CheckProjectNameParam pa);

    void setProjectRelation(SetProjectRelationParam pa, Integer subjectID);

    QueryNextLevelAndAvailableProjectResult queryNextLevelProjectAndCanUsed(QueryNextLevelAndAvailableProjectParam pa);

    void removeProjectRelation(RemoveProjectRelationParam pa, Integer subjectID);

    List<ProjectWithNext> queryProjectWithNextList(QueryProjectWithNextListParam pa);

    Object queryProjectWithRaiseCrops(QueryProjectWithRaiseCropsParam pa);
}
