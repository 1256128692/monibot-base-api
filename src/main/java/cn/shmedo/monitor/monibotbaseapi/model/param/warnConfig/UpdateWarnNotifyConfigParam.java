package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.NotifyType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.DataWarnNotifyLevelCheck;
import cn.shmedo.monitor.monibotbaseapi.model.standard.INotifyConfigTargetCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 12:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnNotifyConfigParam extends QueryWarnNotifyConfigDetailParam implements INotifyConfigTargetCheck, DataWarnNotifyLevelCheck {
    private Boolean allProject;
    private List<Integer> projectIDList;
    private List<Integer> warnLevel;
    private List<@Valid @Range(min = 1, max = 2, message = "通知方式(多选),枚举值: 1.平台消息 2.短信") Integer> notifyMethod;
    private List<Integer> deptList;
    private List<Integer> userList;
    private List<Integer> roleList;
    private String exValue;
    @JsonIgnore
    private final CompanyPlatformParam companyPlatformParam = new CompanyPlatformParam();
    @JsonIgnore
    private TbWarnNotifyConfig tbWarnNotifyConfig;

    @Override
    public ResultWrapper<?> validate() {
        final Integer companyID = getCompanyID();
        final Integer platform = getPlatform();
        companyPlatformParam.setCompanyID(companyID);
        companyPlatformParam.setPlatform(platform);
        ResultWrapper<?> validate = companyPlatformParam.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        List<TbWarnNotifyConfig> tbWarnNotifyConfigList = ContextHolder.getBean(TbWarnNotifyConfigMapper.class)
                .selectBatchIds(List.of(getNotifyConfigID()));
        if (CollUtil.isEmpty(tbWarnNotifyConfigList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警通知配置不存在");
        }
        tbWarnNotifyConfig = tbWarnNotifyConfigList.stream().findAny().orElseThrow();
        tbWarnNotifyConfig.setUpdateUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        tbWarnNotifyConfig.setUpdateTime(new Date());
        if (CollUtil.isNotEmpty(roleList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目前暂不支持添加角色");
        }
        Optional.ofNullable(notifyMethod).map(HashSet::new).map(JSONUtil::toJsonStr).ifPresent(tbWarnNotifyConfig::setNotifyMethod);
        if (CollUtil.isNotEmpty(warnLevel)) {
            if (!NotifyType.DATA_NOTIFY.getCode().equals(tbWarnNotifyConfig.getNotifyType())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "只有数据报警可以配置报警等级枚举key");
            }
            ResultWrapper<?> valid = warnLevelValid(companyID, platform, t -> tbWarnNotifyConfig.setWarnLevel(JSONUtil.toJsonStr(new HashSet<>(t))));
            if (Objects.nonNull(valid)) {
                return valid;
            }
        }
        projectIDList = Optional.ofNullable(projectIDList).map(u -> u.stream().distinct().collect(Collectors.toList())).orElse(null);
        if (CollUtil.isNotEmpty(projectIDList) && ContextHolder.getBean(TbProjectInfoMapper.class)
                .selectCount(new LambdaQueryWrapper<TbProjectInfo>().in(TbProjectInfo::getID, projectIDList)) != projectIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有工程ID不存在");
        }
        return valid(depts -> tbWarnNotifyConfig.setDepts(JSONUtil.toJsonStr(depts)),
                users -> tbWarnNotifyConfig.setUsers(JSONUtil.toJsonStr(users)), ex -> tbWarnNotifyConfig.setExValue(ex));
    }

    /**
     * prohibit acquire {@code companyPlatformParam}.
     */
    private CompanyPlatformParam getCompanyPlatformParam() {
        return null;
    }
}
