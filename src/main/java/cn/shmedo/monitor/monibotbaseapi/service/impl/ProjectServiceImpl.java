package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private TbTagRelationMapper tbTagRelationMapper;

    private TbPropertyMapper tbPropertyMapper;
    private TbProjectPropertyMapper tbProjectPropertyMapper;
    @Autowired
    public ProjectServiceImpl(TbProjectInfoMapper tbProjectInfoMapper, TbTagMapper tbTagMapper, TbTagRelationMapper tbTagRelationMapper, TbPropertyMapper tbPropertyMapper, TbProjectPropertyMapper tbProjectPropertyMapper) {
        this.tbProjectInfoMapper = tbProjectInfoMapper;
        this.tbTagMapper = tbTagMapper;
        this.tbPropertyMapper = tbPropertyMapper;
        this.tbProjectPropertyMapper = tbProjectPropertyMapper;
        this.tbProjectTypeMapper = tbProjectTypeMapper;
        this.tbTagRelationMapper = tbTagRelationMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProject(AddProjectParam pa, Integer userID) {
        // TODO 处理图片
        String imgPath = "xxxx";
        TbProjectInfo tbProjectInfo = Param2DBEntityUtil.fromAddProjectParam2TbProjectInfo(pa, userID, imgPath);
        tbProjectInfoMapper.insert(tbProjectInfo);

        // 处理属性

        Map<String, TbProperty> propertyMap = pa.getProperties().stream().collect(Collectors.toMap(TbProperty::getName, Function.identity()));
        List<TbProjectProperty> projectPropertyList = pa.getModelValueList().stream().map(
                item -> {
                    TbProjectProperty tbProjectProperty = new TbProjectProperty();
                    tbProjectProperty.setPropertyID(tbProjectInfo.getID());
                    tbProjectProperty.setPropertyID(propertyMap.get(item.getName()).getID());
                    tbProjectProperty.setValue(item.getValue());
                    return tbProjectProperty;
                }
        ).collect(Collectors.toList());

        tbProjectPropertyMapper.insertBatch(projectPropertyList);

        List<Integer> tagID4DBList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(pa.getTagIDList())){
            List<TbTag> tagList = Param2DBEntityUtil.from2TbTagList(pa.getTagList(), pa.getCompanyID(), userID);
            tbTagMapper.insertBatch(tagList);
            tagID4DBList.addAll(tagList.stream().map(TbTag::getID).collect(Collectors.toList()));
        }
        if (ObjectUtil.isNotEmpty(pa.getTagIDList())){
            tagID4DBList.addAll(pa.getTagIDList());
        }
        if (ObjectUtil.isNotEmpty(tagID4DBList)){
            tbTagRelationMapper.insertBatch(tagID4DBList, tbProjectInfo.getID());
        }
    }

    @Override
    public List<TbProjectType> getProjectType() {
        //查询全部项目类型并返回
        List<TbProjectType> list = tbProjectTypeMapper.selectAll();
        return list;
    }

    @Override
    public List<ProjectInfoResult> getProjectInfoList(QueryProjectListParam pa) {
        //添加分页参数
        IPage page = Page.of(pa.getPageSize(), pa.getCurrentPage());

        //构建查询条件
        //基础拓展信息查询未写-todo
        //标签查询未写-todo
        List<TbProjectInfo> tbProjectInfoPage = tbProjectInfoMapper.selectList(new LambdaQueryWrapper<TbProjectInfo>()
                .like(!StringUtils.isNullOrEmpty(pa.getProjectName()), TbProjectInfo::getProjectName, pa.getProjectName())
                .like(!StringUtils.isNullOrEmpty(pa.getDirectManageUnit()), TbProjectInfo::getDirectManageUnit, pa.getDirectManageUnit())
                .like(!StringUtils.isNullOrEmpty(pa.getLocation()), TbProjectInfo::getLocation,pa.getLocation())
                .eq(pa.getCompanyId() != null, TbProjectInfo::getCompanyID, pa.getCompanyId())
                .eq(pa.getProjectType() != null, TbProjectInfo::getProjectType, pa.getProjectType())
                .eq(pa.getEnable() != null, TbProjectInfo::getEnable, pa.getEnable())
                .in(pa.getPlatformTypeList() != null, TbProjectInfo::getPlatformType, pa.getPlatformTypeList())
                .ge(pa.getExpiryDate() != null, TbProjectInfo::getExpiryDate, pa.getExpiryDate())
                .le(pa.getBeginCreateTime() != null, TbProjectInfo::getCreateTime, pa.getBeginCreateTime())
                .ge(pa.getEndCreatTime() != null, TbProjectInfo::getCreateTime, pa.getEndCreatTime()));

        //类型转换，实体表转为要返回的类型
        List<ProjectInfoResult> projectInfoResults = tbProjectInfoPage.stream().map(s -> {
            ProjectInfoResult projectInfoResult = new ProjectInfoResult();
            BeanUtils.copyProperties(s, projectInfoResult);
            return projectInfoResult;
        }).collect(Collectors.toList());
        return projectInfoResults;
    }

    @Override
    public ProjectInfoResult getProjectInfoData(QueryProjectInfoParam pa) {

        int id = pa.getId();
        //根据项目id获取数据库表数据-未判空-todo
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(id);

        //判断项目是否停用
        if (projectInfo.getEnable() || projectInfo.getExpiryDate().before(new Date())) {

        }
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
