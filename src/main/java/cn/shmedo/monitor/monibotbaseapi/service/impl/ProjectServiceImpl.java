package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.ErrorConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.TagDto;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.QueryFileInfoRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.PermissionService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    private final FileConfig fileConfig;

    private final RedisService redisService;

    @Autowired
    public ProjectServiceImpl(TbProjectInfoMapper tbProjectInfoMapper,
                              TbTagMapper tbTagMapper,
                              TbTagRelationMapper tbTagRelationMapper,
                              TbPropertyMapper tbPropertyMapper,
                              TbProjectTypeMapper tbProjectTypMapper,
                              TbProjectPropertyMapper tbProjectPropertyMapper,
                              FileConfig fileConfig,
                              RedisService redisService) {
        this.tbProjectInfoMapper = tbProjectInfoMapper;
        this.tbTagMapper = tbTagMapper;
        this.tbPropertyMapper = tbPropertyMapper;
        this.tbProjectPropertyMapper = tbProjectPropertyMapper;
        this.tbProjectTypeMapper = tbProjectTypMapper;
        this.tbTagRelationMapper = tbTagRelationMapper;
        this.fileConfig = fileConfig;
        this.redisService = redisService;
    }

    private static final String TOKEN_HEADER = "Authorization";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProject(AddProjectParam pa, Integer userID) {
        String imgPath = "";
        if (StringUtils.isNotBlank(pa.getImageContent()) && StringUtils.isNotBlank(pa.getImageSuffix())) {
            imgPath = handlerimagePath(pa.getImageContent(), pa.getImageSuffix(), userID, null);
        }
        TbProjectInfo tbProjectInfo = Param2DBEntityUtil.fromAddProjectParam2TbProjectInfo(pa, userID, imgPath);
        tbProjectInfoMapper.insert(tbProjectInfo);

        // 处理属性
        Map<Integer, String> PropertyIDAndValueMap =
                ObjectUtil.isEmpty( pa.getModelValueList())?
                        new HashMap<>():
                        pa.getModelValueList().stream().collect(Collectors.toMap(PropertyIdAndValue::getID, PropertyIdAndValue::getValue));
        List<TbProjectProperty> projectPropertyList = pa.getProperties().stream().map(
                item -> {
                    TbProjectProperty tbProjectProperty = new TbProjectProperty();
                    tbProjectProperty.setProjectID(tbProjectInfo.getID());
                    tbProjectProperty.setPropertyID(item.getID());
                    tbProjectProperty.setValue(PropertyIDAndValueMap.get(item.getID()));
                    return tbProjectProperty;
                }
        ).collect(Collectors.toList());
        tbProjectPropertyMapper.insertBatch(projectPropertyList);



        List<Integer> tagID4DBList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(pa.getTagList())) {
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

        // 新增项目权限
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV3> resourceItemV3s = new LinkedList<ResourceItemV3>();
        // 插入成功,并且添加权限
        if (tbProjectInfo.getID() != null) {
            resourceItemV3s.add(new ResourceItemV3(DefaultConstant.AUTH_RESOURSE, tbProjectInfo.getID().toString(), tbProjectInfo.getProjectName()));
        }
        if (!CollectionUtil.isEmpty(resourceItemV3s)) {
            ResultWrapper<Object> info = instance.addMdmbaseResource(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new AddResourcesParameter(pa.getCompanyID(), resourceItemV3s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
    }

    private String handlerimagePath(String imageContent, String imageSuffix, Integer userID, String fileName) {
        MdInfoService instance = ThirdHttpService.getInstance(MdInfoService.class, ThirdHttpService.MdInfo);
        AddFileUploadRequest pojo = new AddFileUploadRequest();
        if (StrUtil.isBlank(fileName)) {
            pojo.setFileName(UUID.randomUUID().toString());
        } else {
            pojo.setFileName(fileName);
        }
        pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
        pojo.setFileContent(imageContent);
        pojo.setFileType(imageSuffix);
        pojo.setUserID(userID);
        ResultWrapper<FilePathResponse> info = instance.AddFileUpload(pojo);
        if (!info.apiSuccess()) {
            return ErrorConstant.IMAGE_INSERT_FAIL;
        } else {
            return info.getData().getPath();
        }
    }

    @Override
    public List<TbProjectType> getProjectType() {
        //查询全部项目类型并返回
        List<TbProjectType> list = tbProjectTypeMapper.selectAll();
        return list;
    }

    @Override
    public void transferProject(TransferProjectParam param, CurrentSubject currentSubject) {
        tbProjectInfoMapper.updateCompanyID(param.getProjectID(), param.getCompanyID(), currentSubject.getSubjectID(), new Date());

        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV2> resourceItemV2s = new LinkedList<ResourceItemV2>();
        // 转移成功后,并且修改项目资源的公司ID
        resourceItemV2s.add(new ResourceItemV2(DefaultConstant.AUTH_RESOURSE, param.getProjectID().toString()));
        if (!CollectionUtil.isEmpty(resourceItemV2s)) {
            ResultWrapper<Object> info = instance.transferMdmbaseResource(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new TransferResourceParameter(param.getRowCompanyID(),
                            param.getCompanyID(), resourceItemV2s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
    }

    @Override
    public void raiseExpiryDate(RaiseExpiryDateParam param, Integer userID) {
        tbProjectInfoMapper.updateExpiryDate(param.getProjectID(), param.getNewRetireDate(), userID, new Date());
    }


    @Override
    public PageUtil.PageResult<ProjectInfoResult> getProjectInfoList(ServletRequest request, QueryProjectListParam pa) {
        //将有效期转化为字符串
        if (pa.getExpiryDate() != null) {
            String s1 = pa.getExpiryDate().toString();
            pa.setExpiryDateStr(s1);
        }
        //对基础属性进行分类，json和普通字符串，分别进行查询后取交集
        List<PropertyQueryEntity> propertyEntity = pa.getPropertyEntity();
        if (propertyEntity != null) {
            List<Integer> idList = getIDList(propertyEntity);
            if (idList != null && idList.size() > 0) {
                pa.setIdList(idList);
            } else if (idList == null || idList.size() == 0) {
                List<Integer> list = new ArrayList<>();
                list.add(0);
                pa.setIdList(list);
            }
        }

        //查询列表信息
        List<TbProjectInfo> projectInfoList = tbProjectInfoMapper.getProjectInfoList(pa);

        //类型转换，实体表转为要返回的类型
        List<ProjectInfoResult> projectInfoResults = projectInfoList.stream().map(s -> {
            ProjectInfoResult projectInfoResult = new ProjectInfoResult();
            BeanUtils.copyProperties(s, projectInfoResult);
            //根据项目id获取标签信息列表,构建查询条件查询标签列表
            LambdaQueryWrapper<TbTagRelation> tbTagRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tbTagRelationLambdaQueryWrapper.eq(TbTagRelation::getProjectID, s.getID());
            List<Integer> collect = tbTagRelationMapper.selectList(tbTagRelationLambdaQueryWrapper).stream().map(t -> t.getTagID()).collect(Collectors.toList());
            List<TbTag> tbTags = null;
            if (collect.size() > 0) {
                tbTags = tbTagMapper.queryTagList(collect);
            }
            //给项目类型名称赋值
            TbProjectType tbProjectType = tbProjectTypeMapper.selectByPrimaryKey(s.getProjectType());
            projectInfoResult.setProjectTypeName(tbProjectType.getTypeName());
            projectInfoResult.setProjectTypeMainName(tbProjectType.getMainType());

            //给标签列表赋值
            projectInfoResult.setTagInfo(tbTags);
            //根据项目id获取客户企业信息
            Company company = getCompany(request, s.getCompanyID());
            projectInfoResult.setCompany(company);

            //根据项目id获取拓展属性信息列表
            projectInfoResult.setPropertyList(tbProjectPropertyMapper.getPropertyList(s.getID()));
            return projectInfoResult;
        }).collect(Collectors.toList());

        return PageUtil.page(projectInfoResults, pa.getPageSize(), pa.getCurrentPage());
    }

    @Override
    public ResultWrapper getProjectInfoData(ServletRequest request, QueryProjectInfoParam pa) {

        int id = pa.getID();
        //根据项目id获取数据库表数据
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(id);
        if (projectInfo == null) {
            return ResultWrapper.withCode(ResultCode.RESOURCE_NOT_FOUND);
        }
        handlerimagePathToRealPath(projectInfo);

        //类型转换-将projectInfo转为ProjectInfoResult
        ProjectInfoResult projectInfoResult = ProjectInfoResult.valueOf(projectInfo);

        //给项目类型赋值
        TbProjectType tbProjectType = tbProjectTypeMapper.selectById(projectInfo.getProjectType());
        if (tbProjectType == null) {
            return ResultWrapper.withCode(ResultCode.RESOURCE_NOT_FOUND);
        }
        projectInfoResult.setProjectTypeName(tbProjectType.getTypeName());
        projectInfoResult.setProjectTypeMainName(tbProjectType.getMainType());

        //构建查询条件查询标签列表
        LambdaQueryWrapper<TbTagRelation> tbTagRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tbTagRelationLambdaQueryWrapper.eq(TbTagRelation::getProjectID, pa.getID());
        List<Integer> collect = tbTagRelationMapper.selectList(tbTagRelationLambdaQueryWrapper).stream().map(t -> t.getTagID()).collect(Collectors.toList());
        List<TbTag> tbTags = null;
        if (collect.size() > 0) {
            tbTags = tbTagMapper.queryTagList(collect);
        }

        //给标签列表赋值
        projectInfoResult.setTagInfo(tbTags);

        //给公司信息赋值
        Company data = getCompany(request, projectInfo.getCompanyID());
        projectInfoResult.setCompany(data);

        //给拓展信息赋值
        projectInfoResult.setPropertyList(tbProjectPropertyMapper.getPropertyList(pa.getID()));

        return ResultWrapper.success(projectInfoResult);
    }

    private <T extends TbProjectInfo> void handlerimagePathToRealPath(T projectInfo) {
        if (!StrUtil.isBlank(projectInfo.getImagePath())){
            MdInfoService instance = ThirdHttpService.getInstance(MdInfoService.class, ThirdHttpService.MdInfo);
            QueryFileInfoRequest pojo = new QueryFileInfoRequest();
            pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
            pojo.setFilePath(projectInfo.getImagePath());
            ResultWrapper<FileInfoResponse> info = instance.queryFileInfo(pojo);
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }else{
                projectInfo.setImagePath(info.getData().getAbsolutePath());
            }
        }
    }

    /**
     * 批量删除
     *
     * @param idListParam
     * @return
     */
    @Override
    public ResultWrapper deleteProjectList(ProjectIDListParam idListParam) {
        tbProjectInfoMapper.deleteProjectList(idListParam.getDataIDList());
        // 删除项目权限
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV2> resourceItemV2s = null;
        if (!CollectionUtil.isEmpty(idListParam.getDataIDList())) {
            resourceItemV2s = idListParam.getDataIDList().stream().map(item -> new ResourceItemV2(DefaultConstant.AUTH_RESOURSE, item.toString())).toList();
        }
        if (!CollectionUtil.isEmpty(resourceItemV2s)) {
            ResultWrapper<Object> info = instance.deleteMdmbaseResource(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new DeleteResourcesParameter(idListParam.getCompanyID(), resourceItemV2s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
        return ResultWrapper.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(UpdateProjectParameter pa) {
        CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
        TbProjectInfo projectInfo = pa.buildProject(currentSubject.getSubjectID());
        tbProjectInfoMapper.updateByPrimaryKey(projectInfo);
        if (!CollectionUtil.isEmpty(pa.getPropertyDataList())) {
            List<TbProjectProperty> projectProperties = pa.buildPropertyDataList();
            tbProjectPropertyMapper.updateBatch(pa.getProjectID(), projectProperties);
        }
        //处理标签
        List<Integer> tagID4DBList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(pa.getTagList())) {
            List<TbTag> tagList = Param2DBEntityUtil.from2TbTagList(pa.getTagList(), pa.getCompanyID(), currentSubject.getSubjectID());
            tbTagMapper.insertBatch(tagList);
            tagID4DBList.addAll(tagList.stream().map(TbTag::getID).toList());
        }
        if (ObjectUtil.isNotEmpty(pa.getTagIDList())) {
            tagID4DBList.addAll(pa.getTagIDList());
        }
        if (ObjectUtil.isNotEmpty(tagID4DBList)) {
            tbTagRelationMapper.deleteByProjectID(pa.getProjectID());
            tbTagRelationMapper.insertBatch(tagID4DBList, pa.getProjectID());
        }

        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV3> resourceItemV3s = new LinkedList<ResourceItemV3>();
        // 更新成功后,并且修改项目资源的描述
        if (projectInfo != null) {
            resourceItemV3s.add(new ResourceItemV3(DefaultConstant.AUTH_RESOURSE, projectInfo.getID().toString(), projectInfo.getProjectName()));
        }
        if (!CollectionUtil.isEmpty(resourceItemV3s)) {
            ResultWrapper<Object> info = instance.updateMdmbaseResourceDesc(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new UpdateResourceDescParameter(resourceItemV3s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
    }

    @Override
    public void updateProjectImage(UpdateProjectImageParam pa, Integer userID) {
        String path = handlerimagePath(pa.getImageContent(), pa.getImageSuffix(), userID, pa.getFileName());
        if (StringUtils.isNotBlank(path)) {
            tbProjectInfoMapper.updatePathByID(path, pa.getProjectID());
        }
    }

    @Override
    public Company getCompany(ServletRequest request, Integer id) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader(TOKEN_HEADER);
        CompanyThird companyThird = new CompanyThird();
        companyThird.setCompanyID(id);
        UserService userService = ThirdHttpService.getInstance(UserService.class, ThirdHttpService.Auth);
        ResultWrapper<Company> companyInfo = userService.getCompanyInfo(token, companyThird);
        Company data = companyInfo.getData();
        return data;
    }

    /**
     * 判断是否为json格式
     *
     * @param entity
     * @return
     */
    public boolean isJson(PropertyQueryEntity entity) {
        String value = entity.getValue();
        boolean result = false;
        if (StringUtils.isNotBlank(value)) {
            if (JSONUtil.isTypeJSONArray(value)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取项目ID列表
     *
     * @param propertyEntity
     * @return
     */
    public List<Integer> getIDList(List<PropertyQueryEntity> propertyEntity) {
        List<PropertyQueryEntity> propertystr = new ArrayList<>();
        List<List<PropertyQueryEntity>> propertyJson = new ArrayList<>();
        if (propertyEntity != null && propertyEntity.size() > 0) {
            propertyEntity.forEach(p -> {
                if (p.getValue() != null && !"[]".equals(p.getValue()) && !"".equals(p.getValue())) {
                    if (isJson(p)) {
                        List<String> strings = JSONUtil.parseArray(p.getValue()).toList(String.class);
                        List<PropertyQueryEntity> json = new ArrayList<>();
                        for (String s : strings) {
                            PropertyQueryEntity entity = new PropertyQueryEntity();
                            entity.setName(p.getName());
                            entity.setValue(s);
                            json.add(entity);
                        }
                        propertyJson.add(json);
                    } else {
                        propertystr.add(p);
                    }
                }
            });
        }
        List<List<Integer>> strIDLists = new ArrayList<>();
        List<Integer> str = new ArrayList<>();
        List<Integer> json = new ArrayList<>();
        if (propertystr != null && propertystr.size() > 0) {
            propertystr.forEach(p -> strIDLists.add(tbProjectInfoMapper.getStrIDList(p)));
            int i = 0;
            List<Integer> list = strIDLists.get(0);
            str.add(list.get(0));
            if (strIDLists != null && strIDLists.size() > 1) {
                for (i = 0; i < strIDLists.size(); i++) {
                    str.retainAll(strIDLists.get(i));
                }
            }
        }
        if (propertyJson != null && propertyJson.size() > 0) {
            propertyJson.forEach(p -> strIDLists.add(tbProjectInfoMapper.getJsonIDList(p)));
            int i = 0;
            List<Integer> list = strIDLists.get(0);
            json = strIDLists.get(list.get(0));
            if (strIDLists != null && strIDLists.size() > 1) {
                for (i = 0; i < strIDLists.size(); i++) {
                    json.retainAll(strIDLists.get(i));
                }
            }
        }
        if (str.size() > 0 && json.size() > 0) {
            str.retainAll(json);
            return str;
        } else if (str.size() > 0 && json.size() == 0) {
            return str;
        } else if (str.size() == 0 && json.size() > 0) {
            return json;
        }
        return null;
    }

    @Override
    public PageUtil.PageResult<ProjectInfo> queryProjectList(ServletRequest request, QueryProjectListRequest pa) {
        List<Integer> projectIDList = null;
        if (ObjectUtil.isNotEmpty(pa.getPropertyEntity())){
            projectIDList = tbProjectInfoMapper
                    .getProjectIDByProperty(pa.getPropertyEntity(), pa.getPropertyEntity().size());
        }

        pa.setProjectIDList(projectIDList);
        Page<ProjectInfo> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<ProjectInfo> dataList = tbProjectInfoMapper.getProjectList(page, pa);

        List<Integer> ids = dataList.getRecords().stream().map(TbProjectInfo::getID).toList();
        if (!ids.isEmpty()) {
            Map<Integer, List<TagDto>> tagGroup = tbTagMapper.queryTagByProjectID(ids)
                    .stream().collect(Collectors.groupingBy(TagDto::getProjectID));

            Map<Integer, List<PropertyDto>> propMap = tbProjectPropertyMapper
                    .queryPropertyByProjectID(ids, 0).stream()
                    .collect(Collectors.groupingBy(PropertyDto::getProjectID));

            Collection<Object> areas = dataList.getRecords()
                    .stream().map(e -> {
                        JSONObject json = JSONUtil.parseObj(e.getLocation());
                        return json.isEmpty() ? null : CollUtil.getLast(json.values());
                    }).filter(Objects::nonNull).collect(Collectors.toSet());;
            Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                    .stream().collect(Collectors.toMap(e -> e.getId().toString(), RegionArea::getName));
            areas.clear();

            dataList.getRecords().forEach(item -> {
                item.setTagInfo(tagGroup.getOrDefault(item.getID(), Collections.emptyList()));
                item.setPropertyList(propMap.getOrDefault(item.getID(), Collections.emptyList()));
                item.setCompany(getCompany(request, item.getCompanyID()));

                JSONObject json = JSONUtil.parseObj(item.getLocation());
                if(!json.isEmpty()) {
                    String areaCode = (String) CollUtil.getLast(json.values());
                    item.setLocation(areaMap.getOrDefault(areaCode, null));
                }

                handlerimagePathToRealPath(item);
            });
        }
        return new PageUtil.PageResult<>((int)page.getPages(), dataList.getRecords(), (int)page.getTotal());
    }

    @Override
    public ProjectInfo queryProjectInfo(ServletRequest request, QueryProjectInfoParam pa) {
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(pa.getID());
        if (projectInfo == null){
            return null;
        }
        ProjectInfo result = new ProjectInfo();
        BeanUtil.copyProperties(projectInfo, result);
        result.setTagInfo(tbTagMapper.queryTagByProjectID(List.of(pa.getID())));
        result.setPropertyList(tbProjectPropertyMapper.queryPropertyByProjectID(List.of(pa.getID()), null));

        JSONObject json = JSONUtil.parseObj(result.getLocation());
        if (!json.isEmpty()) {
            String areaCode = (String) CollUtil.getLast(json.values());
            RegionArea area = redisService.get(RedisKeys.REGION_AREA_KEY, areaCode, RegionArea.class);
            result.setLocation(area != null ? area.getName() : StrUtil.EMPTY);
        }

        handlerimagePathToRealPath(result);
        return result;
    }
}