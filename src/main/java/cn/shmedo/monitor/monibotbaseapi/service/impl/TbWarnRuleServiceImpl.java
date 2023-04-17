package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtEngineDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtTriggerActionInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtEngineInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtWarnStatusDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnActionService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnRuleService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnTriggerService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnRuleServiceImpl extends ServiceImpl<TbWarnRuleMapper, TbWarnRule> implements ITbWarnRuleService {
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbMonitorItemMapper tbMonitorItemMapper;
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;
    private final UserService userService;
    private final ITbWarnTriggerService tbWarnTriggerService;
    private final ITbWarnActionService tbWarnActionService;

    //TODO inner warn status sorted
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public PageUtil.Page<WtEngineInfo> queryWtEnginePage(QueryWtEnginePageParam param) {
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectProjectInfoByCompanyID(param.getCompanyID());
        List<Integer> projectIDList = tbProjectInfos.stream().map(TbProjectInfo::getID).toList();
        if (CollectionUtil.isNullOrEmpty(projectIDList)) {
            return PageUtil.Page.empty();
        }
        final Map<Integer, String> projectIDNameMap = tbProjectInfos.stream()
                .collect(Collectors.toMap(TbProjectInfo::getID, TbProjectInfo::getProjectName));
        final Map<Integer, TbMonitorItem> idItemMap = new HashMap<>();
        final Map<Integer, TbMonitorPoint> idPointMap = new HashMap<>();
        IPage<TbWarnRule> page = this.baseMapper.selectWarnRuleByPage(
                new Page<>(param.getCurrentPage(), param.getPageSize()).addOrder(OrderItem.desc("CreateTime")),
                param, projectIDList);
        List<TbWarnRule> records = page.getRecords();
        List<Integer> ruleIds = records.stream().peek(u -> {
            Optional.ofNullable(u.getMonitorItemID()).ifPresent(s -> idItemMap.put(s, null));
            Optional.ofNullable(u.getMonitorPointID()).ifPresent(s -> idPointMap.put(s, null));
        }).map(TbWarnRule::getID).toList();
        Optional.of(idItemMap).filter(ObjectUtil::isNotEmpty).ifPresent(w ->
                this.tbMonitorItemMapper.selectBatchIds(w.keySet()).stream().peek(u -> w.put(u.getID(), u))
                        .collect(Collectors.toList()));
        Optional.of(idPointMap).filter(ObjectUtil::isNotEmpty).ifPresent(w ->
                this.tbMonitorPointMapper.selectBatchIds(w.keySet()).stream().peek(u -> w.put(u.getID(), u))
                        .collect(Collectors.toList()));
        Map<Integer, List<WtTriggerActionInfo>> engineIdWarnMap = Optional.of(ruleIds).filter(u -> u.size() > 0)
                .map(tbWarnTriggerService::queryWarnStatusByEngineIds)
                .map(w -> w.stream().collect(Collectors.toMap(WtTriggerActionInfo::getWarnID, info -> info.setAction(info))))
                .map(Map::values).map(t -> t.stream().collect(Collectors.groupingBy(WtTriggerActionInfo::getEngineID)))
                .orElse(new HashMap<>());
        List<WtEngineInfo> collect = records.stream().map(u -> WtEngineInfo.build(u)
                        .setProjectName(Optional.ofNullable(projectIDNameMap.get(u.getProjectID())).orElse("--"))
                        .setMonitorItemName(Optional.ofNullable(idItemMap.get(u.getMonitorItemID())).map(TbMonitorItem::getName)
                                .orElse("--"))
                        .setMonitorPointName(Optional.ofNullable(idPointMap.get(u.getMonitorPointID())).map(TbMonitorPoint::getName)
                                .orElse("--"))
                        .setDataList(Optional.ofNullable(engineIdWarnMap.get(u.getID())).map(w -> w.stream()
                                .map(WtTriggerActionInfo::build).collect(Collectors.toList())).orElse(new ArrayList<>())))
                .collect(Collectors.toList());
        return new PageUtil.Page<>(page.getPages(), collect, page.getTotal());
    }

    //TODO inner warn status sorted
    @SuppressWarnings("unchecked")
    @Override
    public WtEngineDetail queryWtEngineDetail(QueryWtEngineDetailParam param, final Tuple<String, String> appKeySecret) {
        Integer engineID = param.getEngineID();
        TbWarnRule tbWarnRule = this.baseMapper.selectById(engineID);
        WtEngineDetail build = WtEngineDetail.build(tbWarnRule);
        // 防止数据库NOT NULL被修改,还是在这里做了一些空处理
        Optional.ofNullable(tbWarnRule.getProjectID()).map(tbProjectInfoMapper::selectById).map(TbProjectInfo::getProjectName)
                .ifPresent(build::setProjectName);
        Optional.ofNullable(tbWarnRule.getMonitorPointID()).map(tbMonitorPointMapper::selectById).map(TbMonitorPoint::getName)
                .ifPresent(build::setMonitorPointName);
        Optional.ofNullable(tbWarnRule.getMonitorItemID()).map(tbMonitorItemMapper::selectById).ifPresent(u -> {
            build.setMonitorItemName(u.getName());
            build.setMonitorItemAlias(u.getAlias());
        });
        List<Integer> userIdList = new ArrayList<>();
        Map<Integer, String> userIdNameMap = null;
        Optional.ofNullable(tbWarnRule.getCreateUserID()).ifPresent(userIdList::add);
        Optional.ofNullable(tbWarnRule.getUpdateUserID()).ifPresent(userIdList::add);
        ResultWrapper<Object> wrapper = Optional.of(userIdList).filter(u -> u.size() > 0).map(w -> {
            QueryUserIDNameParameter pa = new QueryUserIDNameParameter();
            pa.setUserIDList(w);
            return pa;
        }).map(u -> userService.queryUserIDName(u, appKeySecret.getItem1(), appKeySecret.getItem2())).orElse(null);
        if (wrapper == null) {
            log.info("规则引擎缺少创建/修改用户ID,规则引擎ID: {}", engineID);
        } else {
            Object wrapperData = wrapper.getData();
            if (wrapperData == null) {
                log.info("第三方服务调用失败,未能获取到用户名称信息...");
            } else {
                userIdNameMap = ((Collection<Object>) wrapperData).stream()
                        .map(u -> JsonUtil.toObject(JsonUtil.toJson(u), UserIDName.class))
                        .collect(Collectors.toMap(UserIDName::getUserID, UserIDName::getUserName));
            }
        }
        Optional.ofNullable(userIdNameMap).flatMap(u -> Optional.ofNullable(tbWarnRule.getCreateUserID()).map(u::get))
                .ifPresent(build::setCreateUserName);
        List<WtTriggerActionInfo> infos = Optional.of(param).map(QueryWtEngineDetailParam::getEngineID).map(u -> {
            List<Integer> list = new ArrayList<>();
            list.add(u);
            return list;
        }).map(tbWarnTriggerService::queryWarnStatusByEngineIds).orElse(new ArrayList<>());
        // nullable for draft
        Optional.ofNullable(infos.stream().collect(Collectors.toMap(
                                WtTriggerActionInfo::getWarnID, info -> info.setAction(info), WtTriggerActionInfo::setAction))
                        .values().stream().collect(Collectors.groupingBy(WtTriggerActionInfo::getEngineID)).get(engineID))
                .ifPresent(warnList -> {
                    List<Integer> fieldList = warnList.stream().map(WtTriggerActionInfo::getFieldID).toList();
                    Map<Integer, String> fieldIdNameMap = tbMonitorTypeFieldMapper.selectBatchIds(fieldList).stream()
                            .collect(Collectors.toMap(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName));
                    List<WtWarnStatusDetailInfo> dataList = warnList.stream().map(WtTriggerActionInfo::buildDetail)
                            .map(u -> u.setMetadataName(fieldIdNameMap.getOrDefault(u.getMetadataID(), "--"))).toList();
                    build.setDataList(dataList);
                });
        return build;
    }

    @Override
    public Integer addWtEngine(AddWtEngineParam param, Integer userID) {
        Integer monitorItemID = param.getMonitorItemID();
        Integer monitorType = Optional.ofNullable(tbMonitorItemMapper.selectById(monitorItemID))
                .map(TbMonitorItem::getMonitorType)
                .orElseThrow(() -> new RuntimeException("请检查tb_monitor_type中id:" + monitorItemID + "的记录是否存在MonitorType"));
        TbWarnRule build = AddWtEngineParam.build(param);
        build.setCreateUserID(userID);
        build.setUpdateUserID(userID);
        build.setMonitorType(monitorType);
        this.save(build);
        return build.getID();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateWtEngine(UpdateWtEngineParam param) {
        Integer engineID = param.getEngineID();
        if (Objects.nonNull(param.getEngineName()) || Objects.nonNull(param.getEngineDesc())) {
            this.updateById(UpdateWtEngineParam.build(param));
        }
        List<WtWarnStatusDetailInfo> dataList = param.getDataList();
        if (!CollectionUtil.isNullOrEmpty(dataList)) {
            List<Tuple<TbWarnTrigger, List<TbWarnAction>>> tupleList = dataList.stream().map(u -> {
                Tuple<TbWarnTrigger, List<TbWarnAction>> tuple = new Tuple<>();
                TbWarnTrigger trigger = new TbWarnTrigger();
                BeanUtils.copyProperties(u, trigger);
                trigger.setFieldID(u.getMetadataID());
                trigger.setID(u.getWarnID());
                //ensure their relation won't be changed and tbWarnTriggerService would set one column at least.
                trigger.setRuleID(engineID);
                tuple.setItem1(trigger);
                tuple.setItem2(u.getAction());
                return tuple;
            }).toList();
            tbWarnTriggerService.saveOrUpdateBatch(tupleList.stream().map(Tuple::getItem1).toList());
            List<TbWarnAction> tbWarnActions = tupleList.stream().map(u -> {
                Integer warnID = u.getItem1().getID();
                return u.getItem2().stream().peek(w -> w.setTriggerID(warnID)).toList();
            }).flatMap(Collection::stream).toList();
            tbWarnActionService.saveOrUpdateBatch(tbWarnActions);
        }
    }

    @Override
    public void updateWtEngineEnable(BatchUpdateWtEngineEnableParam param) {
        List<Integer> engineIDList = param.getEngineIDList();
        if (param.getEnable()) {
            // ignore rule which not has trigger config while user try to enable it.
            engineIDList = this.baseMapper.selectRuleWarnIDListByRuleIDList(engineIDList);
        }
        if (!CollectionUtil.isNullOrEmpty(engineIDList)) {
            this.update(new LambdaUpdateWrapper<TbWarnRule>().in(TbWarnRule::getID, engineIDList)
                    .set(TbWarnRule::getEnable, param.getEnable()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteWtEngine(BatchDeleteWtEngineParam param) {
        List<Integer> engineIDList = param.getEngineIDList();
        List<TbWarnRule> tbWarnRules = this.baseMapper.selectList(
                new LambdaQueryWrapper<TbWarnRule>().in(TbWarnRule::getID, engineIDList).select(TbWarnRule::getID));
        if (CollectionUtil.isNullOrEmpty(tbWarnRules)) {
            return;
        }
        engineIDList = tbWarnRules.stream().map(TbWarnRule::getID).toList();
        this.removeBatchByIds(engineIDList);
        List<TbWarnTrigger> triggerIDList = tbWarnTriggerService.list(
                new LambdaQueryWrapper<TbWarnTrigger>().in(TbWarnTrigger::getRuleID, engineIDList));
        tbWarnTriggerService.deleteWarnStatusByList(triggerIDList);
    }
}
