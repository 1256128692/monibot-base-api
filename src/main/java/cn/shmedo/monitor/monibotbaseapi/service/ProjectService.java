package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
    Page<TbProjectInfo> getProjectInfoList(QueryProjectListParam pa);
    /**
     * 查询项目详情
     * @param pa
     * @return
     */
    ProjectInfoResult getProjectInfoData(QueryProjectInfoParam pa);
    void addProject(AddProjectParam pa, Integer userID);

    /**
     * 查询项目类型
     * @return
     */
    List<TbProjectType> getProjectType();
}
