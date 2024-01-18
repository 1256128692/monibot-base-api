package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnLevelAliasInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 10:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateThresholdBaseConfigParam extends QueryThresholdBaseConfigParam {
    @Range(min = 1, max = 2, message = "触发设置类型 1.有数据满足规则,直接触发对应等级报警 2.有连续n次数据满足规则,再触发对应等级报警")
    private Integer triggerType;
    private Integer triggerTimes;
    private List<@Valid FieldWarnAlias> aliasConfigList;

    @JsonIgnore
    private TbTriggerConfig tbTriggerConfig = null;
    @JsonIgnore
    private List<TbWarnLevelAlias> tbWarnLevelAliasList;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        final Date current = new Date();
        final Integer platform = getPlatform();
        final Integer projectID = getProjectID();
        final Integer monitorItemID = getMonitorItemID();

        // trigger config
        if (Objects.nonNull(triggerType)) {
            if (triggerType == 1) {
                triggerTimes = -1;
            }
            if (Objects.isNull(triggerTimes)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警触发配置不合法");
            }
            if (triggerType == 2 && (triggerTimes < 2 || triggerTimes > 99)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "请输入“2~99”间的正整数");
            }
            List<TbTriggerConfig> tbTriggerConfigList = ContextHolder.getBean(TbTriggerConfigMapper.class)
                    .selectList(new LambdaQueryWrapper<TbTriggerConfig>().eq(TbTriggerConfig::getProjectID, projectID)
                            .eq(TbTriggerConfig::getPlatform, platform).eq(TbTriggerConfig::getMonitorItemID, monitorItemID));
            tbTriggerConfig = CollUtil.isNotEmpty(tbTriggerConfigList) ? tbTriggerConfigList.stream().findAny().orElseThrow() :
                    new TbTriggerConfig(null, projectID, platform, monitorItemID, null, null, current, null, null);
            tbTriggerConfig.setTriggerTimes(triggerTimes);
            tbTriggerConfig.setUpdateTime(current);
        }

        // warn level alias config
        if (CollUtil.isNotEmpty(aliasConfigList)) {
            final Integer monitorType = getMonitorType();
            Set<Integer> warnLevelSet = ContextHolder.getBean(ITbWarnBaseConfigService.class).getWarnLevelSet(getCompanyID(), platform);
            Set<Integer> fieldIDSet = ContextHolder.getBean(TbMonitorItemFieldMapper.class).selectList(
                            new LambdaQueryWrapper<TbMonitorItemField>().eq(TbMonitorItemField::getMonitorItemID, monitorItemID))
                    .stream().map(TbMonitorItemField::getMonitorTypeFieldID).collect(Collectors.toSet());
            if (aliasConfigList.stream().anyMatch(u -> !fieldIDSet.contains(u.getFieldID())) ||
                    aliasConfigList.stream().anyMatch(u -> u.getDataList().stream().anyMatch(w -> !warnLevelSet.contains(w.getWarnLevel())))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "别名配置列表不合法");
            }
            tbWarnLevelAliasList = aliasConfigList.stream().map(u -> {
                Integer fieldID = u.getFieldID();
                return u.getDataList().stream().map(w -> new TbWarnLevelAlias(
                        null, w.getAlias(), platform, projectID, monitorType, monitorItemID, fieldID, w.getWarnLevel(),
                        null, current, null, current)).toList();
            }).flatMap(Collection::stream).toList();

            // check repeated
            if (tbWarnLevelAliasList.stream().map(UpdateThresholdBaseConfigParam::combineWarnLevelAliasKey).distinct().toList().size() != tbWarnLevelAliasList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "不允许重复添加别名");
            }

            // query which records needs to be updated.
            Map<String, TbWarnLevelAlias> tbWarnLevelAliasMap = ContextHolder.getBean(TbWarnLevelAliasMapper.class)
                    .selectList(new LambdaQueryWrapper<TbWarnLevelAlias>().and(wrapper -> tbWarnLevelAliasList.stream()
                            .peek(u -> wrapper.or(w -> w.eq(TbWarnLevelAlias::getPlatform, u.getPlatform())
                                    .eq(TbWarnLevelAlias::getMonitorItemID, u.getMonitorItemID())
                                    .eq(TbWarnLevelAlias::getFieldID, u.getFieldID())
                                    .eq(TbWarnLevelAlias::getWarnLevel, u.getWarnLevel()))).toList())).stream()
                    .collect(Collectors.toMap(UpdateThresholdBaseConfigParam::combineWarnLevelAliasKey, Function.identity()));
            if (CollUtil.isNotEmpty(tbWarnLevelAliasMap)) {
                tbWarnLevelAliasList.stream().filter(u -> tbWarnLevelAliasMap.containsKey(combineWarnLevelAliasKey(u)))
                        .peek(u -> Optional.of(tbWarnLevelAliasMap).map(w -> w.get(combineWarnLevelAliasKey(u))).ifPresent(w -> {
                            u.setId(w.getId());
                            u.setCreateTime(w.getCreateTime());
                            u.setCreateUserID(w.getCreateUserID());
                        })).toList();
            }
        }
        return null;
    }

    @Data
    private final static class FieldWarnAlias {
        @NotNull(message = "属性ID不能为空")
        @Positive(message = "属性ID必须为正值")
        private Integer fieldID;
        private List<@Valid WarnLevelAliasInfo> dataList;
    }

    /**
     * (non-Javadoc)
     *
     * @see #combineWarnLevelAliasKey(Integer, Integer, Integer, Integer)
     */
    private static String combineWarnLevelAliasKey(@NotNull final TbWarnLevelAlias alias) {
        return combineWarnLevelAliasKey(alias.getPlatform(), alias.getMonitorItemID(), alias.getFieldID(), alias.getWarnLevel());
    }

    /**
     * combine to a unique key.
     */
    private static String combineWarnLevelAliasKey(final Integer platform, final Integer monitorItemID,
                                                   final Integer fieldID, final Integer warnLevel) {
        return platform + "@@" + monitorItemID + "@@" + fieldID + "@@" + warnLevel;
    }
}
