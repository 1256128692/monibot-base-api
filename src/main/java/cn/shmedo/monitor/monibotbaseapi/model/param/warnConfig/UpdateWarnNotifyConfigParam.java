package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbNotifyConfigProjectRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyConfigProjectRelation;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.NotifyType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IDataWarnNotifyLevelCheck;
import cn.shmedo.monitor.monibotbaseapi.model.standard.INotifyConfigTargetCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 12:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnNotifyConfigParam extends QueryWarnNotifyConfigDetailParam implements INotifyConfigTargetCheck, IDataWarnNotifyLevelCheck {
    private Boolean allProject;
    private List<Integer> projectIDList;
    private List<Integer> warnLevel;
    private List<@Valid @Range(min = 1, max = 3, message = "通知方式(多选),枚举值: 1.平台消息 2.短信 3.邮件") Integer> notifyMethod;
    private List<Integer> deptList;
    private List<Integer> userList;
    private List<Integer> roleList;
    private String exValue;
    @JsonIgnore
    private final CompanyPlatformParam companyPlatformParam = new CompanyPlatformParam();
    @JsonIgnore
    private TbWarnNotifyConfig tbWarnNotifyConfig;
    @JsonIgnore
    private List<TbNotifyConfigProjectRelation> updateRelationList;

    @Override
    public ResultWrapper<?> validate() {
        final Integer companyID = getCompanyID();
        final Integer platform = getPlatform();
        final Integer notifyConfigID = getNotifyConfigID();
        companyPlatformParam.setCompanyID(companyID);
        companyPlatformParam.setPlatform(platform);
        ResultWrapper<?> validate = companyPlatformParam.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        List<TbWarnNotifyConfig> tbWarnNotifyConfigList = ContextHolder.getBean(TbWarnNotifyConfigMapper.class)
                .selectList(new LambdaQueryWrapper<TbWarnNotifyConfig>().eq(TbWarnNotifyConfig::getCompanyID, companyID)
                        .eq(TbWarnNotifyConfig::getPlatform, platform).eq(TbWarnNotifyConfig::getId, notifyConfigID));
        if (CollUtil.isEmpty(tbWarnNotifyConfigList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警通知配置不存在");
        }
        tbWarnNotifyConfig = tbWarnNotifyConfigList.stream().findAny().orElseThrow();
        tbWarnNotifyConfig.setUpdateTime(new Date());
        if (CollUtil.isNotEmpty(roleList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目前暂不支持添加角色");
        }
        Optional.ofNullable(notifyMethod).map(HashSet::new).filter(CollUtil::isNotEmpty).map(JSONUtil::toJsonStr).ifPresent(tbWarnNotifyConfig::setNotifyMethod);
        if (CollUtil.isNotEmpty(warnLevel)) {
            if (!NotifyType.DATA_NOTIFY.getCode().equals(tbWarnNotifyConfig.getNotifyType())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "只有数据报警可以配置报警等级枚举key");
            }
            ResultWrapper<?> valid = warnLevelValid(companyID, platform, t -> tbWarnNotifyConfig.setWarnLevel(JSONUtil.toJsonStr(new HashSet<>(t))));
            if (Objects.nonNull(valid)) {
                return valid;
            }
        }
        if (Objects.nonNull(allProject) || Objects.nonNull(projectIDList)) {
            if (CollUtil.isNotEmpty(projectIDList) && ContextHolder.getBean(TbProjectInfoMapper.class)
                    .selectCount(new LambdaQueryWrapper<TbProjectInfo>().in(TbProjectInfo::getID, projectIDList)) != projectIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有工程ID不存在");
            }
            if (Objects.nonNull(allProject) && allProject) {
                projectIDList = List.of(-1);
            }
            projectIDList = Objects.nonNull(projectIDList) ? projectIDList.stream().sorted().toList() : null;
            List<Integer> dbProjectList = ContextHolder.getBean(TbNotifyConfigProjectRelationMapper.class)
                    .selectList(new LambdaQueryWrapper<TbNotifyConfigProjectRelation>()
                            .eq(TbNotifyConfigProjectRelation::getNotifyConfigID, notifyConfigID))
                    .stream().map(TbNotifyConfigProjectRelation::getProjectID).sorted().toList();
            if (!CollUtil.isEqualList(projectIDList, dbProjectList)) {
                updateRelationList = projectIDList.stream().map(u -> new TbNotifyConfigProjectRelation(null, u, notifyConfigID)).toList();
            }
        }
        if (CollUtil.isEmpty(deptList) && CollUtil.isEmpty(userList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "必须选择通知对象");
        }
        tbWarnNotifyConfig.setDepts(JSONUtil.toJsonStr(deptList));
        tbWarnNotifyConfig.setUsers(JSONUtil.toJsonStr(userList));
        return validTarget(null, null, ex -> tbWarnNotifyConfig.setExValue(ex));
    }

    /**
     * prohibit acquire {@code companyPlatformParam}.
     */
    private CompanyPlatformParam getCompanyPlatformParam() {
        return null;
    }
}
