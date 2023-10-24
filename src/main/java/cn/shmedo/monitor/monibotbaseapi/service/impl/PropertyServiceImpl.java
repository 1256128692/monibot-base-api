package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.cache.FormModelCache;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertySubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.SearchWorkFlowTemplateListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.Model4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryPropertyValuesResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow.DescribeWorkFlowTemplateResponse;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.WorkFlowTemplateService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
@Service
public class PropertyServiceImpl extends ServiceImpl<TbPropertyMapper, TbProperty> implements PropertyService {
    private final TbPropertyMapper tbPropertyMapper;
    private final TbProjectPropertyMapper tbProjectPropertyMapper;
    private final TbPropertyModelMapper tbPropertyModelMapper;
    private final TbPropertyModelGroupMapper tbPropertyModelGroupMapper;
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbOtherDeviceMapper tbOtherDeviceMapper;
    private final FormModelCache formModelCache;
    private final WorkFlowTemplateService workFlowTemplateService;

    public PropertyServiceImpl(TbPropertyMapper tbPropertyMapper,
                               TbProjectPropertyMapper tbProjectPropertyMapper,
                               TbPropertyModelMapper tbPropertyModelMapper,
                               TbPropertyModelGroupMapper tbPropertyModelGroupMapper,
                               TbProjectInfoMapper tbProjectInfoMapper,
                               TbOtherDeviceMapper tbOtherDeviceMapper,
                               FormModelCache formModelCache,
                               WorkFlowTemplateService workFlowTemplateService) {
        this.tbPropertyMapper = tbPropertyMapper;
        this.tbProjectPropertyMapper = tbProjectPropertyMapper;
        this.tbPropertyModelMapper = tbPropertyModelMapper;
        this.tbPropertyModelGroupMapper = tbPropertyModelGroupMapper;
        this.tbProjectInfoMapper = tbProjectInfoMapper;
        this.tbOtherDeviceMapper = tbOtherDeviceMapper;
        this.formModelCache = formModelCache;
        this.workFlowTemplateService = workFlowTemplateService;
    }

    @Override
    public void updateProperty(UpdatePropertyParam pa) {
        updateProperty(pa.getProjectID(), pa.getModelValueList(), pa.getProperties());
    }

