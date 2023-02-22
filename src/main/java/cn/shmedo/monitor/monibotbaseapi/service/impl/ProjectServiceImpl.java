package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
@Service
public class ProjectServiceImpl implements ProjectService {
    private TbProjectInfoMapper tbProjectInfoMapper;
    @Autowired
    public ProjectServiceImpl(TbProjectInfoMapper tbProjectInfoMapper) {
        this.tbProjectInfoMapper = tbProjectInfoMapper;
    }

    @Override
    public void addProject(AddProjectParam pa, Integer userID) {

    }

    @Override
    public ProjectInfoResult getProjectInfoData(int Id) {
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectByPrimaryKey(Id);
        System.out.println(projectInfo.toString());
        return null;
    }
}
