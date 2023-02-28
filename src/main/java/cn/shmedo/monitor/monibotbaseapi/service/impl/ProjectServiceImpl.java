package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.CompanyThird;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    private static final String TOKEN_HEADER = "Authorization";

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
                    tbProjectProperty.setProjectID(tbProjectInfo.getID());
                    tbProjectProperty.setPropertyID(propertyMap.get(item.getName()).getID());
                    tbProjectProperty.setValue(item.getValue());
                    return tbProjectProperty;
                }
        ).collect(Collectors.toList());

        tbProjectPropertyMapper.insertBatch(projectPropertyList);

        List<Integer> tagID4DBList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(pa.getTagList())){
            List<TbTag> tagList = Param2DBEntityUtil.from2TbTagList(pa.getTagList(), pa.getCompanyID(), userID);
            tbTagMapper.insertBatch(tagList);
            tagID4DBList.addAll(tagList.stream().map(TbTag::getID).toList());
        }
        if (ObjectUtil.isNotEmpty(pa.getTagIDList())) {
            tagID4DBList.addAll(pa.getTagIDList());
        }
        if (ObjectUtil.isNotEmpty(tagID4DBList)) {
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
    public void transferProject(TransferProjectParam param, Integer userID) {
        tbProjectInfoMapper.updateCompanyID(param.getProjectID(), param.getCompanyID(), userID, new Date());
    }

    @Override
    public void raiseExpiryDate(RaiseExpiryDateParam param, Integer userID) {
        tbProjectInfoMapper.updateExpiryDate(param.getProjectID(), param.getNewRetireDate(), userID, new Date());
    }

    @Override
    public PageUtil.PageResult<ProjectInfoResult> getProjectInfoList(ServletRequest request,QueryProjectListParam pa) {
        //查询列表信息
        List<TbProjectInfo> projectInfoList = tbProjectInfoMapper.getProjectInfoList(pa);

        //类型转换，实体表转为要返回的类型
        List<ProjectInfoResult> projectInfoResults = projectInfoList.stream().map(s -> {
            ProjectInfoResult projectInfoResult = new ProjectInfoResult();
            BeanUtils.copyProperties(s, projectInfoResult);
            //根据项目id获取标签信息列表-todo

            //根据项目id获取客户企业信息
            Company company = getCompany(request, s.getCompanyID());
            projectInfoResult.setCompany(company);

            //根据项目id获取拓展属性信息列表
            projectInfoResult.setPropertyList(tbProjectPropertyMapper.getPropertyList(s.getID()));
            return projectInfoResult;
        }).collect(Collectors.toList());

        return PageUtil.page(projectInfoResults,pa.getPageSize(),pa.getCurrentPage());
    }

    @Override
    public ResultWrapper getProjectInfoData(ServletRequest request,QueryProjectInfoParam pa) {

        int id = pa.getId();
        //根据项目id获取数据库表数据-未判空-todo
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(id);

        //类型转换-将projectInfo转为ProjectInfoResult
        ProjectInfoResult projectInfoResult = ProjectInfoResult.valueOf(projectInfo);

        //构建查询条件查询标签列表
        LambdaQueryWrapper<TbTag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(TbTag::getCompanyID, projectInfo.getCompanyID());
        List<TbTag> tbTags = tbTagMapper.selectList(tagLambdaQueryWrapper);

        //给标签列表赋值
        projectInfoResult.setTagInfo(tbTags);

        //给公司信息赋值-todo
        Company data = getCompany(request,projectInfo.getCompanyID());
        projectInfoResult.setCompany(data);

        //给拓展信息赋值-todo
        projectInfoResult.setPropertyList(tbProjectPropertyMapper.getPropertyList(pa.getId()));

        return ResultWrapper.success(projectInfoResult);
    }

    @Override
    public Company getCompany(ServletRequest request,Integer id){
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader(TOKEN_HEADER);
        CompanyThird companyThird = new CompanyThird();
        companyThird.setCompanyID(id);
        UserService userService = ThirdHttpService.getInstance(UserService.class, ThirdHttpService.Auth);
        ResultWrapper<Company> companyInfo = userService.getCompanyInfo(token,companyThird);
        Company data = companyInfo.getData();
        return data;
    }
}
