package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtWarnStatusDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class UpdateWtEngineParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @JsonIgnore
    public TbWarnRule tbWarnRule;
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
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    @Min(value = 1, message = "监测项目ID不能小于1")
    private Integer monitorItemID;
    @Min(value = 1, message = "监测点位ID不能小于1")
    private Integer monitorPointID;
    private String productID;
    private String deviceCSV;
    private List<WtWarnStatusDetailInfo> dataList;

    public TbWarnRule build(UpdateWtEngineParam param) {
        TbWarnRule res = new TbWarnRule();
        res.setID(param.getEngineID());
        res.setName(param.getEngineName());
        res.setDesc(param.getEngineDesc());
        res.setMonitorItemID(param.getMonitorItemID());
        res.setMonitorPointID(param.getMonitorPointID());
        res.setProjectID(param.getProjectID());
        if (StringUtils.isNotBlank(productID) && StringUtils.isNotBlank(deviceCSV)) {
            if (tbWarnRule.getRuleType().intValue() == 2) {
                res.setVideoType(productID);
                res.setVideoCSV(deviceCSV);
            } else if (tbWarnRule.getRuleType().intValue() == 3) {
                res.setProductID(Integer.valueOf(productID));
                res.setDeviceCSV(deviceCSV);
            }
        }
        return res;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper validate() {
        TbWarnRuleMapper tbWarnRuleMapper = ContextHolder.getBean(TbWarnRuleMapper.class);
        tbWarnRule = tbWarnRuleMapper.selectById(engineID);
        if (tbWarnRule == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "引擎不存在");
        }
        if (Objects.nonNull(projectID)) {
            TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
            if (tbProjectInfoMapper.selectCount(new LambdaQueryWrapper<TbProjectInfo>()
                    .eq(TbProjectInfo::getID, projectID)) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
            }
        }
        if (Objects.nonNull(monitorItemID)) {
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            if (tbMonitorItemMapper.selectCount(new LambdaQueryWrapper<TbMonitorItem>()
                    .eq(TbMonitorItem::getID, monitorItemID)) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
            }
        }
        if (Objects.nonNull(monitorPointID)) {
            if (Objects.isNull(monitorItemID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "编辑监测点位时,必须传入相应的监测项目");
            }
            TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
            if (tbMonitorPointMapper.selectCount(new LambdaQueryWrapper<TbMonitorPoint>()
                    .eq(TbMonitorPoint::getID, monitorPointID).eq(TbMonitorPoint::getMonitorItemID, monitorItemID)) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点位不存在");
            }
        }
        if (!CollectionUtil.isNullOrEmpty(dataList)) {
            TbWarnTriggerMapper tbWarnTriggerMapper = ContextHolder.getBean(TbWarnTriggerMapper.class);
            Map<Integer, String> updatedWarnIDNameMap = tbWarnTriggerMapper.selectList(
                            new LambdaQueryWrapper<TbWarnTrigger>().eq(TbWarnTrigger::getRuleID, engineID)
                                    .select(TbWarnTrigger::getID, TbWarnTrigger::getWarnName))
                    .stream().collect(Collectors.toMap(TbWarnTrigger::getID, TbWarnTrigger::getWarnName));
            List<String> createWarnNameList = new ArrayList<>();

            // 可空ID -> warnID、actionID,若为空视为新增的 告警状态、动作,所以校验 规则、告警状态、动作 三者间关系时要排除新增的部分
            List<Tuple<Integer, Integer>> updateTriggerRuleIDList = new ArrayList<>();
            List<Tuple<Integer, Integer>> updatActionTriggerIDList = new ArrayList<>();
//            Set<Tuple<Integer, String>> fieldSet = new HashSet<>(); TODO 暂时对数据源不做校验
            Set<Integer> actionTypeSet = new HashSet<>();
            Tuple<Integer, String> checkTriggerTuple = new Tuple<>();
            Tuple<Integer, String> checkActionTuple = new Tuple<>();
            dataList.stream()
//                    .peek(u -> Optional.ofNullable(u.getFieldToken()).filter(FieldShowUtil::notBelongSpecialType).ifPresent()) TODO 暂时对数据源不做校验
                    .peek(u -> {
                        Integer warnID = u.getWarnID();
                        String warnName = u.getWarnName();
                        if (Objects.isNull(warnID)) {
                            try {
                                Assert.notEmpty(warnName, "请输入报警名称");
                                Assert.notNull(u.getWarnLevel(), "请选择报警等级");
                                Assert.checkBetween(u.getWarnLevel(), 1, 4, "报警等级不合法");
                                Assert.notNull(u.getFieldToken(), "请选择源数据");
                                Assert.notEmpty(u.getCompareRule(), "请选择比较区间");
                                Assert.notEmpty(u.getTriggerRule(), "请完善误判过滤条件");
                            } catch (Throwable e) {
                                if (Objects.isNull(checkTriggerTuple.getItem1())) {
                                    checkTriggerTuple.setItem1(1);
                                    checkTriggerTuple.setItem2(e.getMessage());
                                }
                            }
                            createWarnNameList.add(warnName);
                        } else {
                            Optional.ofNullable(warnName).ifPresent(n -> updatedWarnIDNameMap.put(warnID, n));
                            updateTriggerRuleIDList.add(new Tuple<>(warnID, engineID));
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
                                    updatActionTriggerIDList.add(new Tuple<>(actionID, u.getWarnID()));
                                }
                                Optional.ofNullable(actionType).ifPresent(actionTypeSet::add);
                            }).toList();
                        });
                    }).toList();

            if (createWarnNameList.stream().distinct().toList().size() != createWarnNameList.size()
                    || createWarnNameList.stream().anyMatch(updatedWarnIDNameMap::containsValue)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一源数据不同报警状态，报警名称不能重复");
            }
            if (checkTriggerTuple.getItem1() != null && checkTriggerTuple.getItem1() == 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, checkTriggerTuple.getItem2());
            }
            if (checkActionTuple.getItem1() != null && checkActionTuple.getItem1() == 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, checkActionTuple.getItem2());
            }
            if (actionTypeSet.stream().anyMatch(u -> u < 1 || u > 4)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有动作类型不合法");
            }
//            TODO 暂时对数据源不做校验
//            if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(fieldSet)) {
//                TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
//                if (fieldSet.size() != tbMonitorTypeFieldMapper.selectCount(
//                        new LambdaQueryWrapper<TbMonitorTypeField>().in(TbMonitorTypeField::getID, fieldSet))) {
//                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有数据源不存在");
//                }
//            }
            if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(updateTriggerRuleIDList)) {
                if (updateTriggerRuleIDList.size() != tbWarnTriggerMapper.selectRuleTriggerCount(updateTriggerRuleIDList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有需修改的告警状态不合法");
                }
            }
            if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(updatActionTriggerIDList)) {
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
