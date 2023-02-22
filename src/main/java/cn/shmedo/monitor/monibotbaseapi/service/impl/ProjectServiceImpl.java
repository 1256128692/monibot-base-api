package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
@Service
public class ProjectServiceImpl implements ProjectService {
    private TbProjectInfoMapper tbProjectInfoMapper;
    private TbTagMapper tbTagMapper;
    @Autowired
    public ProjectServiceImpl(TbProjectInfoMapper tbProjectInfoMapper,TbTagMapper tbTagMapper) {
        this.tbProjectInfoMapper = tbProjectInfoMapper;
        this.tbTagMapper = tbTagMapper;
    }

    @Override
    public void addProject(AddProjectParam pa, Integer userID) {

    }

    @Override
    public ProjectInfoResult getProjectInfoData(int id) {

        //根据项目id获取数据库表数据
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(id);
        if (StringUtils.isEmpty(projectInfo)){
            //未查到数据返回提示空-todo
            return null;
        }
        //类型转换-将projectInfo转为ProjectInfoResult
        ProjectInfoResult projectInfoResult = ProjectInfoResult.valueOf(projectInfo);

        //构建查询条件查询标签列表
        LambdaQueryWrapper<TbTag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(TbTag::getID,id).eq(TbTag::getCompanyID,projectInfo.getCompanyID());
        List<TbTag> tbTags = tbTagMapper.selectList(tagLambdaQueryWrapper);

        //给标签列表赋值
        projectInfoResult.setTagInfo(tbTags);

        //给公司信息赋值-todo
        projectInfoResult.setCompany(null);

        //给拓展信息赋值-todo
        projectInfoResult.setPropertyList(null);

        return projectInfoResult;
    }
}
