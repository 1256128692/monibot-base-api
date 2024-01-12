package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.NotifyType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.DataWarnNotifyLevelCheck;
import cn.shmedo.monitor.monibotbaseapi.model.standard.INotifyConfigTargetCheck;
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
public class AddWarnNotifyConfigParam extends CompanyPlatformParam implements INotifyConfigTargetCheck, DataWarnNotifyLevelCheck {
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
        if (NotifyType.DATA_NOTIFY.getCode().equals(notifyType)) {
            if (CollUtil.isEmpty(warnLevel)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警等级配置不能为空");
            }
            ResultWrapper<?> valid = warnLevelValid(companyID, platform, t -> tbWarnNotifyConfig.setWarnLevel(JSONUtil.toJsonStr(new HashSet<>(t))));
            if (Objects.nonNull(valid)) {
                return valid;
            }
        }
        if (CollUtil.isNotEmpty(roleList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目前暂不支持添加角色");
        }
        if (CollUtil.isEmpty(deptList) && CollUtil.isEmpty(userList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "必须选择通知对象");
        }
        return valid(depts -> tbWarnNotifyConfig.setDepts(JSONUtil.toJsonStr(depts)),
                users -> tbWarnNotifyConfig.setUsers(JSONUtil.toJsonStr(users)), ex -> tbWarnNotifyConfig.setExValue(ex));
    }
}
