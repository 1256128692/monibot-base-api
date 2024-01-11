package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnLevelType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryDeptSimpleListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserNoPageParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 11:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddWarnNotifyConfigParam extends CompanyPlatformParam {
    private Boolean allProject;
    @Size(min = 1, message = "选中的工程ID数不能小于1")
    private List<Integer> projectIDList;
    @NotNull(message = "通知配置类型不能为空")
    @Range(min = 1, max = 2, message = "通知配置类型 1.设备报警通知 2.数据报警通知")
    private Integer notifyType;
    @NotEmpty(message = "通知方式不能为空")
    private List<@Valid @Range(min = 1, max = 2, message = "通知方式(多选),枚举值: 1.平台消息 2.短信") Integer> notifyMethod;
    private List<Integer> warnLevel;
    private List<Integer> deptList;
    private List<Integer> userList;
    private List<Integer> roleList;
    private String exValue;
    @JsonIgnore
    private TbWarnNotifyConfig tbWarnNotifyConfig;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        final Integer companyID = getCompanyID();
        final Integer platform = getPlatform();
        final Date current = new Date();
        final Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        // Set time here to avoid using database default timezone.
        tbWarnNotifyConfig = new TbWarnNotifyConfig();
        tbWarnNotifyConfig.setCreateUserID(userID);
        tbWarnNotifyConfig.setUpdateUserID(userID);
        tbWarnNotifyConfig.setCreateTime(current);
        tbWarnNotifyConfig.setUpdateTime(current);
        tbWarnNotifyConfig.setCompanyID(companyID);
        tbWarnNotifyConfig.setPlatform(platform);
        tbWarnNotifyConfig.setNotifyMethod(JSONUtil.toJsonStr(new HashSet<>(notifyMethod)));
        tbWarnNotifyConfig.setNotifyType(notifyType);
        projectIDList = Optional.ofNullable(projectIDList).map(u -> u.stream().distinct().collect(Collectors.toList())).orElse(null);
        if ((Objects.isNull(allProject) || !allProject) && CollUtil.isEmpty(projectIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程ID不能为空");
        }
        if (CollUtil.isNotEmpty(projectIDList) && ContextHolder.getBean(TbProjectInfoMapper.class)
                .selectCount(new LambdaQueryWrapper<TbProjectInfo>().in(TbProjectInfo::getID, projectIDList)) != projectIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有工程ID不存在");
        }
        if (notifyType == 2) {
            if (CollUtil.isEmpty(warnLevel)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警等级配置不能为空");
            }
            TbWarnBaseConfig tbWarnBaseConfig = ContextHolder.getBean(ITbWarnBaseConfigService.class).queryByCompanyIDAndPlatform(companyID, platform);
            Set<Integer> warnLevelSet = DataWarnLevelType.fromCode(tbWarnBaseConfig.getWarnLevelType()).getWarnLevelSet();
            if (warnLevel.stream().anyMatch(u -> !warnLevelSet.contains(u))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警等级配置不合法");
            }
            tbWarnNotifyConfig.setWarnLevel(JSONUtil.toJsonStr(new HashSet<>(warnLevel)));
        }
        if (CollUtil.isNotEmpty(roleList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目前暂不支持添加角色");
        }
        if (CollUtil.isEmpty(deptList) && CollUtil.isEmpty(userList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "必须选择通知对象");
        }
        deptList = Optional.ofNullable(deptList).filter(CollUtil::isNotEmpty).map(u -> u.stream().distinct().toList()).orElse(null);
        userList = Optional.ofNullable(userList).filter(CollUtil::isNotEmpty).map(u -> u.stream().distinct().toList()).orElse(null);
        UserService userService = ContextHolder.getBean(UserService.class);
        FileConfig fileConfig = ContextHolder.getBean(FileConfig.class);
        String authAppKey = fileConfig.getAuthAppKey();
        String authAppSecret = fileConfig.getAuthAppSecret();
        if (Optional.ofNullable(deptList).filter(CollUtil::isNotEmpty).map(u -> {
                    tbWarnNotifyConfig.setDepts(JSONUtil.toJsonStr(u));
                    return new QueryDeptSimpleListParam(companyID, u);
                }).map(u -> userService.queryDeptSimpleList(u, authAppKey, authAppSecret)).filter(ResultWrapper::apiSuccess)
                .map(ResultWrapper::getData).map(u -> !Objects.equals(u.size(), deptList.size())).orElse(false)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有部门不存在");
        }
        if (Optional.ofNullable(userList).filter(CollUtil::isNotEmpty).map(u -> {
                    tbWarnNotifyConfig.setUsers(JSONUtil.toJsonStr(u));
                    QueryUserNoPageParam param = new QueryUserNoPageParam();
                    param.setCompanyID(companyID);
                    param.setUserIDList(u);
                    param.setIncludeExternal(true);
                    return param;
                }).map(u -> userService.queryUserNoPage(u, authAppKey, authAppSecret)).filter(ResultWrapper::apiSuccess)
                .map(ResultWrapper::getData).map(u -> !Objects.equals(u.size(), userList.size())).orElse(false)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有用户不存在");
        }
        if (ObjectUtil.isNotEmpty(exValue)) {
            try {
                JSONArray array = JSONUtil.parseArray(exValue);
            } catch (JSONException e) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "配置的扩展信息不合法");
            }
            tbWarnNotifyConfig.setExValue(exValue);
        }
        return null;
    }
}
