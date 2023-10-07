package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.propertymodelgroup.PropertyModelGroupResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyModelGroupService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
public class PropertyModelGroupServiceImpl implements PropertyModelGroupService, InitializingBean {
    private String appKey = null;
    private String appSecret = null;

    @Autowired
    private TbPropertyModelGroupMapper tbPropertyModelGroupMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FileConfig fileConfig;

    @Override
    public void afterPropertiesSet() {
        appKey = fileConfig.getAuthAppKey();
        appSecret = fileConfig.getAuthAppSecret();
    }

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
        ResultWrapper<Object> resultWrapper = userService.queryUserIDName(queryUserIdNameParameter, appKey, appSecret);
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

    @Override
    public Object deletePropertyModelGroup(DeletePropertyModelGroupParam deletePropertyModelGroupParam) {
        return tbPropertyModelGroupMapper.deleteBatchIds(deletePropertyModelGroupParam.getIDList());
    }

    @Override
    public Object queryPropertyModelGroupList(QueryPropertyModelGroupListParam queryPropertyModelGroupListParam) {
        LambdaQueryWrapper<TbPropertyModelGroup> queryWrapper = new QueryWrapper<TbPropertyModelGroup>().lambda()
                .eq(TbPropertyModelGroup::getCompanyID, queryPropertyModelGroupListParam.getCompanyID())
                .eq(TbPropertyModelGroup::getGroupType, queryPropertyModelGroupListParam.getGroupType())
                .eq(StringUtils.isNotEmpty(queryPropertyModelGroupListParam.getName()), TbPropertyModelGroup::getName, queryPropertyModelGroupListParam.getName());
        List<TbPropertyModelGroup> tbPropertyModelGroupList = tbPropertyModelGroupMapper.selectList(queryWrapper);
        if (CollectionUtil.isNullOrEmpty(tbPropertyModelGroupList)) {
            return Collections.emptyList();
        }

        List<PropertyModelGroupResponse> propertyModelGroupResponseList = new ArrayList<>();
        List<Integer> userIdList = tbPropertyModelGroupList.stream().map(TbPropertyModelGroup::getCreateUserID).collect(Collectors.toList());
        ResultWrapper<Object> resultWrapper = userService.queryUserIDName(new QueryUserIDNameParameter(userIdList), appKey, appSecret);
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
        return propertyModelGroupResponseList;
    }

}
