package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;


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
    PageUtil.PageResult<ProjectInfoResult> getProjectInfoList(QueryProjectListParam pa);
    /**
     * 查询项目详情
     * @param pa
     * @return
     */
    ResultWrapper getProjectInfoData(QueryProjectInfoParam pa);

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

    ResultWrapper getCompany(Integer id);

    void transferProject(TransferProjectParam param, Integer userID);

    void raiseExpiryDate(RaiseExpiryDateParam param, Integer userID);
}
