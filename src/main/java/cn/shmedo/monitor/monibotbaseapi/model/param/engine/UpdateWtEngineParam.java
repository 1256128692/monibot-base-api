package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnActionMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnRuleMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnTriggerMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtWarnStatusDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.*;

@Data
public class UpdateWtEngineParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "引擎ID不能为空")
    @Min(value = 1, message = "引擎ID不能小于1")
    private Integer engineID;
    @Size(min = 1, max = 30, message = "引擎名称必须在1~30字之间")
    private String engineName;
    @Size(min = 1, max = 200, message = "引擎简介必须在1~200字之间")
    private String engineDesc;
    private List<WtWarnStatusDetailInfo> dataList;

    public static TbWarnRule build(UpdateWtEngineParam param) {
        TbWarnRule res = new TbWarnRule();
        res.setID(param.getEngineID());
        res.setName(param.getEngineName());
        res.setDesc(param.getEngineDesc());
        return res;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper validate() {
        TbWarnRuleMapper tbWarnRuleMapper = ContextHolder.getBean(TbWarnRuleMapper.class);
        if (tbWarnRuleMapper.selectOne(new LambdaQueryWrapper<TbWarnRule>().eq(TbWarnRule::getID, engineID)
                .select(TbWarnRule::getID)) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "引擎不存在");
        }
        if (!CollectionUtil.isNullOrEmpty(dataList)) {
            // 可空ID -> warnID、actionID,若为空视为新增的 告警状态、动作,所以校验 规则、告警状态、动作 三者间关系时要排除新增的部分
            List<Tuple<Integer, Integer>> updateTriggerRuleIDList = new ArrayList<>();
            List<Tuple<Integer, Integer>> updatActionTriggerIDList = new ArrayList<>();
            Set<Integer> fieldSet = new HashSet<>();
            Set<Integer> actionTypeSet = new HashSet<>();
            Tuple<Integer, String> checkTriggerTuple = new Tuple<>();
            Tuple<Integer, String> checkActionTuple = new Tuple<>();
            dataList.stream().peek(u -> Optional.ofNullable(u.getMetadataID()).ifPresent(fieldSet::add))
                    .peek(u -> {
                        if (Objects.isNull(u.getWarnID())) {
                            try {
                                Assert.notEmpty(u.getWarnName(), "请输入报警名称");
                                Assert.notNull(u.getWarnLevel(), "请输入报警等级");
                                Assert.checkBetween(u.getWarnLevel(), 1, 4, "报警等级不合法");
                                Assert.notNull(u.getMetadataID(), "请选择源数据");
                                Assert.notEmpty(u.getCompareRule(), "请选择比较区间");
                                Assert.notEmpty(u.getTriggerRule(), "请完善误判过滤条件");
                            } catch (Throwable e) {
                                if (Objects.isNull(checkTriggerTuple.getItem1())) {
                                    checkTriggerTuple.setItem1(1);
                                    checkTriggerTuple.setItem2(e.getMessage());
                                }
                            }
                        } else {
                            updateTriggerRuleIDList.add(new Tuple<>(u.getWarnID(), engineID));
                        }
                        Optional.ofNullable(u.getAction()).filter(s -> !CollectionUtil.isNullOrEmpty(s)).ifPresent(w -> {
                            w.stream().peek(m -> {
                                Integer actionID = m.getID();
                                Integer actionType = m.getActionType();
                                if (Objects.isNull(actionID)) {
                                    try {
                                        Assert.notNull(actionType, "动作类型不能为空");
                                        if (actionType == 2) {
                                            Assert.notEmpty(m.getDesc(), "解决方案说明不能为空");
                                        } else if (actionType == 3) {
                                            Assert.notEmpty(m.getActionTarget(), "短信推送设置不能为空");
                                        }
                                    } catch (Throwable e) {
                                        if (Objects.isNull(checkActionTuple.getItem1())) {
                                            checkActionTuple.setItem1(1);
                                            checkActionTuple.setItem2(e.getMessage());
                                        }
                                    }
                                } else {
                                    updatActionTriggerIDList.add(new Tuple<>(actionID, m.getTriggerID()));
                                }
                                Optional.ofNullable(actionType).ifPresent(actionTypeSet::add);
                            });
                        });
                    }).toList();

            if (checkTriggerTuple.getItem1() != null && checkTriggerTuple.getItem1() == 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, checkTriggerTuple.getItem2());
            }
            if (checkActionTuple.getItem1() != null && checkActionTuple.getItem1() == 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, checkActionTuple.getItem2());
            }
            if (actionTypeSet.stream().anyMatch(u -> u < 1 || u > 4)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有动作类型不合法");
            }
            if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(fieldSet)) {
                TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
                if (fieldSet.size() != tbMonitorTypeFieldMapper.selectCount(
                        new LambdaQueryWrapper<TbMonitorTypeField>().in(TbMonitorTypeField::getID, fieldSet))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有数据源不存在");
                }
            }
            if (ObjectUtil.isNotEmpty(updateTriggerRuleIDList)) {
                TbWarnTriggerMapper tbWarnTriggerMapper = ContextHolder.getBean(TbWarnTriggerMapper.class);
                if (updateTriggerRuleIDList.size() != tbWarnTriggerMapper.selectRuleTriggerCount(updateTriggerRuleIDList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有需修改的告警状态不合法");
                }
            }
            if (ObjectUtil.isNotEmpty(updatActionTriggerIDList)) {
                TbWarnActionMapper tbWarnActionMapper = ContextHolder.getBean(TbWarnActionMapper.class);
                if (updatActionTriggerIDList.size() != tbWarnActionMapper.selectTriggerActionCount(updatActionTriggerIDList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有需修改的动作不合法");
                }
            }
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
