package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.propertymodelgroup.PropertyModelGroupResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyModelGroupService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2023/9/15 16:56
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.impl
 * @ClassName: PropertyModelGroupServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class PropertyModelGroupServiceImpl implements PropertyModelGroupService {

    @Autowired
    private TbPropertyModelMapper tbPropertyModelMapper;

    @Autowired
    private TbPropertyModelGroupMapper tbPropertyModelGroupMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FileConfig fileConfig;

    @Override
    public Object addPropertyModelGroup(AddPropertyModelGroupParam addPropertyModelGroupParam) {
        TbPropertyModelGroup tbPropertyModelGroup = CustomizeBeanUtil.copyProperties(addPropertyModelGroupParam, TbPropertyModelGroup.class);
        tbPropertyModelGroup.setCreateTime(LocalDateTime.now());
        tbPropertyModelGroup.setCreateUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        tbPropertyModelGroupMapper.insert(tbPropertyModelGroup);
        return tbPropertyModelGroup.getID();
    }

    @Override
    public Object updatePropertyModelGroup(UpdatePropertyModelGroupParam updatePropertyModelGroupParam) {
        TbPropertyModelGroup tbPropertyModelGroup = CustomizeBeanUtil.copyProperties(updatePropertyModelGroupParam, TbPropertyModelGroup.class);
        return 1 == tbPropertyModelGroupMapper.updateById(tbPropertyModelGroup);
    }

    @Override
    public PropertyModelGroupResponse queryPropertyModelGroup(QueryPropertyModelGroupParam queryPropertyModelGroupParam) {
        TbPropertyModelGroup tbPropertyModelGroup = queryPropertyModelGroupParam.getTbPropertyModelGroup();
        PropertyModelGroupResponse propertyModelGroupResponse = CustomizeBeanUtil.copyProperties(tbPropertyModelGroup, PropertyModelGroupResponse.class);
        QueryUserIDNameParameter queryUserIdNameParameter = new QueryUserIDNameParameter(Collections.singletonList(propertyModelGroupResponse.getCreateUserID()));
        ResultWrapper<Object> resultWrapper = userService.queryUserIDName(queryUserIdNameParameter, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
        if (resultWrapper.apiSuccess()) {
            List<Map<String, Object>> userIdNameList = (List<Map<String, Object>>) resultWrapper.getData();
            if (CollectionUtil.isNullOrEmpty(userIdNameList)) {
                return propertyModelGroupResponse;
            }
            List<UserIDName> newUserIdNameList = CustomizeBeanUtil.toBeanList(userIdNameList, UserIDName.class);
            propertyModelGroupResponse.setCreateUserName(newUserIdNameList.get(0).getUserName());
        }
        return propertyModelGroupResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object deletePropertyModelGroup(DeletePropertyModelGroupParam deletePropertyModelGroupParam) {
        // 删除其他设备分组、工作流分组时候，分组下模板不删除，会将模板对应分组ID转为-1（默认分组）
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<TbPropertyModel>().lambda()
                .in(TbPropertyModel::getModelType, List.of(PropertyModelType.DEVICE.getCode(), PropertyModelType.WORK_FLOW.getCode()))
                .in(TbPropertyModel::getGroupID, deletePropertyModelGroupParam.getIDList()));
        if (!CollectionUtil.isNullOrEmpty(tbPropertyModelList)) {
            tbPropertyModelList.forEach(model -> model.setGroupID(DefaultConstant.PROPERTY_MODEL_DEFAULT_GROUP));
            tbPropertyModelMapper.updateBatchById(tbPropertyModelList);
        }
        return tbPropertyModelGroupMapper.deleteBatchIds(deletePropertyModelGroupParam.getIDList());
    }

    @Override
    public Object queryPropertyModelGroupList(QueryPropertyModelGroupListParam param) {
        LambdaQueryWrapper<TbPropertyModelGroup> queryWrapper = new QueryWrapper<TbPropertyModelGroup>().lambda()
                .eq(TbPropertyModelGroup::getCompanyID, param.getCompanyID())
                .eq(TbPropertyModelGroup::getGroupType, param.getGroupType())
                .eq(Objects.nonNull(param.getGroupTypeSubType()), TbPropertyModelGroup::getGroupTypeSubType, param.getGroupTypeSubType())
                .eq(Objects.nonNull(param.getPlatform()), TbPropertyModelGroup::getPlatform, param.getPlatform())
                .eq(StringUtils.isNotEmpty(param.getName()), TbPropertyModelGroup::getName, param.getName());
        List<TbPropertyModelGroup> tbPropertyModelGroupList = tbPropertyModelGroupMapper.selectList(queryWrapper);
        List<PropertyModelGroupResponse> propertyModelGroupResponseList = Lists.newArrayList();
        if (!CollectionUtil.isNullOrEmpty(tbPropertyModelGroupList)) {
            List<Integer> userIdList = tbPropertyModelGroupList.stream().map(TbPropertyModelGroup::getCreateUserID).collect(Collectors.toList());
            ResultWrapper<Object> resultWrapper = userService.queryUserIDName(new QueryUserIDNameParameter(userIdList), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
            if (resultWrapper.apiSuccess()) {
                // 获取CreateUserID对应的CreateUserName
                List<Map<String, Object>> userIdNameList = (List<Map<String, Object>>) resultWrapper.getData();
                if (!CollectionUtil.isNullOrEmpty(userIdNameList)) {
                    List<UserIDName> newUserIdNameList = CustomizeBeanUtil.toBeanList(userIdNameList, UserIDName.class);
                    Map<Integer, UserIDName> userIdNameMap = newUserIdNameList.stream().collect(Collectors.toMap(UserIDName::getUserID, Function.identity()));
                    propertyModelGroupResponseList = CustomizeBeanUtil.copyListProperties(tbPropertyModelGroupList, PropertyModelGroupResponse::new);
                    propertyModelGroupResponseList.forEach(res -> res.setCreateUserName(userIdNameMap.get(res.getCreateUserID()).getUserName()));
                }
            }
        }

        // 添加默认分组
        if (Objects.isNull(param.getName()) && !PropertyModelType.BASE_PROJECT.getCode().equals(param.getGroupType())) {
            PropertyModelGroupResponse propertyModelGroupResponse = new PropertyModelGroupResponse();
            propertyModelGroupResponse.setID(DefaultConstant.PROPERTY_MODEL_DEFAULT_GROUP);
            propertyModelGroupResponse.setGroupType(param.getGroupType());
            propertyModelGroupResponse.setName(DefaultConstant.PROPERTY_MODEL_DEFAULT_GROUP_NAME);
            propertyModelGroupResponseList.add(propertyModelGroupResponse);
        }
        return propertyModelGroupResponseList.stream().sorted(Comparator.comparing(PropertyModelGroupResponse::getID)).toList();
    }

}
