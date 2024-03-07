package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbNotifyConfigProjectRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserContact;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.NotifyType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryDepartmentIncludeUserInfoListParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserContactParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserInDeptListNoPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.CompanyPlatformParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnNotifyConfigDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.UpdateWarnNotifyConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryProjectBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DepartmentIncludeUserInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserNoPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.DataWarnConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.DeviceWarnConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
@Service
@RequiredArgsConstructor
public class TbWarnNotifyConfigServiceImpl extends ServiceImpl<TbWarnNotifyConfigMapper, TbWarnNotifyConfig> implements ITbWarnNotifyConfigService {
    private final UserService userService;
    private final FileConfig fileConfig;
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbNotifyConfigProjectRelationMapper tbNotifyConfigProjectRelationMapper;

    @Override
    public WarnNotifyConfigDetail queryWarnNotifyConfigDetail(QueryWarnNotifyConfigDetailParam param) {
        Integer companyID = param.getCompanyID();
        WarnNotifyConfigDetail detail = param.getDetail();
        Optional.ofNullable(detail.getUserIDStr()).filter(JSONUtil::isTypeJSONArray).map(JSONUtil::parseArray)
                .map(u -> JSONUtil.toList(u, Integer.class)).filter(CollUtil::isNotEmpty).ifPresent(u -> {
                    Set<Integer> externalUserIDSet = Optional.of(new QueryUserInDeptListNoPageParam(companyID, null, null, null, null, null, true, u, null))
                            .map(w -> userService.queryUserInDeptListNoPage(w, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                            .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).map(w ->
                                    w.stream().map(UserNoPageInfo::getUser).filter(s -> !companyID.equals(s.getCompanyID()))
                                            .map(UserNoPageInfo.TbUserEx::getUserID).collect(Collectors.toSet())).orElse(Set.of());
                    detail.setExternalIDList(u.stream().filter(externalUserIDSet::contains).toList());
                    detail.setUserIDList(u.stream().filter(w -> !externalUserIDSet.contains(w)).toList());
                });
        Optional.ofNullable(detail.getProjectIDStr()).filter(JSONUtil::isTypeJSONArray).map(JSONUtil::parseArray)
                .map(u -> JSONUtil.toList(u, Integer.class)).filter(CollUtil::isNotEmpty).ifPresent(u -> detail
                        .setProjectIDList(u.size() > 1 || u.stream().findAny().orElse(0) != -1 ? u :
                                tbProjectInfoMapper.selectListByCompanyIDAndProjectIDList(companyID, null)
                                        .stream().map(TbProjectInfo::getID).toList()));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWarnNotifyConfig(TbWarnNotifyConfig tbWarnNotifyConfig, List<Integer> projectIDList, Integer userID) {
        tbWarnNotifyConfig.setCreateUserID(userID);
        tbWarnNotifyConfig.setUpdateUserID(userID);
        this.save(tbWarnNotifyConfig);
        final Integer notifyConfigId = tbWarnNotifyConfig.getId();
        List<TbNotifyConfigProjectRelation> list = projectIDList.stream().map(u -> new TbNotifyConfigProjectRelation(null, u, notifyConfigId)).toList();
        tbNotifyConfigProjectRelationMapper.insertBatchSomeColumn(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarnNotifyConfig(UpdateWarnNotifyConfigParam param, Integer userID) {
        List<TbNotifyConfigProjectRelation> updateRelationList = param.getUpdateRelationList();
        if (CollUtil.isNotEmpty(updateRelationList)) {
            tbNotifyConfigProjectRelationMapper.delete(new LambdaQueryWrapper<TbNotifyConfigProjectRelation>()
                    .eq(TbNotifyConfigProjectRelation::getNotifyConfigID, param.getNotifyConfigID()));
            tbNotifyConfigProjectRelationMapper.insertBatchSomeColumn(updateRelationList);
        }
        TbWarnNotifyConfig tbWarnNotifyConfig = param.getTbWarnNotifyConfig();
        tbWarnNotifyConfig.setUpdateUserID(userID);
        updateById(tbWarnNotifyConfig);
    }

    @Override
    public WarnNotifyConfigInfo queryWarnNotifyConfigList(CompanyPlatformParam param, TbWarnBaseConfig tbWarnBaseConfig) {
        Integer companyID = param.getCompanyID();
        WarnNotifyConfigInfo info = new WarnNotifyConfigInfo();
        BeanUtil.copyProperties(tbWarnBaseConfig, info);
        // set Projects
        setWarnNotifyConfigProjectInfo(info, companyID, param.getPlatform(), param.getProjectID());
        // set Users
        setWarnNotifyConfigUserInfo(info, companyID);
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWarnNotifyConfigBatch(List<Integer> notifyConfigIDList) {
        tbNotifyConfigProjectRelationMapper.delete(new LambdaQueryWrapper<TbNotifyConfigProjectRelation>()
                .in(TbNotifyConfigProjectRelation::getNotifyConfigID, notifyConfigIDList));
        removeBatchByIds(notifyConfigIDList);
    }

    @Override
    public WarnNotifyConfig queryByProjectIDAndPlatform(@Nonnull Integer projectID, @Nonnull Integer platform,
                                                        @Nonnull Integer notifyType) {
        TbWarnNotifyConfig config = baseMapper.queryByProjectIDAndPlatform(projectID, platform, notifyType);
        if (config != null) {
            ResultWrapper<Map<Integer, UserContact>> wrapper = userService.queryUserContact(QueryUserContactParam.builder()
                    .depts(JSONUtil.toList(config.getDepts(), Integer.class))
                    .roles(JSONUtil.toList(config.getRoles(), Integer.class))
                    .users(JSONUtil.toList(config.getUsers(), Integer.class))
                    .build(), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());

            WarnNotifyConfig item = new WarnNotifyConfig();
            item.setLevels(JSONUtil.toList(config.getWarnLevel(), Integer.class));
            item.setContacts(Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of()));
            item.setMethods(JSONUtil.toList(config.getNotifyMethod(), Integer.class));
            return item;
        }
        return null;
    }

    private void setWarnNotifyConfigProjectInfo(WarnNotifyConfigInfo info, final Integer companyID, final Integer platform,
                                                final Integer projectID) {
        Map<Integer, TbProjectInfo> allProjectIDMap = tbProjectInfoMapper.selectList(new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getCompanyID, companyID)).stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        List<QueryProjectBaseInfoResponse> allProjectList = allProjectIDMap.values().stream().map(u -> {
            QueryProjectBaseInfoResponse response = new QueryProjectBaseInfoResponse();
            response.setProjectID(u.getID());
            response.setProjectName(u.getProjectName());
            response.setProjectShortName(u.getShortName());
            return response;
        }).toList();
        List<DataWarnConfigInfo> deviceWarnConfigInfoList = baseMapper.selectWarnNotifyConfigList(
                        companyID, platform, projectID, NotifyType.DEVICE_NOTIFY.getCode())
                .stream().peek(u -> u.afterProperties(allProjectIDMap, allProjectList)).toList();
        List<DataWarnConfigInfo> dataWarnConfigInfoList = baseMapper.selectWarnNotifyConfigList(
                        companyID, platform, projectID, NotifyType.DATA_NOTIFY.getCode())
                .stream().peek(u -> u.afterProperties(allProjectIDMap, allProjectList)).toList();
        info.setDeviceWarnList(new ArrayList<>(deviceWarnConfigInfoList));
        info.setDataWarnList(dataWarnConfigInfoList);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setWarnNotifyConfigUserInfo(WarnNotifyConfigInfo info, final Integer companyID) {
        Map<Integer, UserNoPageInfo.TbUserEx> userMap = new HashMap<>();
        Map<Integer, Map<Integer, UserNoPageInfo.TbUserEx>> deptIDUsersMap = new HashMap<>();
        fillUserAndDeptMap(info.getDeviceWarnList(), userMap, deptIDUsersMap);
        fillUserAndDeptMap(info.getDataWarnList(), userMap, deptIDUsersMap);

        List<DepartmentIncludeUserInfo.DeptResponse> deptResponseList = Optional.of(deptIDUsersMap).filter(CollUtil::isNotEmpty)
                .map(u -> new QueryDepartmentIncludeUserInfoListParameter(companyID, null, 3, null))
                .map(u -> userService.queryDepartmentIncludeUserInfoList(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).map(DepartmentIncludeUserInfo::getDepts)
                .orElse(List.of());
        Optional.of(userMap.keySet()).filter(CollUtil::isNotEmpty).map(u -> {
                    QueryUserInDeptListNoPageParam userNoPageParam = new QueryUserInDeptListNoPageParam();
                    userNoPageParam.setCompanyID(companyID);
                    userNoPageParam.setIncludeExternal(true);
                    userNoPageParam.setUserIDList(new ArrayList<>(u));
                    return userNoPageParam;
                }).map(u -> userService.queryUserInDeptListNoPage(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).ifPresent(u ->
                        u.stream().map(UserNoPageInfo::getUser).peek(w -> userMap.putIfAbsent(w.getUserID(), w)).toList());
        info.setUserName(companyID, userMap, deptResponseList);
    }

    /**
     * filling users and dept info to relative {@link Map}.
     *
     * @param dataList       source
     * @param userMap        <pre>Map<{@code userId},{@code null}></pre>
     * @param deptIDUsersMap <pre>Map<{@code deptId},empty map of {@code Map<userId, userInfo>}></pre>
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void fillUserAndDeptMap(final List<? extends DeviceWarnConfigInfo> dataList,
                                    Map<Integer, UserNoPageInfo.TbUserEx> userMap,
                                    Map<Integer, Map<Integer, UserNoPageInfo.TbUserEx>> deptIDUsersMap) {
        dataList.stream().peek(u -> {
            Optional.ofNullable(u.getAllUserList()).ifPresent(w -> w.stream().peek(s -> userMap.putIfAbsent(s, null)).toList());
            Optional.ofNullable(u.getDeptList()).ifPresent(w -> w.stream().peek(s -> deptIDUsersMap.putIfAbsent(s, new HashMap<>())).toList());
        }).toList();
    }
}
