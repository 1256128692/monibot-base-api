package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import jakarta.servlet.ServletRequest;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
public interface ProjectService {

    /**
     * 查询项目列表信息
     * @param pa
     * @return
     */
    PageUtil.PageResult<ProjectInfoResult> getProjectInfoList(ServletRequest request,QueryProjectListParam pa);
    /**
     * 查询项目详情
     * @param pa
     * @return
     */
    ResultWrapper getProjectInfoData(ServletRequest request, QueryProjectInfoParam pa);

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

    Company getCompany(ServletRequest request, Integer id);

    void transferProject(TransferProjectParam param, CurrentSubject currentSubject);

    void raiseExpiryDate(RaiseExpiryDateParam param, Integer userID);

    /**
     * 批量删除
     * @param IDS
     * @return
     */
    ResultWrapper deleteProjectList(ProjectIDListParam IDS);

    void updateProject(UpdateProjectParameter pa);

    void updateProjectImage(UpdateProjectImageParam pa, Integer userID);

    /**
     * 查询项目列表
     * @param request   请求 {@link ServletRequest}
     * @param pa    参数 {@link QueryProjectListRequest}
     * @return  {@link PageUtil.PageResult<ProjectInfo>}
     */
    PageUtil.PageResult<ProjectInfo> queryProjectList(ServletRequest request, QueryProjectListRequest pa);

    ProjectInfo queryProjectInfo(ServletRequest request, QueryProjectInfoParam pa);
}