    @Override
    public void updateProperty(Integer projectID, List<PropertyIdAndValue> propertyIdAndValueList, List<TbProperty> propertyList) {
        Map<Integer, TbProperty> propertyMap = propertyList.stream().collect(Collectors.toMap(TbProperty::getID, Function.identity()));
        List<TbProjectProperty> projectPropertyList = propertyIdAndValueList.stream().map(
                item -> {
                    TbProjectProperty tbProjectProperty = new TbProjectProperty();
                    tbProjectProperty.setPropertyID(propertyMap.get(item.getID()).getID());
                    tbProjectProperty.setValue(item.getValue());
                    return tbProjectProperty;
                }
        ).collect(Collectors.toList());
        tbProjectPropertyMapper.updateBatch(projectID, projectPropertyList, PropertySubjectType.Project.getType());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer addModel(AddModelParam param, Integer userID) {
        TbPropertyModel record = Param2DBEntityUtil.fromAddModelParam2TbPropertyModel(param, userID);
        tbPropertyModelMapper.insert(record);
        List<TbProperty> properties = Param2DBEntityUtil.fromAddModelParam2TbPropertyList(param, userID, record.getID());
        tbPropertyMapper.insertBatch(properties);

        // 同步更新缓存
        formModelCache.putBatch(List.of(record), properties);
        return record.getID();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer copyModel(CopyModelParam param) {
        TbPropertyModel newTbPropertyModel = BeanUtil.toBean(param.getTbPropertyModel(), TbPropertyModel.class);
        param.wrapperTbPropertyModel(newTbPropertyModel);
        tbPropertyModelMapper.insert(newTbPropertyModel);

        LambdaQueryWrapper<TbProperty> queryWrapper = new QueryWrapper<TbProperty>().lambda().eq(TbProperty::getModelID, param.getModelID());
        List<TbProperty> tbPropertyList = tbPropertyMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(tbPropertyList)) {
            param.wrapperTbProperty(tbPropertyList, newTbPropertyModel.getID());
            tbPropertyMapper.insertBatchSomeColumn(tbPropertyList);
        }

        // 同步更新缓存
        formModelCache.putBatch(List.of(newTbPropertyModel), tbPropertyList);
        return newTbPropertyModel.getID();
    }

    @Override
    public List<String> queryPropertyValue(QueryPropertyValueParam param) {
        return tbProjectPropertyMapper.getPropertyValue(param);
    }

    @Override
    public Boolean transferGrouping(TransferGroupingParam param) {
        List<TbPropertyModel> tbPropertyModelList = param.getTbPropertyModelList();
        tbPropertyModelList.forEach(model -> model.setGroupID(param.getNewGroupID()));
        tbPropertyModelMapper.updateBatchById(tbPropertyModelList);

        // 同步刷新缓存
        List<TbProperty> tbPropertyList = tbPropertyMapper.selectList(new QueryWrapper<TbProperty>().lambda()
                .in(TbProperty::getModelID, param.getModelIDList()));
        formModelCache.putBatch(tbPropertyModelList, tbPropertyList);
        return Boolean.TRUE;
    }

    @Override
    public List<Model4Web> queryModelList(QueryModelListParam param) {
        // 模板ID非空时，根据模板ID精确查询
        if (param.getModelID() != null) {
            TbPropertyModel tbPropertyModel = tbPropertyModelMapper.selectByPrimaryKey(param.getModelID());
            if (tbPropertyModel == null) {
                return List.of();
            }
            List<TbProperty> properties = tbPropertyMapper.selectByModelIDs(List.of(param.getModelID()));
            return List.of(Model4Web.valueOf(tbPropertyModel, properties));
        }

        // 模糊查询
        LambdaQueryWrapper<TbPropertyModel> queryWrapper = new QueryWrapper<TbPropertyModel>().lambda()
                .like(StringUtils.isNotEmpty(param.getName()), TbPropertyModel::getName, param.getName())
                .eq(Objects.nonNull(param.getModelType()), TbPropertyModel::getModelType, param.getModelType())
                .eq(Objects.nonNull(param.getModelTypeSubType()), TbPropertyModel::getModelTypeSubType, param.getModelTypeSubType())
                .eq(Objects.nonNull(param.getGroupID()) && PropertyModelType.BASE_PROJECT.getCode().equals(param.getModelType()),
                        TbPropertyModel::getGroupID, param.getGroupID())
                .eq(Objects.nonNull(param.getCreateType()), TbPropertyModel::getCreateType, param.getCreateType())
                .eq(Objects.nonNull(param.getPlatform()), TbPropertyModel::getPlatform, param.getPlatform());
        if (Objects.nonNull(param.getGroupID()) && Objects.nonNull(param.getModelType()) &&
                (PropertyModelType.DEVICE.getCode().equals(param.getModelType()) || PropertyModelType.WORK_FLOW.getCode().equals(param.getModelType()))) {
            queryWrapper.in(TbPropertyModel::getGroupID, List.of(param.getGroupID(), DefaultConstant.PROPERTY_MODEL_DEFAULT_GROUP));
        }
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(tbPropertyModelList)) {
            return List.of();
        }
        // 处理模板下属性
        List<Model4Web> model4WebList = CustomizeBeanUtil.copyListProperties(tbPropertyModelList, Model4Web::new);
        List<Integer> modeIdList = tbPropertyModelList.stream().map(TbPropertyModel::getID).collect(Collectors.toList());
        List<TbProperty> tbPropertyList = tbPropertyMapper.selectList(new QueryWrapper<TbProperty>().lambda()
                .in(TbProperty::getModelID, modeIdList));
        Map<Integer, List<TbProperty>> propertyMap = tbPropertyList.stream().collect(Collectors.groupingBy(TbProperty::getModelID));

        // 处理组名
        Map<Integer, List<TbPropertyModel>> modelGroup = tbPropertyModelList.stream()
                .collect(Collectors.groupingBy(model -> {
                    if (PropertyModelType.BASE_PROJECT.getCode().equals(model.getModelType())) {
                        return PropertyModelType.BASE_PROJECT.getCode();
                    } else {
                        return PropertyModelType.UN_BASE_PROJECT.getCode();
                    }
                }));

        // 处理非工程项目分组
        Map<Integer, TbPropertyModelGroup> unProjectGroupMap = Maps.newHashMap();
        if (modelGroup.containsKey(PropertyModelType.UN_BASE_PROJECT.getCode())) {
            List<TbPropertyModelGroup> tbPropertyModelGroupList = tbPropertyModelGroupMapper.selectList(
                    new QueryWrapper<TbPropertyModelGroup>().lambda()
                            .eq(Objects.nonNull(param.getCompanyID()), TbPropertyModelGroup::getCompanyID, param.getCompanyID())
                            .eq(Objects.nonNull(param.getPlatform()), TbPropertyModelGroup::getPlatform, param.getPlatform())
                            .in(Objects.nonNull(param.getModelType()), TbPropertyModelGroup::getGroupType, param.getModelType()));
            unProjectGroupMap = tbPropertyModelGroupList.stream().collect(Collectors.toMap(TbPropertyModelGroup::getID, Function.identity()));
        }

        Map<Integer, TbPropertyModelGroup> finalGroupMap = unProjectGroupMap;
        model4WebList.forEach(
                item -> {
                    item.setPropertyList(propertyMap.get(item.getID()));
                    if (PropertyModelType.BASE_PROJECT.getCode().equals(item.getModelType())) {
                        // 工程项目
                        item.setGroupName(ProjectTypeCache.projectTypeMap.get(Byte.valueOf(String.valueOf(item.getGroupID()))).getTypeName());
                    } else {
                        // 非工程项目
                        String groupName = finalGroupMap.containsKey(item.getGroupID()) ? finalGroupMap.get(item.getGroupID()).getName() : "默认";
                        item.setGroupName(groupName);
                    }
                }
        );

        // 工程项目模板组下没有模板，也要显示
        if (PropertyModelType.BASE_PROJECT.getCode().equals(param.getModelType()) && modelGroup.containsKey(PropertyModelType.BASE_PROJECT.getCode())) {
            Set<Integer> groupIDList = modelGroup.get(PropertyModelType.BASE_PROJECT.getCode()).stream().map(TbPropertyModel::getGroupID).collect(Collectors.toSet());
            ProjectTypeCache.projectTypeMap.forEach((k, v) -> {
                if (!groupIDList.contains(Integer.valueOf(String.valueOf(k)))) {
                    Model4Web model4Web = new Model4Web();
                    model4Web.setModelType(PropertyModelType.BASE_PROJECT.getCode());
                    model4Web.setGroupID(Integer.valueOf(k));
                    model4Web.setGroupName(v.getTypeName());
                    model4WebList.add(model4Web);
                }
            });
        }

        // 非工程项目模板组下没有模板，也要显示
        if (CollectionUtil.isNotEmpty(unProjectGroupMap) && modelGroup.containsKey(PropertyModelType.UN_BASE_PROJECT.getCode())) {
            Stream<TbPropertyModel> modelStream = modelGroup.get(PropertyModelType.UN_BASE_PROJECT.getCode()).stream();
            if (Objects.nonNull(param.getModelType()) && !PropertyModelType.BASE_PROJECT.getCode().equals(param.getModelType())) {
                modelStream = modelStream.filter(m -> param.getModelType().equals(m.getModelType()));
            }
            Set<Integer> groupIDList = modelStream.map(TbPropertyModel::getGroupID).collect(Collectors.toSet());
            unProjectGroupMap.forEach((k, v) -> {
                if (!groupIDList.contains(k)) {
                    Model4Web model4Web = new Model4Web();
                    model4Web.setModelType(v.getGroupType());
                    model4Web.setGroupID(k);
                    model4Web.setGroupName(v.getName());
                    model4WebList.add(model4Web);
                }
            });
        }

        return model4WebList.stream().sorted(Comparator.comparing(Model4Web::getGroupID)).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateModel(UpdateModelParam param) {
        List<TbProperty> newTbPropertyList = Lists.newArrayList();
        // 更新模板
        TbPropertyModel tbPropertyModel = param.getTbPropertyModel();
        int row = tbPropertyModelMapper.updateById(tbPropertyModel);

        // 更新模板下属性
        Set<Integer> keySet = param.getAllPropertyIdAndNameMap().keySet();
        if (CollectionUtil.isNotEmpty(param.getAddModelItemList())) {
            Set<Integer> addModelItemIdSet = param.getAddModelItemList().stream().map(ModelItem::getID).collect(Collectors.toSet());
            keySet.removeAll(addModelItemIdSet);
        }
        if (CollectionUtil.isNotEmpty(param.getUpdateModelItemList())) {
            Set<Integer> updateModelItemIdSet = param.getUpdateModelItemList().stream().map(ModelItem::getID).collect(Collectors.toSet());
            keySet.removeAll(updateModelItemIdSet);
        }
        // --1-- 删除属性模板
        if (CollectionUtil.isNotEmpty(keySet)) {
            tbPropertyMapper.deleteBatchIds(keySet);
        }
        // --2-- 修改属性模板
        if (CollectionUtil.isNotEmpty(param.getUpdateModelItemList())) {
            List<TbProperty> tbPropertyList = CustomizeBeanUtil.copyListProperties(param.getUpdateModelItemList(), TbProperty::new);
            updateBatchById(tbPropertyList);
            newTbPropertyList.addAll(tbPropertyList);
        }
        // --3-- 新增属性模板
        if (CollectionUtil.isNotEmpty(param.getAddModelItemList())) {
            List<TbProperty> tbPropertyList = CustomizeBeanUtil.copyListProperties(param.getAddModelItemList(), TbProperty::new);
            tbPropertyMapper.insertBatch(tbPropertyList);
            newTbPropertyList.addAll(tbPropertyList);
        }

        // 同步刷新缓存
        formModelCache.putBatch(List.of(tbPropertyModel), newTbPropertyList);
        return 1 == row;
    }

    public Boolean deleteModelCheck(DeleteModelParam param) {
        List<Integer> modelIDList;
        Map<Integer, List<TbPropertyModel>> modelTypeGroupMap = param.getTbPropertyModelList().stream().collect(Collectors.groupingBy(TbPropertyModel::getModelType));
        // 工程项目类型校验表单模板是否有被项目使用
        if (modelTypeGroupMap.containsKey(PropertyModelType.BASE_PROJECT.getCode())) {
            modelIDList = modelTypeGroupMap.get(PropertyModelType.BASE_PROJECT.getCode()).stream().map(TbPropertyModel::getID).toList();
            List<TbProjectInfo> tbProjectInfoList = tbProjectInfoMapper.selectList(new QueryWrapper<TbProjectInfo>().lambda()
                    .eq(TbProjectInfo::getCompanyID, param.getCompanyID())
                    .in(TbProjectInfo::getModelID, modelIDList));
            if (CollectionUtil.isEmpty(tbProjectInfoList)) {
                return Boolean.TRUE;
            }
        }
        // 设备类型校验表单模板是否有被设备使用
        if (modelTypeGroupMap.containsKey(PropertyModelType.DEVICE.getCode())) {
            modelIDList = modelTypeGroupMap.get(PropertyModelType.DEVICE.getCode()).stream().map(TbPropertyModel::getID).toList();
            List<TbOtherDevice> tbOtherDeviceList = tbOtherDeviceMapper.selectList(new QueryWrapper<TbOtherDevice>().lambda()
                    .eq(TbOtherDevice::getCompanyID, param.getCompanyID())
                    .in(TbOtherDevice::getTemplateID, modelIDList));
            if (CollectionUtil.isEmpty(tbOtherDeviceList)) {
                return Boolean.TRUE;
            }
        }
        // 工作流类型校验表单模板是否有被工作流使用
        if (modelTypeGroupMap.containsKey(PropertyModelType.WORK_FLOW.getCode())) {
            modelIDList = modelTypeGroupMap.get(PropertyModelType.WORK_FLOW.getCode()).stream().map(TbPropertyModel::getID).toList();
            ResultWrapper<List<DescribeWorkFlowTemplateResponse>> resultWrapper = workFlowTemplateService
                    .searchWorkFlowTemplateList(new SearchWorkFlowTemplateListParam(param.getCompanyID(), param.getModelIDList()));
            if(resultWrapper.apiSuccess()){
                if(CollectionUtil.isEmpty(resultWrapper.getData())){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteModel(DeleteModelParam param) {
        LambdaQueryWrapper<TbProperty> propertyLambdaQueryWrapper = new QueryWrapper<TbProperty>().lambda().in(TbProperty::getModelID, param.getModelIDList());
        tbPropertyMapper.delete(propertyLambdaQueryWrapper);

        // 同步删除redis缓存
        formModelCache.removeBatch(param.getModelIDList());
        return tbPropertyModelMapper.deleteBatchIds(param.getModelIDList());
    }

    @Override
    public void AddPropertyValues(AddPropertyValuesParam param) {
        List<TbProjectProperty> tbProjectPropertyList = param.wrapperToPropertyValues();
        tbProjectPropertyMapper.insertBatch(tbProjectPropertyList);
    }

    @Override
    public Object queryPropertyValues(QueryPropertyValuesParam param) {
        List<QueryPropertyValuesResponse> modelList = Lists.newArrayList();
        // 查询模板下的属性
        List<TbProperty> tbPropertyList = tbPropertyMapper.selectByModelIDs(param.getModelIDList());
        if (CollectionUtil.isEmpty(tbPropertyList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板下属性为空");
        }
        Map<Integer, List<TbProperty>> propertyGroup = tbPropertyList.stream().collect(Collectors.groupingBy(TbProperty::getModelID));
        List<Integer> propertyIdList = tbPropertyList.stream().map(TbProperty::getID).toList();

        // 查询属性下的属性值
        List<TbProjectProperty> tbProjectPropertyList = tbProjectPropertyMapper.selectList(new QueryWrapper<TbProjectProperty>().lambda()
                .eq(TbProjectProperty::getSubjectType, param.getSubjectType())
                .eq(TbProjectProperty::getProjectID, param.getSubjectID())
                .in(TbProjectProperty::getPropertyID, propertyIdList));

        param.wrapperToPropertyValues(modelList, propertyGroup, tbProjectPropertyList);
        return modelList;
    }
}
