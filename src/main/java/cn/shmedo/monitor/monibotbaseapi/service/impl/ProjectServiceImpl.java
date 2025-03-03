package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.ErrorConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.config.AuthServiceIDAndProjectTypeRelation;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Company;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.TagDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.QueryFileInfoRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.AuthService;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
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
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:24
 **/
@DependsOn("projectTypeCache")
@EnableTransactionManagement
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectServiceImpl extends ServiceImpl<TbProjectInfoMapper, TbProjectInfo> implements ProjectService, InitializingBean {
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbTagMapper tbTagMapper;
    private final TbProjectTypeMapper tbProjectTypeMapper;
    private final TbTagRelationMapper tbTagRelationMapper;
    private final TbProjectPropertyMapper tbProjectPropertyMapper;
    private final FileConfig fileConfig;
    private final PropertyService propertyService;
    private final MdInfoService mdInfoService;
    private final TbProjectConfigMapper tbProjectConfigMapper;
    private final FileService fileService;
    private final TbMonitorItemMapper tbMonitorItemMapper;
    private final TbMonitorItemFieldMapper tbMonitorItemFieldMapper;
    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;
    private final TbProjectRelationMapper tbProjectRelationMapper;
    private final TbDocumentFileMapper tbDocumentFileMapper;
    private final TbProjectServiceRelationMapper tbProjectServiceRelationMapper;

    private final TbSensorMapper tbSensorMapper;
    private final RegionArea defaultRegionArea = new RegionArea();
    private Map<String, RegionArea> regionAreaMap;
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private RedisService monitorRedisService;
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.IOT_REDIS_SERVICE)
    private RedisService iotRedisService;
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.AUTH_REDIS_SERVICE)
    private RedisService authRedisService;

    @Override
    public void afterPropertiesSet() throws Exception {
        regionAreaMap = monitorRedisService.getAll(RedisKeys.REGION_AREA_KEY, RegionArea.class);
        // 缓存预热--工程项目信息
        setupProjectInfoCache();
    }

    /**
     * 设置工程项目信息缓存
     */
    private void setupProjectInfoCache() {
        // redis没有key时才参加缓存预热
        if (!monitorRedisService.hasKey(RedisKeys.PROJECT_KEY)) {
            List<TbProjectInfo> list = this.list();
            Optional.ofNullable(list).ifPresent(li -> {
                // 处理属性
                List<ProjectInfoCache> cacheList = BeanUtil.copyToList(li, ProjectInfoCache.class);
                cacheList.forEach(projectInfoCache -> doProjectInfoCache(projectInfoCache, regionAreaMap));
                // 设置缓存
                monitorRedisService.putAll(RedisKeys.PROJECT_KEY, cacheList.stream().collect(Collectors.toMap(ProjectInfoCache::getID, Function.identity())));
            });
        }
    }

    private void doProjectInfoCache(ProjectInfoCache projectInfoCache, Map<String, RegionArea> regionAreaMap) {
        // 项目类型
        TbProjectType tbProjectType = ProjectTypeCache.projectTypeMap.getOrDefault(projectInfoCache.getProjectType(), null);
        Optional.ofNullable(tbProjectType).ifPresent(t -> {
            projectInfoCache.setProjectTypeName(t.getTypeName());
            projectInfoCache.setProjectMainTypeName(t.getMainType());
        });
        // 行政区划
        Optional.ofNullable(projectInfoCache.getLocation()).filter(StringUtils::isNotBlank)
                .map(loc -> {
                    ProjectInfoCache.LocationInfo locationInfo = JSONUtil.toBean(loc, ProjectInfoCache.LocationInfo.class);
                    locationInfo.setProvinceName(regionAreaMap.getOrDefault(String.valueOf(locationInfo.getProvince()), defaultRegionArea).getName())
                            .setCityName(regionAreaMap.getOrDefault(String.valueOf(locationInfo.getCity()), defaultRegionArea).getName())
                            .setAreaName(regionAreaMap.getOrDefault(String.valueOf(locationInfo.getArea()), defaultRegionArea).getName())
                            .setTownName(regionAreaMap.getOrDefault(String.valueOf(locationInfo.getTown()), defaultRegionArea).getName());
                    return locationInfo;
                })
                .ifPresent(projectInfoCache::setLocationInfo);
    }

    private static List<String> convertToRaiseCropNameList(String raiseCropName) {
        try {
            // 尝试解析为 JSON 数组
            return JSONUtil.parseArray(raiseCropName).toList(String.class);
        } catch (Exception e) {
            // 解析失败，将其作为单个元素的列表处理
            return Collections.singletonList(raiseCropName);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addProject(AddProjectParam pa, Integer userID) {
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
                    tbProjectProperty.setProjectID(Long.valueOf(tbProjectInfo.getID()));
                    tbProjectProperty.setPropertyID(item.getID());
                    tbProjectProperty.setValue(PropertyIDAndValueMap.get(item.getID()));
                    tbProjectProperty.setSubjectType(PropertySubjectType.Project.getType());
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
                tbMonitorItem.setCompanyID(pa.getCompanyID());
                tbMonitorItem.setProjectID(tbProjectInfo.getID());
                tbMonitorItem.setCreateTime(now);
                tbMonitorItem.setCreateUserID(userID);
                tbMonitorItem.setUpdateTime(now);
                tbMonitorItem.setUpdateUserID(userID);
                // 和原有的保持一致
//                tbMonitorItem.setCreateType(CreateType.PREDEFINED.getType());
            }
            tbMonitorItemMapper.insertBatch(map.keySet());
            map.forEach((key, value) -> {
                value.forEach(item -> {
                    item.setMonitorItemID(key.getID());
                });
            });
            tbMonitorItemFieldMapper.insertEntityBatch(map.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
            // 根据监测项目处理监测类别
            List<Byte> monitorClassList = pa.getMonitorItems().stream().map(TbMonitorItem::getMonitorClass).filter(Objects::nonNull).distinct().toList();
            if (CollectionUtils.isNotEmpty(monitorClassList)) {
                List<TbProjectMonitorClass> tempList = monitorClassList.stream().map(e -> {
                    TbProjectMonitorClass obj = new TbProjectMonitorClass();
                    obj.setProjectID(tbProjectInfo.getID());
                    obj.setMonitorClass(e.intValue());
                    obj.setEnable(true);
                    obj.setDensity("2h");
                    return obj;

                }).toList();
                tbProjectMonitorClassMapper.insertBatch(tempList);
            }

        }
        // 处理和服务的关系
        tbProjectServiceRelationMapper.insertBatchSomeColumn(
                pa.getServiceIDList().stream().map(sid ->
                        TbProjectServiceRelation.builder()
                                .projectID(tbProjectInfo.getID()).serviceID(sid).build()
                ).toList()
        );

        // 新增项目权限
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV3> resourceItemV3s = new LinkedList<ResourceItemV3>();
        // 插入成功,并且添加权限
        if (tbProjectInfo.getID() != null) {
            resourceItemV3s.add(new ResourceItemV3(DefaultConstant.AUTH_RESOURSE, tbProjectInfo.getID().toString(), tbProjectInfo.getProjectName()));
        }
        if (!CollectionUtil.isEmpty(resourceItemV3s)) {
            ResultWrapper<Object> info = null;
            if (DefaultConstant.MDWT_PROJECT_TYPE_LIST.contains(pa.getProjectType())) {
                info = instance.addMdmbaseResource(fileConfig.getAuthAppKey(),
                        fileConfig.getAuthAppSecret(), new AddResourcesParameter(pa.getCompanyID(), resourceItemV3s));
            } else {
                info = instance.addMdwtResource(fileConfig.getAuthAppKey(),
                        fileConfig.getAuthAppSecret(), new AddResourcesParameter(pa.getCompanyID(), resourceItemV3s));
            }
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }

        // 添加项目缓存
        ProjectInfoCache projectInfoCache = BeanUtil.toBean(tbProjectInfo, ProjectInfoCache.class);
        doProjectInfoCache(projectInfoCache, regionAreaMap);
        monitorRedisService.putIfAbsent(RedisKeys.PROJECT_KEY, String.valueOf(projectInfoCache.getID()), projectInfoCache);
        return tbProjectInfo.getID();
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
    public List<TbProjectType> getProjectType(Integer serviceID) {
        //查询全部项目类型并返回
        List<TbProjectType> list = tbProjectTypeMapper.selectAll();
        if (ObjectUtil.isNotEmpty(serviceID)) {
            if (AuthServiceIDAndProjectTypeRelation.isAllTypeService(serviceID)) {
                return list;
            }
            if (!AuthServiceIDAndProjectTypeRelation.isLegalServiceID(serviceID)) {
                return List.of();
            }
            list = list.stream().filter(e -> AuthServiceIDAndProjectTypeRelation.isLegalProjectType(serviceID, e.getTypeName())).toList();
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void transferProject(TransferProjectParam param, CurrentSubject currentSubject) {
        transferProject(currentSubject.getSubjectID(), param.getProjectID(),
                param.getCompanyID(), param.getRowCompanyID(), param.getProjectType());
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferProject(Integer userID, Integer projectID, Integer newCompanyID, Integer oldCompanyID, Integer projectType) {
        tbProjectInfoMapper.updateCompanyID(projectID, newCompanyID, userID, new Date());
        tbTagRelationMapper.deleteByProjectID(projectID);
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV2> resourceItemV2s = new LinkedList<ResourceItemV2>();
        // 转移成功后,并且修改项目资源的公司ID
        resourceItemV2s.add(new ResourceItemV2(DefaultConstant.AUTH_RESOURSE, projectID.toString()));
        if (!CollectionUtil.isEmpty(resourceItemV2s)) {
            ResultWrapper<Object> info = null;
            if (DefaultConstant.MDWT_PROJECT_TYPE_LIST.contains(projectType)) {
                info = instance.transferMdwtResource(fileConfig.getAuthAppKey(),
                        fileConfig.getAuthAppSecret(), new TransferResourceParameter(oldCompanyID,
                                newCompanyID, resourceItemV2s));
            } else {
                info = instance.transferMdmbaseResource(fileConfig.getAuthAppKey(),
                        fileConfig.getAuthAppSecret(), new TransferResourceParameter(oldCompanyID,
                                newCompanyID, resourceItemV2s));
            }

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
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectList(ProjectIDListParam param) {
        tbProjectInfoMapper.deleteProjectInfoList(param.getDataIDList());
        tbTagRelationMapper.deleteProjectTagList(param.getDataIDList());
        tbProjectPropertyMapper.deleteProjectPropertyList(param.getDataIDList(), PropertySubjectType.Project.getType());
        //TODO:删除关联信息以及水利平台相关关联信息

        // 删除项目权限,区分水利项目与其他业务项目
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<Integer> wtProjectIDs = param.getProjectInfoList().stream().filter(pi -> DefaultConstant.MDWT_PROJECT_TYPE_LIST
                .contains(pi.getProjectType().intValue())).map(TbProjectInfo::getID).toList();
        List<Integer> otherProjectIDs = param.getProjectInfoList().stream().filter(pi -> !DefaultConstant.MDWT_PROJECT_TYPE_LIST
                .contains(pi.getProjectType().intValue())).map(TbProjectInfo::getID).toList();
        if (!CollectionUtil.isEmpty(wtProjectIDs)) {
            List<ResourceItemV2> resourceItemV2s = wtProjectIDs.stream().map(item ->
                    new ResourceItemV2(DefaultConstant.AUTH_RESOURSE, item.toString())).toList();
            ResultWrapper<Object> info = instance.deleteMdwtResource(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new DeleteResourcesParameter(param.getCompanyID(), resourceItemV2s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
        if (!CollectionUtil.isEmpty(otherProjectIDs)) {
            List<ResourceItemV2> resourceItemV2s = otherProjectIDs.stream().map(item ->
                    new ResourceItemV2(DefaultConstant.AUTH_RESOURSE, item.toString())).toList();
            ResultWrapper<Object> info = instance.deleteMdmbaseResource(fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret(), new DeleteResourcesParameter(param.getCompanyID(), resourceItemV2s));
            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }
        // 删除项目关系
        tbProjectRelationMapper.delete(new LambdaQueryWrapper<TbProjectRelation>()
                .in(TbProjectRelation::getUpLevelID, param.getDataIDList())
                .or()
                .in(TbProjectRelation::getDownLevelID, param.getDataIDList())
        );

        // 删除工程项目下资料文件
        tbDocumentFileMapper.delete(new QueryWrapper<TbDocumentFile>().lambda()
                .eq(TbDocumentFile::getSubjectType, DocumentSubjectType.PROJECT.getCode())
                .in(TbDocumentFile::getSubjectID, param.getDataIDList()));
        tbProjectInfoMapper.updateLevel2Unallocated();

        // 删除项目缓存
        List<String> idList = param.getDataIDList().stream().map(String::valueOf).collect(Collectors.toList());
        monitorRedisService.remove(RedisKeys.PROJECT_KEY, idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(UpdateProjectParameter pa, Integer userID) {
        TbProjectInfo projectInfo = pa.buildProject(userID);
        tbProjectInfoMapper.updateById(projectInfo);
        if (!CollectionUtil.isEmpty(pa.getPropertyDataList())) {
            List<TbProjectProperty> projectProperties = pa.buildPropertyDataList();
            tbProjectPropertyMapper.updateBatch(pa.getProjectID(), projectProperties, PropertySubjectType.Project.getType());
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
            transferProject(userID, pa.getProjectID(), pa.getNewCompanyID(),
                    projectInfo.getCompanyID(), projectInfo.getProjectType().intValue());
        }
        if (ObjectUtil.isAllNotEmpty(pa.getFileName(), pa.getImageContent(), pa.getImageSuffix())) {
            updateProjectImage(pa.getImageContent(), pa.getImageSuffix(), pa.getFileName(), userID,
                    pa.getNewCompanyID() == null ? projectInfo.getCompanyID() : pa.getNewCompanyID()
                    , pa.getProjectID());
        }
        PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        List<ResourceItemV3> resourceItemV3s = new LinkedList<ResourceItemV3>();
        // 处理服务
        if (ObjectUtil.isNotEmpty(pa.getServiceIDList())) {
            tbProjectServiceRelationMapper.delete(
                    new LambdaQueryWrapper<TbProjectServiceRelation>()
                            .eq(TbProjectServiceRelation::getProjectID, pa.getProjectID())
            );
            tbProjectServiceRelationMapper.insertBatchSomeColumn(
                    pa.getServiceIDList().stream().map(sid ->
                            TbProjectServiceRelation.builder()
                                    .projectID(pa.getProjectID()).serviceID(sid).build()
                    ).toList()
            );
        }
        // 更新成功后,并且修改项目资源的描述
        if (projectInfo != null) {
            resourceItemV3s.add(new ResourceItemV3(DefaultConstant.AUTH_RESOURSE, projectInfo.getID().toString(), projectInfo.getProjectName()));
        }
        if (!CollectionUtil.isEmpty(resourceItemV3s)) {
            ResultWrapper<Object> info = null;
            if (DefaultConstant.MDWT_PROJECT_TYPE_LIST.contains(projectInfo.getProjectType())) {
                info = instance.updateMdwtResourceDesc(fileConfig.getAuthAppKey(),
                        fileConfig.getAuthAppSecret(), new UpdateResourceDescParameter(resourceItemV3s));
            } else {
                info = instance.updateMdmbaseResourceDesc(fileConfig.getAuthAppKey(),
                        fileConfig.getAuthAppSecret(), new UpdateResourceDescParameter(resourceItemV3s));
            }

            if (!info.apiSuccess()) {
                throw new CustomBaseException(info.getCode(), info.getMsg());
            }
        }

        // 刷新项目缓存
        ProjectInfoCache projectInfoCache = BeanUtil.toBean(projectInfo, ProjectInfoCache.class);
        doProjectInfoCache(projectInfoCache, regionAreaMap);
        monitorRedisService.put(RedisKeys.PROJECT_KEY, String.valueOf(projectInfoCache.getID()), projectInfoCache);
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
    public PageUtil.Page<ProjectInfo> queryProjectList(QueryProjectListRequest pa) {
        if (pa.getProjectIDList().isEmpty()) {
            return PageUtil.Page.empty();
        }
        List<ProjectInfo> allList = tbProjectInfoMapper.getProjectList(pa);
        // 进行分层处理
        // 获取公司下项目的级联关系
        List<TbProjectRelation> relationList = tbProjectRelationMapper.queryByCompanyID(pa.getCompanyID());
        List<ProjectInfo> oneList =
                allList.stream().filter(
                        e -> {
                            if (e.getLevel().equals(ProjectLevel.One.getLevel()) || e.getLevel().equals(ProjectLevel.Unallocated.getLevel())) {
                                return true;
                            }
                            if (e.getLevel().equals(ProjectLevel.Two.getLevel()) && relationList.stream().noneMatch(item -> item.getDownLevelID().equals(e.getID()))) {
                                return true;
                            }
                            if (e.getLevel().equals(ProjectLevel.Son.getLevel()) && relationList.stream().noneMatch(item -> item.getDownLevelID().equals(e.getID()))) {
                                return true;
                            }
                            return false;
                        }
                ).toList();
        // 需要重写equals方法,仅根据id判断
        List<ProjectInfo> finalOneList = oneList;
        allList = allList.stream().filter(e -> !finalOneList.contains(e)).toList();
        List<ProjectInfo> twoList = allList.stream().filter(e -> e.getLevel().equals(ProjectLevel.Two.getLevel())).toList();
        List<ProjectInfo> threeList = allList.stream().filter(e -> e.getLevel().equals(ProjectLevel.Son.getLevel())).toList();
        // 进行填充
        // 先从下往上填充
        List<ProjectInfo> finalThreeList = threeList;
        List<Integer> temTwoIDList = new ArrayList<>(relationList.stream().filter(
                e -> finalThreeList.stream().anyMatch(item -> item.getID().equals(e.getDownLevelID()))
        ).map(TbProjectRelation::getUpLevelID).toList());
        temTwoIDList.addAll(twoList.stream().map(TbProjectInfo::getID).toList());
        temTwoIDList = new ArrayList<>(temTwoIDList.stream().distinct().toList());

        List<Integer> finalTemTwoIDList = temTwoIDList;
        List<Integer> temOneIDList = new ArrayList<>(relationList.stream().filter(
                e -> finalTemTwoIDList.stream().anyMatch(item -> item.equals(e.getDownLevelID()))
        ).map(TbProjectRelation::getUpLevelID).toList());
        temOneIDList.addAll(oneList.stream().map(TbProjectInfo::getID).toList());
        if (temOneIDList.isEmpty()) {
            return PageUtil.Page.empty();
        }
        // 过滤
        oneList = tbProjectInfoMapper.selectBatchIds(temOneIDList)
                .stream().map(e ->
                        BeanUtil.copyProperties(e, ProjectInfo.class)
                ).toList();
        if (pa.getIsSonLevel() != null) {
            oneList = oneList.stream().filter(e ->
                    pa.getIsSonLevel() == e.getLevel().equals(ProjectLevel.Son.getLevel())

            ).toList();
        }
        // 再从上往下填充
        List<ProjectInfo> finalOneList1 = oneList;
        List<Integer> tttow = relationList.stream().filter(
                e -> finalOneList1.stream().anyMatch(item -> item.getID().equals(e.getUpLevelID()))
        ).map(TbProjectRelation::getDownLevelID).toList();
        temTwoIDList.addAll(tttow);
        twoList = ObjectUtil.isEmpty(temTwoIDList) ? new ArrayList<>() : tbProjectInfoMapper.selectBatchIds(temTwoIDList)
                .stream().map(e ->
                        BeanUtil.copyProperties(e, ProjectInfo.class)
                ).toList();


        List<ProjectInfo> finalTwoList = twoList;
        List<Integer> ttthree = new ArrayList<>(relationList.stream().filter(
                e -> finalTwoList.stream().anyMatch(item -> item.getID().equals(e.getUpLevelID()))
        ).map(TbProjectRelation::getDownLevelID).toList());
        ttthree.addAll(threeList.stream().map(TbProjectInfo::getID).toList());
        threeList = ObjectUtil.isEmpty(ttthree) ? new ArrayList<>() : tbProjectInfoMapper.selectBatchIds(ttthree)
                .stream().map(e ->
                        BeanUtil.copyProperties(e, ProjectInfo.class)
                ).toList();
        oneList = new ArrayList<>(oneList);
        twoList = new ArrayList<>(twoList);
        threeList = new ArrayList<>(threeList);
        // 分页及填充downLevelProjectList
        Comparator<ProjectInfo> comparator = Comparator.comparing(ProjectInfo::getCreateTime);
        if (pa.getCreatTimeAsc() != null && pa.getCreatTimeAsc()) {
        } else {
            comparator = comparator.reversed();
        }
        oneList.sort(comparator);
        twoList.sort(comparator);
        threeList.sort(comparator);
        PageUtil.Page<ProjectInfo> pageData = PageUtil.page(oneList, pa.getPageSize(), pa.getCurrentPage());
        List<ProjectInfo> finalThreeList1 = threeList;
        Set<Integer> pidAllSet = new HashSet<>();
        Set<Integer> companyIDAllSet = new HashSet<>();
        Set<String> locationInfoSet = new HashSet<>();
        List<ProjectInfo> finalTwoList2 = twoList;
        pageData.currentPageData().forEach(item -> {
            pidAllSet.add(item.getID());
            companyIDAllSet.add(item.getCompanyID());
            locationInfoSet.add(item.getLocationInfo());
            List<Integer> list = relationList.stream().filter(e -> e.getUpLevelID().equals(item.getID())).map(TbProjectRelation::getDownLevelID).toList();
            item.setDownLevelProjectList(finalTwoList2.stream().filter(e -> list.contains(e.getID()))
                    .toList());
            pidAllSet.addAll(item.getDownLevelProjectList().stream().map(ProjectInfo::getID).toList());
            companyIDAllSet.addAll(item.getDownLevelProjectList().stream().map(ProjectInfo::getCompanyID).toList());
            locationInfoSet.addAll(item.getDownLevelProjectList().stream().map(ProjectInfo::getLocationInfo).toList());
            if (ObjectUtil.isNotEmpty(item.getDownLevelProjectList())) {
                item.getDownLevelProjectList().forEach(
                        e -> {
                            List<Integer> list1 = relationList.stream().filter(ee -> ee.getUpLevelID().equals(e.getID())).map(TbProjectRelation::getDownLevelID).toList();
                            e.setDownLevelProjectList(finalThreeList1.stream().filter(ee -> list1.contains(ee.getID())).toList());
                            pidAllSet.addAll(e.getDownLevelProjectList().stream().map(ProjectInfo::getID).toList());
                            companyIDAllSet.addAll(e.getDownLevelProjectList().stream().map(ProjectInfo::getCompanyID).toList());
                            locationInfoSet.addAll(e.getDownLevelProjectList().stream().map(ProjectInfo::getLocationInfo).toList());
                        }
                );
            }
        });
        if (!pageData.currentPageData().isEmpty()) {
            //标签
            Map<Integer, List<TagDto>> tagGroup = tbTagMapper.queryTagByProjectID(new ArrayList<>(pidAllSet)).stream()
                    .collect(Collectors.groupingBy(TagDto::getProjectID));
            //属性
            Map<Integer, List<PropertyDto>> propMap = tbProjectPropertyMapper
                    .queryPropertyByProjectID(new ArrayList<>(pidAllSet), null, PropertySubjectType.Project.getType()).stream()
                    .collect(Collectors.groupingBy(PropertyDto::getProjectID));
            //行政区划
            Map<String, String> areaMap = monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY, locationInfoSet.stream().filter(Objects::nonNull)
                            .collect(Collectors.toSet()), RegionArea.class)
                    .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
            //公司信息
            Map<Integer, Company> companyMap = iotRedisService.multiGet(RedisKeys.IOT_COMPANY_INFO_KEY, new ArrayList<>(companyIDAllSet.stream().map(String::valueOf).toList()), Company.class)
                    .stream().collect(Collectors.toMap(Company::getId, e -> e));
            //图片 TODO 任意文件不存在会返回null，故无法批量获取
//            Map<String, String> imgMap = fileService.getFileUrlList(pageData.getRecords().stream().map(ProjectInfo::getImagePath)
//                            .filter(StrUtil::isNotEmpty).collect(Collectors.toList()), pa.getCompanyID())
//                    .stream().collect(Collectors.toMap(FileInfoResponse::getFilePath, FileInfoResponse::getAbsolutePath));

            Map<String, AuthService> temp = authRedisService.getAll(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, AuthService.class);
            Map<Integer, AuthService> serviceMap = new HashMap<>(temp.size());
            ;
            temp.forEach((key, value) -> {
                serviceMap.put(Integer.valueOf(key), value);
            });
            // 获取全部的项目ID
            List<Integer> pidList = new ArrayList<>();
            pageData.currentPageData().forEach(
                    e -> {
                        pidList.add(e.getID());
                        if (ObjectUtil.isNotEmpty(e.getDownLevelProjectList())) {
                            e.getDownLevelProjectList().forEach(
                                    ee -> {
                                        pidList.add(ee.getID());
                                        if (ObjectUtil.isNotEmpty(ee.getDownLevelProjectList())) {
                                            ee.getDownLevelProjectList().forEach(
                                                    eee -> {
                                                        pidList.add(eee.getID());
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            List<TbProjectServiceRelation> tbProjectServiceRelations = tbProjectServiceRelationMapper.selectList(
                    new LambdaQueryWrapper<TbProjectServiceRelation>()
                            .in(TbProjectServiceRelation::getProjectID, pidList)
            );
            Map<Integer, List<Integer>> pidServiceListMap = tbProjectServiceRelations.stream().collect(
                    Collectors.groupingBy(TbProjectServiceRelation::getProjectID,
                            Collectors.mapping(TbProjectServiceRelation::getServiceID, Collectors.toList())));
            pageData.currentPageData().forEach(item -> {
                item.setTagInfo(tagGroup.getOrDefault(item.getID(), List.of()));
                item.setPropertyList(propMap.getOrDefault(item.getID(), List.of()));
                item.setCompany(companyMap.get(item.getCompanyID()));
                item.setLocationInfo(areaMap.getOrDefault(item.getLocationInfo(), null));
                Optional.ofNullable(item.getImagePath()).filter(e -> !e.isEmpty()).ifPresent(path -> {
                    item.setImagePath(fileService.getFileUrl(item.getImagePath()));
                });
//                item.setImagePath(imgMap.getOrDefault(item.getImagePath(), null));

                // 处理服务
                if (pidServiceListMap.containsKey(item.getID())) {
                    item.setServiceList(
                            pidServiceListMap.get(item.getID()).stream().map(
                                    serviceMap::get
                            ).toList()
                    );
                }
                if (ObjectUtil.isNotEmpty(item.getDownLevelProjectList())) {
                    item.getDownLevelProjectList().forEach(
                            e -> {
                                if (pidServiceListMap.containsKey(e.getID())) {
                                    e.setServiceList(
                                            pidServiceListMap.get(e.getID()).stream().map(
                                                    serviceMap::get
                                            ).toList()
                                    );
                                }
                                if (ObjectUtil.isNotEmpty(e.getDownLevelProjectList())) {
                                    e.getDownLevelProjectList().forEach(
                                            ee -> {
                                                if (pidServiceListMap.containsKey(ee.getID())) {
                                                    ee.setServiceList(
                                                            pidServiceListMap.get(ee.getID()).stream().map(
                                                                    serviceMap::get
                                                            ).toList()
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    );

                    if (ObjectUtil.isNotEmpty(item.getDownLevelProjectList())) {
                        item.getDownLevelProjectList().forEach(
                                ee -> {
                                    if (pidServiceListMap.containsKey(ee.getID())) {
                                        ee.setServiceList(
                                                pidServiceListMap.get(ee.getID()).stream().map(
                                                        serviceMap::get
                                                ).toList()
                                        );
                                    }
                                }
                        );
                    }
                }

            });
        }
        return pageData;
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
        result.setPropertyList(tbProjectPropertyMapper.queryPropertyByProjectID(List.of(pa.getID()), null, PropertySubjectType.Project.getType()));
        if (StrUtil.isNotEmpty(result.getLocationInfo())) {
            RegionArea area = monitorRedisService.get(RedisKeys.REGION_AREA_KEY, result.getLocationInfo(), RegionArea.class);
            result.setLocationInfo(area != null ? area.getName() : StrUtil.EMPTY);
        }

        handlerImagePathToRealPath(result);
        List<Integer> serviceIDList = tbProjectServiceRelationMapper.selectList(
                new LambdaQueryWrapper<TbProjectServiceRelation>().eq(TbProjectServiceRelation::getProjectID, pa.getID())
        ).stream().map(TbProjectServiceRelation::getServiceID).toList();
        Map<String, AuthService> temp = authRedisService.getAll(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, AuthService.class);
        Map<Integer, AuthService> serviceMap = new HashMap<>(temp.size());
        ;
        temp.forEach((key, value) -> {
            serviceMap.put(Integer.valueOf(key), value);
        });
        ;
        result.setServiceList(
                serviceIDList.stream().map(
                        serviceMap::get
                ).toList()
        );
        // 处理为文件或者图片的属性
        result.getPropertyList().forEach(e -> {
            if (FormPropertyType.FILE.getCode().equals(e.getType().intValue())
                    || FormPropertyType.PICTURE.getCode().equals(e.getType().intValue())) {
                if (StrUtil.isNotEmpty(e.getValue())) {
                    e.setOssList(
                            Arrays.stream(e.getValue().split(",")).toList()
                    );
                }

            }
        });
        List<String> ossAllList = result.getPropertyList().stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getOssList()))
                .flatMap(
                        e -> e.getOssList().stream()
                ).toList();
        if (ObjectUtil.isNotEmpty(ossAllList)) {
            List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(ossAllList, result.getCompanyID());
            if (ObjectUtil.isEmpty(fileUrlList)) {
                return result;
            }
            Map<String, FileInfoResponse> fileMap = fileUrlList.stream().collect(Collectors.toMap(
                    FileInfoResponse::getFilePath, Function.identity()
            ));
            result.getPropertyList().forEach(
                    e -> {
                        if (ObjectUtil.isNotEmpty(e.getOssList())) {
                            e.setFileList(
                                    e.getOssList().stream().map(
                                            fileMap::get
                                    ).toList());
                        }
                    }
            );
        }
        return result;
    }

    @Override
    public List<ProjectBaseInfo> queryProjectListByProjectName(QueryProjectListParam pa) {

        LambdaQueryWrapper<TbProjectInfo> wrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getCompanyID, pa.getCompanyID());
        Optional.ofNullable(pa.getProjectIDList()).filter(e -> !e.isEmpty())
                .ifPresent(ids -> wrapper.in(TbProjectInfo::getID, ids));
        if (!StringUtil.isNullOrEmpty(pa.getProjectName())) {
            wrapper.like(TbProjectInfo::getProjectName, pa.getProjectName());
        }
        if (!StringUtil.isNullOrEmpty(pa.getLocation())) {
            wrapper.eq(TbProjectInfo::getLocation, pa.getLocation());
        }
        if (pa.getProjectType() != null) {
            wrapper.eq(TbProjectInfo::getProjectType, pa.getProjectType());
        }
        if (ObjectUtil.isNotEmpty(pa.getProjectTypeList())) {
            wrapper.in(TbProjectInfo::getProjectType, pa.getProjectTypeList());
        }
        if (ObjectUtil.isNotEmpty(pa.getServiceIDList())) {
            List<TbProjectServiceRelation> temp = tbProjectServiceRelationMapper.selectList(
                    new LambdaQueryWrapper<TbProjectServiceRelation>().in(TbProjectServiceRelation::getServiceID, pa.getServiceIDList())
            );
            if (ObjectUtil.isNotEmpty(temp)) {
                wrapper.in(TbProjectInfo::getID, temp.stream().map(TbProjectServiceRelation::getProjectID).toList());
            } else {
                return Collections.emptyList();
            }
        }
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(tbProjectInfos))
            return Collections.emptyList();
        Map<Integer, List<TbSensor>> sensorsGroupedByProject;
        List<Integer> projectIDList = tbProjectInfos.stream().filter(pro -> pro.getProjectType() == 1).map(TbProjectInfo::getID).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(projectIDList)) {
            List<TbSensor> tbSensors = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().in(
                    TbSensor::getProjectID, projectIDList));

            List<TbSensor> filteredSensors = tbSensors.stream()
                    .filter(sensor -> sensor.getStatus() != null)
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(filteredSensors)) {
                sensorsGroupedByProject = filteredSensors.stream()
                        .collect(Collectors.groupingBy(TbSensor::getProjectID));
            } else {
                sensorsGroupedByProject = null;
            }

        } else {
            sensorsGroupedByProject = null;
        }

        Map<Integer, List<TbMonitorItem>> monitorItemMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(tbProjectInfos)){
            List<Integer> idSet = tbProjectInfos.stream().map(TbProjectInfo::getID).toList();
            List<TbMonitorItem> tbMonitorItemList = tbMonitorItemMapper.selectList(new LambdaQueryWrapper<TbMonitorItem>().in(TbMonitorItem::getProjectID, idSet));
            monitorItemMap = tbMonitorItemList.stream().collect(Collectors.groupingBy(TbMonitorItem::getProjectID));
        }
        List<ProjectBaseInfo> projectBaseInfoList = new LinkedList<>();
        Map<Integer, List<TbMonitorItem>> finalMonitorItemMap = monitorItemMap;
        tbProjectInfos.forEach(item -> {
            ProjectBaseInfo result = new ProjectBaseInfo();
            BeanUtil.copyProperties(item, result);
            handlerImagePathToRealPath(result);
            // 水库类型
            if (item.getProjectType() == 1) {
                result.setWaterWarn(0);
                if (CollectionUtil.isNotEmpty(sensorsGroupedByProject)) {
                    List<TbSensor> tbSensors = sensorsGroupedByProject.get(item.getID());
                    if (CollectionUtil.isNotEmpty(tbSensors)) {
                        if (tbSensors.get(0).getStatus() == 0) {
                            result.setWaterWarn(0);
                        }
                        if (tbSensors.get(0).getStatus() == 1 || tbSensors.get(0).getStatus() == 2
                                || tbSensors.get(0).getStatus() == 3 || tbSensors.get(0).getStatus() == 4) {
                            result.setWaterWarn(1);
                        }
                    }
                }
            }
            result.setTbMonitorItemList(finalMonitorItemMap.get(result.getID()));
            projectBaseInfoList.add(result);
        });
        return projectBaseInfoList;
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
                if (map.containsKey(pa.getImgType())) {
                    return fileService.getFileUrl(map.get(pa.getImgType()).toString());
                }
            }
        }
        return null;
    }

    @Override
    public QueryWtProjectResponse queryWtProjectSimpleList(QueryWtProjectParam pa) {

        //有过滤条件先按条件查询对应项目列表，和有权限的项目列表取交集
        if (CollUtil.isNotEmpty(pa.getPropertyList())) {
            List<Integer> projectIDs = tbProjectInfoMapper.getProjectIDByProperty(pa.getPropertyList(), null);
            pa.setProjectList(CollUtil.intersection(projectIDs, pa.getProjectList()));
            if (pa.getProjectList().isEmpty()) {
                return new QueryWtProjectResponse(Collections.emptyList());
            }
        }

        //查询项目列表
        LambdaQueryWrapper<TbProjectInfo> wrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getCompanyID, pa.getCompanyID())
                .le(TbProjectInfo::getProjectType, QueryWtProjectParam.v1KeySet.size())
                .in(TbProjectInfo::getID, pa.getProjectList());
        Optional.ofNullable(pa.getProjectName()).filter(StrUtil::isNotBlank)
                .ifPresent(item -> wrapper.like(TbProjectInfo::getProjectName, item));
        Optional.ofNullable(pa.getProjectType()).ifPresent(item -> wrapper.eq(TbProjectInfo::getProjectType, item));
        List<TbProjectInfo> list = Optional.of(pa.getProjectList()).filter(CollectionUtil::isNotEmpty)
                .map(u -> this.list(wrapper)).orElse(new ArrayList<>());
        List<Integer> idSet = list.stream().map(TbProjectInfo::getID).toList();

        //属性字典
        Map<Integer, Map<String, String>> propMap = idSet.isEmpty() ?
                Collections.emptyMap() :
                tbProjectPropertyMapper.queryPropertyByProjectID(idSet, CreateType.PREDEFINED.getType().intValue(), PropertySubjectType.Project.getType())
                        .stream().collect(Collectors.groupingBy(PropertyDto::getProjectID,
                                Collectors.toMap(PropertyDto::getName, e -> StrUtil.nullToEmpty(e.getValue()), (v1, v2) -> v1)));
        //位置字典
        Collection<Object> areas = list.stream().filter(e -> StrUtil.isNotEmpty(e.getLocation())).map(e -> {
            JSONObject json = JSONUtil.parseObj(e.getLocation());
            e.setLocation(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
            return e.getLocation();
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> areaMap = areas.isEmpty() ?
                Collections.emptyMap() :
                monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                        .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));

        //按项目类型分组处理
        List<QueryWtProjectResponse.WaterInfo> waterInfo = list.stream()
                .collect(Collectors.groupingBy(e -> e.getProjectType().intValue()))
                .entrySet().stream().map(entry -> {
                    List<QueryWtProjectResponse.Detail> dataList = entry.getValue().stream().map(item -> {
                        Map<String, String> ppMap = propMap.getOrDefault(item.getID(), Map.of());
                        String v1 = ppMap.get(QueryWtProjectParam.v1KeySet.get(entry.getKey() - 1));
                        String v2 = ppMap.get(QueryWtProjectParam.v2KeySet.get(entry.getKey() - 1));
                        String location = areaMap.getOrDefault(item.getLocation(), null);
                        return new QueryWtProjectResponse.Detail(item.getID(),
                                item.getProjectName(), item.getShortName(), location, v1, v2);
                    }).toList();
                    TbProjectType projectType = ProjectTypeCache.projectTypeMap.get(entry.getKey().byteValue());
                    return new QueryWtProjectResponse.WaterInfo(entry.getKey(),
                            projectType.getTypeName(), entry.getValue().size(), dataList);
                }).toList();

        return new QueryWtProjectResponse(waterInfo);
    }

    @Override
    public List<QueryProjectBaseInfoResponse> queryProjectBaseInfoList(QueryProjectBaseInfoListParam pa) {

        List<QueryProjectBaseInfoResponse> projectInfoResponseList = new LinkedList();
        List<MonitorPointWithSensor> monitorPointWithSensorList = new LinkedList();

        if (CollectionUtil.isNotEmpty(pa.getMonitorPointWithSensorList())) {
            pa.getMonitorPointWithSensorList().forEach(item -> {
                List<SensorBaseInfoResponse> filerSensorList = pa.getSensorBaseInfoResponseList().stream().filter(s -> s.getMonitorPointID().equals(item.getMonitorPointID())).collect(Collectors.toList());
                item.setSensorList(filerSensorList);
                if (CollectionUtil.isNotEmpty(filerSensorList)) {
                    monitorPointWithSensorList.add(item);
                }
            });
        }
        pa.getProjectInfoResponseList().forEach(item -> {
            List<MonitorPointWithSensor> filerMonitorList = monitorPointWithSensorList.stream().filter(m -> m.getProjectID().equals(item.getProjectID())).collect(Collectors.toList());
            item.setMonitorPointList(filerMonitorList);
            if (CollectionUtil.isNotEmpty(filerMonitorList)) {
                projectInfoResponseList.add(item);
            }
        });
        return projectInfoResponseList;
    }

    @Override
    public Boolean checkProjectName(CheckProjectNameParam pa) {
        return tbProjectInfoMapper.selectCount(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, pa.getCompanyID())
                        .eq(TbProjectInfo::getProjectName, pa.getProjectName())
        ) == 0
                ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setProjectRelation(SetProjectRelationParam pa, Integer subjectID) {
        tbProjectRelationMapper.delete(
                new LambdaQueryWrapper<TbProjectRelation>(
                ).eq(TbProjectRelation::getUpLevelID, pa.getProjectID())
        );
        tbProjectRelationMapper.insertBatch(
                pa.getProjectID(), pa.getNextLevelPIDList(), pa.getRaltionType()
        );
        // 下一级的level
        Byte nextLevel = pa.getRaltionType().equals(ProjectLevel.One.getLevel()) ? ProjectLevel.Two.getLevel() : ProjectLevel.Son.getLevel();
        Date now = new Date();
        // 更新下一级项目的level
        tbProjectInfoMapper.updateLevel(nextLevel, pa.getNextLevelPIDList(), subjectID, now);
        // 更新当前项目的level
        tbProjectInfoMapper.updateLevel(pa.getRaltionType(), List.of(pa.getProjectID()), subjectID, now);
    }

    @Override
    public QueryNextLevelAndAvailableProjectResult queryNextLevelProjectAndCanUsed(QueryNextLevelAndAvailableProjectParam pa) {
        List<TbProjectInfo> nextLevelProjectList = new ArrayList<>();
        /**
         * key: 下级项目id
         * value: 下下级项目列表
         */
        Map<Integer, List<TbProjectInfo>> nnMap = new HashMap<>();
        if (!pa.getTbProjectInfo().getLevel().equals(ProjectLevel.Unallocated.getLevel())) {
            List<TbProjectRelation> temp = tbProjectRelationMapper.selectList(
                    new LambdaQueryWrapper<TbProjectRelation>(
                    ).eq(TbProjectRelation::getUpLevelID, pa.getProjectID())
            );
            List<Integer> nextLevelProjectIDList = temp.stream().map(TbProjectRelation::getDownLevelID).toList();
            if (ObjectUtil.isNotEmpty(nextLevelProjectIDList)) {
                nextLevelProjectList = tbProjectInfoMapper.selectBatchIds(nextLevelProjectIDList);
            }
        }
        if (pa.getTbProjectInfo().getLevel().equals(ProjectLevel.One.getLevel()) && ObjectUtil.isNotEmpty(nextLevelProjectList)) {
            // 获取下下级
            List<TbProjectRelation> temp = tbProjectRelationMapper.selectList(
                    new LambdaQueryWrapper<TbProjectRelation>(
                    ).in(TbProjectRelation::getUpLevelID, nextLevelProjectList.stream().map(TbProjectInfo::getID).toList()));
            List<Integer> nnLevelProjectIDList = temp.stream().map(TbProjectRelation::getDownLevelID).toList();
            if (ObjectUtil.isNotEmpty(nnLevelProjectIDList)) {
                List<TbProjectInfo> nnLevelProjectList = tbProjectInfoMapper.selectBatchIds(nnLevelProjectIDList);
                Map<Integer, List<Integer>> collect = temp.stream().collect(Collectors.groupingBy(TbProjectRelation::getUpLevelID, Collectors.mapping(TbProjectRelation::getDownLevelID, Collectors.toList())));
                collect.forEach((k, v) -> {
                    List<TbProjectInfo> collect1 = nnLevelProjectList.stream().filter(e -> v.contains(e.getID())).collect(Collectors.toList());
                    nnMap.put(k, collect1);
                });
            }
        }
        LambdaQueryWrapper<TbProjectInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TbProjectInfo::getCompanyID, pa.getCompanyID());
        if (ObjectUtil.isNotEmpty(nextLevelProjectList)) {
            lambdaQueryWrapper.notIn(TbProjectInfo::getID, nextLevelProjectList.stream().map(TbProjectInfo::getID).toList());
        }
        if (pa.getTbProjectInfo().getLevel().equals(ProjectLevel.One.getLevel())) {
            lambdaQueryWrapper.and(e ->
                    e.eq(TbProjectInfo::getLevel, ProjectLevel.Two.getLevel())
                            .or()
                            .eq(TbProjectInfo::getLevel, ProjectLevel.Unallocated.getLevel())
            );
        }
        if (pa.getTbProjectInfo().getLevel().equals(ProjectLevel.Two.getLevel())) {
            lambdaQueryWrapper.eq(TbProjectInfo::getLevel, ProjectLevel.Son.getLevel());
        }
        if (pa.getTbProjectInfo().getLevel().equals(ProjectLevel.Unallocated.getLevel())) {
            lambdaQueryWrapper.and(e ->
                    e.eq(TbProjectInfo::getLevel, ProjectLevel.Two.getLevel())
                            .or()
                            .eq(TbProjectInfo::getLevel, ProjectLevel.Son.getLevel())
                            .or()
                            .eq(TbProjectInfo::getLevel, ProjectLevel.Unallocated.getLevel())
            );
            lambdaQueryWrapper.ne(TbProjectInfo::getID, pa.getProjectID());
        }
        List<TbProjectInfo> canUsedProjctList = tbProjectInfoMapper.selectList(lambdaQueryWrapper);
        List<Integer> projectIDList = new ArrayList<>(Stream.of(nextLevelProjectList, canUsedProjctList)
                .flatMap(List::stream)
                .map(TbProjectInfo::getID)
                .toList());
        projectIDList.addAll(nnMap.values().stream().flatMap(List::stream).map(TbProjectInfo::getID).toList());
        List<TbProjectServiceRelation> tbProjectServiceRelations = tbProjectServiceRelationMapper.selectList(
                new LambdaQueryWrapper<TbProjectServiceRelation>()
                        .in(TbProjectServiceRelation::getProjectID, projectIDList)
        );
        Map<Integer, List<Integer>> pidServiceIDListMap = tbProjectServiceRelations.stream().collect(Collectors.groupingBy(
                TbProjectServiceRelation::getProjectID,
                Collectors.mapping(TbProjectServiceRelation::getServiceID, Collectors.toList())
        ));
        Map<String, AuthService> temp = authRedisService.getAll(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, AuthService.class);
        Map<Integer, AuthService> serviceMap = new HashMap<>(temp.size());
        temp.forEach((key, value) -> {
            serviceMap.put(Integer.valueOf(key), value);
        });
        return QueryNextLevelAndAvailableProjectResult.valueOf(nextLevelProjectList, canUsedProjctList, nnMap, pidServiceIDListMap, serviceMap);
    }

    @Override
    @Transactional
    public void removeProjectRelation(RemoveProjectRelationParam pa, Integer subjectID) {
        tbProjectRelationMapper.delete(
                new LambdaQueryWrapper<TbProjectRelation>()
                        .eq(TbProjectRelation::getUpLevelID, pa.getProjectID())
                        .in(TbProjectRelation::getDownLevelID, pa.getNextLevelPIDList())
        );
        // 将没有绑定关系的项目设置为未分配
        tbProjectInfoMapper.updateLevel2Unallocatedwhennorealtion();
    }

    @Override
    public List<ProjectWithNext> queryProjectWithNextList(QueryProjectWithNextListParam pa) {
        LambdaQueryWrapper<TbProjectInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbProjectInfo::getCompanyID, pa.getCompanyID());
        if (ObjectUtil.isNotEmpty(pa.getProjectTypeList())) {
            queryWrapper.in(TbProjectInfo::getProjectType, pa.getProjectTypeList());
        }
        if (ObjectUtil.isNotEmpty(pa.getServiceIDList())) {
            List<TbProjectServiceRelation> temp = tbProjectServiceRelationMapper.selectList(
                    new LambdaQueryWrapper<TbProjectServiceRelation>()
                            .in(TbProjectServiceRelation::getServiceID, pa.getServiceIDList())
            );
            if (ObjectUtil.isEmpty(temp)) {
                return List.of();
            }
            queryWrapper.in(TbProjectInfo::getID, temp.stream().map(TbProjectServiceRelation::getProjectID).toList());
        }
        List<TbProjectInfo> temp = tbProjectInfoMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(temp)) {
            return List.of();
        }
        List<ProjectWithNext> allList = temp.stream().map(
                e -> BeanUtil.copyProperties(e, ProjectWithNext.class)
        ).toList();
        // 进行分层处理
        // 获取公司下项目的级联关系
        List<TbProjectRelation> relationList = tbProjectRelationMapper.queryByCompanyID(pa.getCompanyID());
        List<ProjectWithNext> oneList =
                allList.stream().filter(
                        e -> {
                            if (e.getLevel().equals(ProjectLevel.One.getLevel()) || e.getLevel().equals(ProjectLevel.Unallocated.getLevel())) {
                                return true;
                            }
                            if (e.getLevel().equals(ProjectLevel.Two.getLevel()) && relationList.stream().noneMatch(item -> item.getDownLevelID().equals(e.getID()))) {
                                return true;
                            }
                            if (e.getLevel().equals(ProjectLevel.Son.getLevel()) && relationList.stream().noneMatch(item -> item.getDownLevelID().equals(e.getID()))) {
                                return true;
                            }
                            return false;
                        }
                ).toList();
        // 需要重写equals方法,仅根据id判断
        List<ProjectWithNext> finalOneList = oneList;
        allList = allList.stream().filter(e -> !finalOneList.contains(e)).toList();
        List<ProjectWithNext> twoList = allList.stream().filter(e -> e.getLevel().equals(ProjectLevel.Two.getLevel())).toList();
        List<ProjectWithNext> threeList = allList.stream().filter(e -> e.getLevel().equals(ProjectLevel.Son.getLevel())).toList();
        // 进行填充
        // 先从下往上填充
        List<ProjectWithNext> finalThreeList = threeList;
        List<Integer> temTwoIDList = new ArrayList<>(relationList.stream().filter(
                e -> finalThreeList.stream().anyMatch(item -> item.getID().equals(e.getDownLevelID()))
        ).map(TbProjectRelation::getUpLevelID).toList());
        temTwoIDList.addAll(twoList.stream().map(TbProjectInfo::getID).toList());
        temTwoIDList = new ArrayList<>(temTwoIDList.stream().distinct().toList());

        List<Integer> finalTemTwoIDList = temTwoIDList;
        List<Integer> temOneIDList = new ArrayList<>(relationList.stream().filter(
                e -> finalTemTwoIDList.stream().anyMatch(item -> item.equals(e.getDownLevelID()))
        ).map(TbProjectRelation::getUpLevelID).toList());
        temOneIDList.addAll(oneList.stream().map(TbProjectInfo::getID).toList());
        if (temOneIDList.isEmpty()) {
            return List.of();
        }
        // 过滤
        oneList = tbProjectInfoMapper.selectBatchIds(temOneIDList)
                .stream().map(e ->
                        BeanUtil.copyProperties(e, ProjectWithNext.class)
                ).toList();

        // 再从上往下填充
        List<ProjectWithNext> finalOneList1 = oneList;
        List<Integer> tttow = relationList.stream().filter(
                e -> finalOneList1.stream().anyMatch(item -> item.getID().equals(e.getUpLevelID()))
        ).map(TbProjectRelation::getDownLevelID).toList();
        temTwoIDList.addAll(tttow);
        twoList = ObjectUtil.isEmpty(temTwoIDList) ? new ArrayList<>() : tbProjectInfoMapper.selectBatchIds(temTwoIDList)
                .stream().map(e ->
                        BeanUtil.copyProperties(e, ProjectWithNext.class)
                ).toList();


        List<ProjectWithNext> finalTwoList = twoList;
        List<Integer> ttthree = new ArrayList<>(relationList.stream().filter(
                e -> finalTwoList.stream().anyMatch(item -> item.getID().equals(e.getUpLevelID()))
        ).map(TbProjectRelation::getDownLevelID).toList());
        ttthree.addAll(threeList.stream().map(TbProjectInfo::getID).toList());
        threeList = ObjectUtil.isEmpty(ttthree) ? new ArrayList<>() : tbProjectInfoMapper.selectBatchIds(ttthree)
                .stream().map(e ->
                        BeanUtil.copyProperties(e, ProjectWithNext.class)
                ).toList();
        oneList = new ArrayList<>(oneList);
        twoList = new ArrayList<>(twoList);
        threeList = new ArrayList<>(threeList);
        // 分页及填充downLevelProjectList
        Comparator<ProjectWithNext> comparator = Comparator.comparing(ProjectWithNext::getCreateTime);

        oneList.sort(comparator);
        twoList.sort(comparator);
        threeList.sort(comparator);
        List<ProjectWithNext> finalThreeList1 = threeList;
        Set<Integer> pidAllSet = new HashSet<>();
        Set<Integer> companyIDAllSet = new HashSet<>();
        Set<String> locationInfoSet = new HashSet<>();
        List<ProjectWithNext> finalTwoList2 = twoList;
        oneList.forEach(item -> {
            pidAllSet.add(item.getID());
            companyIDAllSet.add(item.getCompanyID());
            List<Integer> list = relationList.stream().filter(e -> e.getUpLevelID().equals(item.getID())).map(TbProjectRelation::getDownLevelID).toList();
            item.setDownLevelProjectList(finalTwoList2.stream().filter(e -> list.contains(e.getID()))
                    .toList());
            pidAllSet.addAll(item.getDownLevelProjectList().stream().map(ProjectWithNext::getID).toList());
            companyIDAllSet.addAll(item.getDownLevelProjectList().stream().map(ProjectWithNext::getCompanyID).toList());
            if (ObjectUtil.isNotEmpty(item.getDownLevelProjectList())) {
                item.getDownLevelProjectList().forEach(
                        e -> {
                            List<Integer> list1 = relationList.stream().filter(ee -> ee.getUpLevelID().equals(e.getID())).map(TbProjectRelation::getDownLevelID).toList();
                            e.setDownLevelProjectList(finalThreeList1.stream().filter(ee -> list1.contains(ee.getID())).toList());
                            pidAllSet.addAll(e.getDownLevelProjectList().stream().map(ProjectWithNext::getID).toList());
                            companyIDAllSet.addAll(e.getDownLevelProjectList().stream().map(ProjectWithNext::getCompanyID).toList());
                        }
                );
            }
        });
        // 处理服务
        Map<String, AuthService> ssss = authRedisService.getAll(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, AuthService.class);
        Map<Integer, AuthService> serviceMap = new HashMap<>(ssss.size());
        ssss.forEach((key, value) -> {
            serviceMap.put(Integer.valueOf(key), value);
        });
        // 获取全部的项目ID
        List<Integer> pidList = new ArrayList<>();
        oneList.forEach(
                e -> {
                    pidList.add(e.getID());
                    if (ObjectUtil.isNotEmpty(e.getDownLevelProjectList())) {
                        e.getDownLevelProjectList().forEach(
                                ee -> {
                                    pidList.add(ee.getID());
                                    if (ObjectUtil.isNotEmpty(ee.getDownLevelProjectList())) {
                                        ee.getDownLevelProjectList().forEach(
                                                eee -> {
                                                    pidList.add(eee.getID());
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        List<TbProjectServiceRelation> tbProjectServiceRelations = tbProjectServiceRelationMapper.selectList(
                new LambdaQueryWrapper<TbProjectServiceRelation>()
                        .in(TbProjectServiceRelation::getProjectID, pidList)
        );
        Map<Integer, List<Integer>> pidServiceListMap = tbProjectServiceRelations.stream().collect(
                Collectors.groupingBy(TbProjectServiceRelation::getProjectID,
                        Collectors.mapping(TbProjectServiceRelation::getServiceID, Collectors.toList())));
        oneList.forEach(item -> {
            if (pidServiceListMap.containsKey(item.getID())) {
                item.setServiceList(
                        pidServiceListMap.get(item.getID()).stream().map(
                                serviceMap::get
                        ).toList()
                );
            }
            if (ObjectUtil.isNotEmpty(item.getDownLevelProjectList())) {
                item.getDownLevelProjectList().forEach(
                        e -> {
                            if (pidServiceListMap.containsKey(e.getID())) {
                                e.setServiceList(
                                        pidServiceListMap.get(e.getID()).stream().map(
                                                serviceMap::get
                                        ).toList()
                                );
                            }
                            if (ObjectUtil.isNotEmpty(e.getDownLevelProjectList())) {
                                e.getDownLevelProjectList().forEach(
                                        ee -> {
                                            if (pidServiceListMap.containsKey(ee.getID())) {
                                                ee.setServiceList(
                                                        pidServiceListMap.get(ee.getID()).stream().map(
                                                                serviceMap::get
                                                        ).toList()
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );

                if (ObjectUtil.isNotEmpty(item.getDownLevelProjectList())) {
                    item.getDownLevelProjectList().forEach(
                            ee -> {
                                if (pidServiceListMap.containsKey(ee.getID())) {
                                    ee.setServiceList(
                                            pidServiceListMap.get(ee.getID()).stream().map(
                                                    serviceMap::get
                                            ).toList()
                                    );
                                }
                            }
                    );
                }
            }
        });

        return oneList;
    }

    @Override
    public Object queryProjectWithRaiseCrops(QueryProjectWithRaiseCropsParam pa) {
        // 默认只查询田间类型的工程
        List<ProjectWithIrrigationInfo> projectWithIrrigationInfos = tbProjectInfoMapper.queryProjectWithRaiseCrops(pa);
        if (CollectionUtil.isEmpty(projectWithIrrigationInfos)) {
            return Collections.emptyList();
        }
        Map<Integer, List<ProjectWithIrrigationInfo>> groupedByProjectID = projectWithIrrigationInfos.stream()
                .filter(p -> p.getKeyName().equals("种植作物") || p.getKeyName().equals("田间面积"))
                .collect(Collectors.groupingBy(ProjectWithIrrigationInfo::getProjectID));
        if (CollectionUtil.isEmpty(groupedByProjectID)) {
            return Collections.emptyList();
        }

        List<Integer> projectIDList = projectWithIrrigationInfos.stream().map(ProjectWithIrrigationInfo::getProjectID).collect(Collectors.toList());
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectListByCompanyIDAndProjectIDList(pa.getCompanyID(), projectIDList);


        List<ProjectWithIrrigationInfo> projectWithIrrigationInfos1 = groupedByProjectID.values().stream()
                .flatMap(list -> list.stream()
                        .peek(p -> {
                            if ("种植作物".equals(p.getKeyName())) {
                                p.setRaiseCropNameList(convertToRaiseCropNameList(p.getValue()));
                                p.setValue(null);
                            }
                        }))
                .collect(Collectors.toList());
        List<ProjectWithIrrigationBaseInfo> projectWithIrrigationBaseInfoList = new LinkedList<>();
        tbProjectInfos.forEach(p -> {
            ProjectWithIrrigationBaseInfo vo = new ProjectWithIrrigationBaseInfo();
            vo.setProjectID(p.getID());
            vo.setProjectName(p.getProjectName());
            if (!CollectionUtil.isEmpty(projectWithIrrigationInfos1)) {
                vo.setDataList(projectWithIrrigationInfos1.stream().filter(i -> i.getProjectID().equals(p.getID())).collect(Collectors.toList()));
            }
            projectWithIrrigationBaseInfoList.add(vo);
        });


        return projectWithIrrigationBaseInfoList;
    }

}