package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mysql.cj.util.StringUtils;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
@Service
public class ProjectServiceImpl extends ServiceImpl<TbProjectInfoMapper, TbProjectInfo> implements ProjectService {
    private TbProjectInfoMapper tbProjectInfoMapper;
    private TbTagMapper tbTagMapper;
    private TbProjectTypeMapper tbProjectTypeMapper;

    @Autowired
    public ProjectServiceImpl(TbProjectInfoMapper tbProjectInfoMapper, TbTagMapper tbTagMapper,TbProjectTypeMapper tbProjectTypeMapper) {
        this.tbProjectInfoMapper = tbProjectInfoMapper;
        this.tbTagMapper = tbTagMapper;
        this.tbProjectTypeMapper = tbProjectTypeMapper;
    }

    @Override
    public void addProject(AddProjectParam pa, Integer userID) {

    }

    @Override
    public List<TbProjectType> getProjectType() {
        List<TbProjectType> list = tbProjectTypeMapper.selectList(null);
        return list;
    }

    @Override
    public Page<TbProjectInfo> getProjectInfoList(QueryProjectListParam pa) {
        //添加分页参数
        Page<TbProjectInfo> page = Page.of(pa.getPageSize(), pa.getCurrentPage());

        //构建查询条件-部分判空条件未写-todo
        //基础拓展信息查询未写-todo
        LambdaQueryWrapper<TbProjectInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNullOrEmpty(pa.getProjectName()), TbProjectInfo::getProjectName, pa.getProjectName())
                .like(StringUtils.isNullOrEmpty(pa.getDirectManageUnit()), TbProjectInfo::getDirectManageUnit, pa.getDirectManageUnit())
                .eq(TbProjectInfo::getCompanyID, pa.getCompanyId())
                .eq(TbProjectInfo::getProjectType, pa.getProjectType())
                .eq(pa.getEnable() == null,TbProjectInfo::getEnable, pa.getEnable())
                .in(TbProjectInfo::getPlatformType, pa.getPlatformTypeList())
                .ge(TbProjectInfo::getExpiryDate, pa.getExpiryDate())
                .le(TbProjectInfo::getCreateTime, pa.getBeginCreateTime())
                .ge(TbProjectInfo::getCreateTime, pa.getEndCreatTime());
        Page<TbProjectInfo> tbProjectInfoPage = tbProjectInfoMapper.selectPage(page, lambdaQueryWrapper);

        return tbProjectInfoPage;
    }

    @Override
    public ProjectInfoResult getProjectInfoData(QueryProjectInfoParam pa) {

        int id = pa.getId();
        //根据项目id获取数据库表数据-未判空-todo
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(id);

        //类型转换-将projectInfo转为ProjectInfoResult
        ProjectInfoResult projectInfoResult = ProjectInfoResult.valueOf(projectInfo);

        //构建查询条件查询标签列表
        LambdaQueryWrapper<TbTag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(TbTag::getCompanyID, projectInfo.getCompanyID());
        List<TbTag> tbTags = tbTagMapper.selectList(tagLambdaQueryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbTags)) {

        }

        //给标签列表赋值
        projectInfoResult.setTagInfo(tbTags);

        //给公司信息赋值-todo
        projectInfoResult.setCompany(null);

        //给拓展信息赋值-todo
        projectInfoResult.setPropertyList(null);

        return projectInfoResult;
    }
}
