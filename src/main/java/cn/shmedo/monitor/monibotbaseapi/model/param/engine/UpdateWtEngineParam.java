package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

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
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtWarnStatusDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtWarnStatusInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

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
            List<Integer> fieldIDList = new ArrayList<>();
            // 可空ID -> warnID、actionID,若为空视为新增的 告警状态、动作,所以校验 规则、告警状态、动作 三者间关系时要排除新增的部分
            List<Tuple<Integer, Integer>> triggerRuleIDList = new ArrayList<>();
            List<Tuple<Integer, Integer>> actionTriggerIDList = dataList.stream()
                    .peek(u -> fieldIDList.add(u.getMetadataID()))
                    .filter(k -> Objects.nonNull(k.getWarnID()))
                    .peek(u -> triggerRuleIDList.add(new Tuple<>(u.getWarnID(), engineID)))
                    .map(s -> {
                        Integer warnID = s.getWarnID();
                        Map<Integer, Integer> map = new HashMap<>();
                        s.getAction().stream().map(TbWarnAction::getID).filter(Objects::nonNull)
                                .peek(w -> map.put(w, warnID)).collect(Collectors.toList());
                        return map;
                    }).map(Map::entrySet).flatMap(Collection::stream)
                    .map(w -> new Tuple<>(w.getKey(), w.getValue()))
                    .collect(Collectors.toList());
            List<Integer> actionTypeDistinct = dataList.stream().map(WtWarnStatusInfo::getAction)
                    .flatMap(Collection::stream).map(TbWarnAction::getActionType).distinct().toList();
            // multiple triggers maybe have the same datasource
            List<Integer> distinctFieldIDList = fieldIDList.stream().distinct().toList();
            if (actionTypeDistinct.stream().anyMatch(u -> u < 1 || u > 4)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有动作类型不合法");
            }
            if (!CollectionUtil.isNullOrEmpty(distinctFieldIDList)) {
                TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
                if (distinctFieldIDList.size() != tbMonitorTypeFieldMapper.selectCount(
                        new LambdaQueryWrapper<TbMonitorTypeField>().in(TbMonitorTypeField::getID, distinctFieldIDList))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有数据源不存在");
                }
            }
            if (ObjectUtil.isNotEmpty(triggerRuleIDList)) {
                TbWarnTriggerMapper tbWarnTriggerMapper = ContextHolder.getBean(TbWarnTriggerMapper.class);
                if (triggerRuleIDList.size() != tbWarnTriggerMapper.selectRuleTriggerCount(triggerRuleIDList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有需修改的告警状态不合法");
                }
            }
            if (ObjectUtil.isNotEmpty(actionTriggerIDList)) {
                TbWarnActionMapper tbWarnActionMapper = ContextHolder.getBean(TbWarnActionMapper.class);
                if (actionTriggerIDList.size() != tbWarnActionMapper.selectTriggerActionCount(actionTriggerIDList)) {
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
