package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResourceType;
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
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.QueryFileInfoRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.PermissionService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.ParamBuilder;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
@EnableTransactionManagement
@Service
@AllArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<TbProjectInfoMapper, TbProjectInfo> implements ProjectService {
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbTagMapper tbTagMapper;
    private final TbProjectTypeMapper tbProjectTypeMapper;
    private final TbTagRelationMapper tbTagRelationMapper;
    private final TbPropertyMapper tbPropertyMapper;
    private final TbProjectPropertyMapper tbProjectPropertyMapper;
    private final FileConfig fileConfig;
    private final RedisService redisService;
    private final PropertyService propertyService;
    private final MdInfoService mdInfoService;
    private final TbProjectConfigMapper tbProjectConfigMapper;
    private final FileService fileService;
    private final TbMonitorItemMapper tbMonitorItemMapper;
    private final TbMonitorItemFieldMapper tbMonitorItemFieldMapper;
    private static final String TOKEN_HEADER = "Authorization";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProject(AddProjectParam pa, Integer userID) {
        String imgPath = "";
        if (StringUtils.isNotBlank(pa.getImageContent()) && StringUtils.isNotBlank(pa.getImageSuffix())) {
            imgPath = handlerImagePath(pa.getImageContent(), pa.getImageSuffix(), userID, null, pa.getCompanyID());
        }
        TbProjectInfo tbProjectInfo = Param2DBEntityUtil.fromAddProjectParam2TbProjectInfo(pa, userID, imgPath);
        tbProjectInfoMapper.insert(tbProjectInfo);

        // 处理属性
        Map<Integer, String> PropertyIDAndValueMap =
                ObjectUtil.isEmpty(pa.getModelValueList()) ?
                        new HashMap<>() :
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
        // 处理监测项目
        if (CollectionUtils.isNotEmpty(pa.getMonitorItemIDList()) && CollectionUtils.isNotEmpty(pa.getMonitorItems())) {
            List<TbMonitorItemField> tbMonitorItemFields = tbMonitorItemFieldMapper.selectList(
                    new QueryWrapper<TbMonitorItemField>().in("MonitorItemID", pa.getMonitorItemIDList())
            );
            Map<Integer, List<TbMonitorItemField>> temp = tbMonitorItemFields.stream().collect(Collectors.groupingBy(TbMonitorItemField::getMonitorItemID));
            Map<TbMonitorItem, List<TbMonitorItemField>> map = new HashMap<>();
            Date now = new Date();
            for (TbMonitorItem tbMonitorItem : pa.getMonitorItems()) {
                map.put(tbMonitorItem, temp.get(tbMonitorItem.getID()));
                tbMonitorItem.setProjectID(tbProjectInfo.getID());
                tbMonitorItem.setCreateTime(now);
                tbMonitorItem.setCreateUserID(userID);
                tbMonitorItem.setUpdateTime(now);
                tbMonitorItem.setUpdateUserID(userID);
                tbMonitorItem.setCreateType(CreateType.CUSTOMIZED.getType());
            }
            tbMonitorItemMapper.insertBatch(map.keySet());
            map.forEach((key, value)->{
                value.forEach(item ->{
                    item.setMonitorItemID(key.getID());
                });
            });
            tbMonitorItemFieldMapper.insertEntityBatch(map.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
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

    private String handlerImagePath(String imageContent, String imageSuffix, Integer userID, String fileName,
                                    Integer companyID) {
        AddFileUploadRequest pojo = ParamBuilder.buildAddFileUploadRequest(imageContent, imageSuffix, userID, fileName, companyID);
        ResultWrapper<FilePathResponse> info = mdInfoService.addFileUpload(pojo);
        if (!info.apiSuccess()) {
            throw new CustomBaseException(info.getCode(), ErrorConstant.IMAGE_INSERT_FAIL);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void transferProject(TransferProjectParam param, CurrentSubject currentSubject) {
        transferProject(currentSubject.getSubjectID(), param.getProjectID(), param.getCompanyID(), param.getRowCompanyID());
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferProject(Integer userID, Integer projectID, Integer newCompanyID, Integer oldCompanyID) {
        tbProjectInfoMapper.updateCompanyID(projectID, newCompanyID, userID, new Date());
        tbTagRelationMapper.deleteByProjectID(projectID);
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV2> resourceItemV2s = new LinkedList<ResourceItemV2>();
        // 转移成功后,并且修改项目资源的公司ID
        resourceItemV2s.add(new ResourceItemV2(DefaultConstant.AUTH_RESOURSE, projectID.toString()));
        if (!CollectionUtil.isEmpty(resourceItemV2s)) {
            ResultWrapper<Object> info = instance.transferMdmbaseResource(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new TransferResourceParameter(oldCompanyID,
                            newCompanyID, resourceItemV2s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
    }


    @Override
    public void raiseExpiryDate(RaiseExpiryDateParam param, Integer userID) {
        tbProjectInfoMapper.updateExpiryDate(param.getProjectID(), param.getNewRetireDate(), userID, new Date());
    }

    private <T extends TbProjectInfo> void handlerImagePathToRealPath(T projectInfo) {
        if (!StrUtil.isBlank(projectInfo.getImagePath())) {
            QueryFileInfoRequest pojo = new QueryFileInfoRequest();
            pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
            pojo.setFilePath(projectInfo.getImagePath());
            ResultWrapper<FileInfoResponse> info = mdInfoService.queryFileInfo(pojo);
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            } else {
                projectInfo.setImagePath(info.getData().getAbsolutePath());
            }
        }
    }

    /**
     * 批量删除
     * TODO:删除关联信息以及水利平台相关关联信息
     *
     * @param idListParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectList(ProjectIDListParam idListParam) {
        tbProjectInfoMapper.deleteProjectInfoList(idListParam.getDataIDList());
        tbTagRelationMapper.deleteProjectTagList(idListParam.getDataIDList());
        tbProjectPropertyMapper.deleteProjectPropertyList(idListParam.getDataIDList());
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(UpdateProjectParameter pa, Integer userID) {
        TbProjectInfo projectInfo = pa.buildProject(userID);
        tbProjectInfoMapper.updateByPrimaryKey(projectInfo);
        if (!CollectionUtil.isEmpty(pa.getPropertyDataList())) {
            List<TbProjectProperty> projectProperties = pa.buildPropertyDataList();
            tbProjectPropertyMapper.updateBatch(pa.getProjectID(), projectProperties);
        }
        //处理标签
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
            tbTagRelationMapper.deleteByProjectID(pa.getProjectID());
            tbTagRelationMapper.insertBatch(tagID4DBList, pa.getProjectID());
        }
        // 处理属性
        if (ObjectUtil.isNotEmpty(pa.getPropertyList())) {
            propertyService.updateProperty(pa.getProjectID(), pa.getPropertyList(), pa.getProperties());
        }
        if (pa.getNewCompanyID() != null) {
            transferProject(userID, pa.getProjectID(), pa.getNewCompanyID(), projectInfo.getCompanyID());
        }
        if (ObjectUtil.isAllNotEmpty(pa.getFileName(), pa.getImageContent(), pa.getImageSuffix())) {
            updateProjectImage(pa.getImageContent(), pa.getImageSuffix(), pa.getFileName(), userID,
                    pa.getNewCompanyID() == null ? projectInfo.getCompanyID() : pa.getNewCompanyID()
                    , pa.getProjectID());
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
        updateProjectImage(pa.getImageContent(), pa.getImageSuffix(), pa.getFileName(), userID, pa.getCompanyID(), pa.getProjectID());
    }

    public void updateProjectImage(String imageContent, String imageSuffix, String fileName, Integer userID, Integer compnayID, Integer projectID) {
        String path = handlerImagePath(imageContent, imageSuffix, userID, fileName, compnayID);
        if (StringUtils.isNotBlank(path)) {
            tbProjectInfoMapper.updatePathByID(path, projectID);
        }
    }

    @Override
    public PageUtil.Page<ProjectInfo> queryProjectList(QueryProjectListRequest pa, String accessToken) {
        List<Integer> userProjectIDs = getUserProjectIDs(pa.getCompanyID(), accessToken);
        if (CollUtil.isEmpty(userProjectIDs)) {
            return PageUtil.Page.empty();
        }
        List<Integer> projectIDList = List.of();
        if (ObjectUtil.isNotEmpty(pa.getPropertyEntity())) {
            projectIDList = tbProjectInfoMapper
                    .getProjectIDByProperty(pa.getPropertyEntity(), pa.getPropertyEntity().size());
            if (CollUtil.isEmpty(projectIDList)) {
                return PageUtil.Page.empty();
            }
        }
        //FIXME 米度查询所有项目，暂时以固定ID判断
        if (DefaultConstant.MD_ID.equals(pa.getCompanyID())) {
            pa.setCompanyID(null);
        }

        userProjectIDs.addAll(projectIDList);
        pa.setProjectIDList(userProjectIDs);
        Page<ProjectInfo> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<ProjectInfo> pageData = tbProjectInfoMapper.getProjectList(page, pa);

        List<Integer> ids = pageData.getRecords().stream().map(TbProjectInfo::getID).toList();
        if (!ids.isEmpty()) {
            Map<Integer, List<TagDto>> tagGroup = tbTagMapper.queryTagByProjectID(ids)
                    .stream().collect(Collectors.groupingBy(TagDto::getProjectID));

            Map<Integer, List<PropertyDto>> propMap = tbProjectPropertyMapper
                    .queryPropertyByProjectID(ids, 0).stream()
                    .collect(Collectors.groupingBy(PropertyDto::getProjectID));

            Collection<Object> areas = pageData.getRecords()
                    .stream().map(ProjectInfo::getLocationInfo).filter(Objects::nonNull).collect(Collectors.toSet());
            Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                    .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
            areas.clear();
            Collection<Object> companyData = pageData.getRecords()
                    .stream().map(s -> s.getCompanyID().toString()).filter(Objects::nonNull).collect(Collectors.toSet());
            List<Company> companies = redisService.multiGet(RedisKeys.COMPANY_INFO_KEY, companyData, Company.class);
            System.out.println(companies);
            Map<Integer, Company> companyMap = redisService.multiGet(RedisKeys.COMPANY_INFO_KEY, companyData, Company.class)
                    .stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
            companyData.clear();

            pageData.getRecords().forEach(item -> {
                item.setTagInfo(tagGroup.getOrDefault(item.getID(), Collections.emptyList()));
                item.setPropertyList(propMap.getOrDefault(item.getID(), Collections.emptyList()));
                item.setCompany(companyMap.getOrDefault(item.getCompanyID(), null));
                item.setLocationInfo(areaMap.getOrDefault(item.getLocationInfo(), null));
                handlerImagePathToRealPath(item);
            });
        }
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public ProjectInfo queryProjectInfo(QueryProjectInfoParam pa) {
        TbProjectInfo projectInfo = tbProjectInfoMapper.selectById(pa.getID());
        if (projectInfo == null) {
            return null;
        }
        ProjectInfo result = new ProjectInfo();
        BeanUtil.copyProperties(projectInfo, result);
        result.setTagInfo(tbTagMapper.queryTagByProjectID(List.of(pa.getID())));
        result.setPropertyList(tbProjectPropertyMapper.queryPropertyByProjectID(List.of(pa.getID()), null));
        if (StrUtil.isNotEmpty(result.getLocationInfo())) {
            RegionArea area = redisService.get(RedisKeys.REGION_AREA_KEY, result.getLocationInfo(), RegionArea.class);
            result.setLocationInfo(area != null ? area.getName() : StrUtil.EMPTY);
        }

        handlerImagePathToRealPath(result);
        return result;
    }

    @Override
    public List<ProjectBaseInfo> queryProjectListByProjectName(QueryProjectListParam pa) {

        List<Integer> projectIDs = getUserProjectIDs(pa.getCompanyID(), pa.getAccessToken());
        LambdaQueryWrapper<TbProjectInfo> wrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getCompanyID, pa.getCompanyID())
                .in(TbProjectInfo::getID, projectIDs);
        if (!CollectionUtil.isNotEmpty(projectIDs)) {
            wrapper.in(TbProjectInfo::getID, projectIDs);
        }
        if (!StringUtil.isNullOrEmpty(pa.getProjectName())) {
            wrapper.like(TbProjectInfo::getProjectName, pa.getProjectName());
        }
        if (pa.getProjectType() != null) {
            wrapper.eq(TbProjectInfo::getProjectType, pa.getProjectType());
        }
        if (pa.getPlatformType() != null) {
            wrapper.in(TbProjectInfo::getPlatformType, pa.getPlatformType().intValue());
        }
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(tbProjectInfos)) {

            List<ProjectBaseInfo> projectBaseInfoList = new LinkedList<>();
            tbProjectInfos.forEach(item -> {
                ProjectBaseInfo result = new ProjectBaseInfo();
                BeanUtil.copyProperties(item, result);
                handlerImagePathToRealPath(result);
                projectBaseInfoList.add(result);
            });

            return projectBaseInfoList;
        } else {
            return null;
        }
    }

    /**
     * 获取用户在该公司中具有权限的项目
     *
     * @return
     */
    @Override
    public List<Integer> getUserProjectIDs(Integer companyID, String accessToken) {
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        QueryResourceListByPermissionParameter pa = new QueryResourceListByPermissionParameter();
        pa.setCompanyID(companyID);
        pa.setServiceName(DefaultConstant.MDNET_SERVICE_NAME);
        pa.setPermissionToken(DefaultConstant.LIST_PROJECT);
        pa.setResourceType(ResourceType.BASE_PROJECT.toInt());
        ResultWrapper<List<String>> info = instance.queryResourceListByPermission(pa, accessToken);
        if (!info.apiSuccess()) {
            throw new CustomBaseException(info.getCode(), info.getMsg());
        }
        List<Integer> pids = info.getData().stream().distinct().map(Integer::parseInt).collect(Collectors.toList());
        return pids;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String setProjectImg(SetProjectImgParam pa, Integer userID) {

        String path = handlerImagePath(pa.getImageContent(), pa.getImageSuffix(), userID, pa.getFileName(), pa.getCompanyID());
        //TODO  项目图片使用group:img key:projectImg
        TbProjectConfig tbProjectConfig = tbProjectConfigMapper.selectOne(
                new QueryWrapper<TbProjectConfig>().eq("projectID", pa.getProjectID())
                        .lambda().eq(TbProjectConfig::getGroup, "img")
                        .eq(TbProjectConfig::getKey, "projectImg")
        );
        if (tbProjectConfig == null) {
            tbProjectConfig = new TbProjectConfig();
            tbProjectConfig.setProjectID(pa.getProjectID());
            tbProjectConfig.setGroup("img");
            tbProjectConfig.setKey("projectImg");
            tbProjectConfig.setValue(JSONUtil.toJsonStr(Map.of(pa.getImgType(), path)));
            tbProjectConfigMapper.insert(tbProjectConfig);
        } else {
            String value = tbProjectConfig.getValue();
            if (StrUtil.isNotEmpty(value)) {
                Map map = JSONUtil.toBean(value, Map.class);
                map.put(pa.getImgType(), path);
                tbProjectConfig.setValue(JSONUtil.toJsonStr(map));
            } else {
                tbProjectConfig.setValue(JSONUtil.toJsonStr(Map.of(pa.getImgType(), path)));
            }
            tbProjectConfigMapper.updateByPrimaryKey(tbProjectConfig);
        }
        return fileService.getFileUrl(path);
    }

    @Override
    public String queryProjectImg(QueryProjectImgParam pa) {
        TbProjectConfig tbProjectConfig = tbProjectConfigMapper.selectOne(
                new QueryWrapper<TbProjectConfig>().eq("projectID", pa.getProjectID())
                        .lambda().eq(TbProjectConfig::getGroup, "img")
                        .eq(TbProjectConfig::getKey, "projectImg")
        );
        if (tbProjectConfig != null && StrUtil.isNotEmpty(tbProjectConfig.getValue())) {
            String value = tbProjectConfig.getValue();
            if (StrUtil.isNotEmpty(value)) {
                Map map = JSONUtil.toBean(value, Map.class);
                Object obj = map.get(pa.getImgType());
                if (obj!=null){
                    return fileService.getFileUrl(obj.toString());
                }
            }
        }
        return null;
    }

}