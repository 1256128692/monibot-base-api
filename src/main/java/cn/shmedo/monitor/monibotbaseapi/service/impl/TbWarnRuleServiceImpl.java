package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryByProductIDListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.ProductBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnActionService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnRuleService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnTriggerService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.engineField.CompareIntervalDescUtil;
import cn.shmedo.monitor.monibotbaseapi.util.engineField.FieldShowUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnRuleServiceImpl extends ServiceImpl<TbWarnRuleMapper, TbWarnRule> implements ITbWarnRuleService {
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbMonitorItemMapper tbMonitorItemMapper;
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final UserService userService;
    private final IotService iotService;
    private final ITbWarnTriggerService tbWarnTriggerService;
    private final ITbWarnActionService tbWarnActionService;
    private final FileConfig fileConfig;
    private final TbWarnRuleMapper tbWarnRuleMapper;
    private final TbSensorMapper tbSensorMapper;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public PageUtil.Page<?> queryWtEnginePage(QueryWtEnginePageParam param) {
        Map<Integer, String> projectIDNameMap = null;
        List<Integer> projectIDList = null;
        if (WarnType.MONITOR.getCode().equals(param.getRuleType())) {
            List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectProjectInfoByCompanyID(param.getCompanyID());
            projectIDList = tbProjectInfos.stream().map(TbProjectInfo::getID).toList();
            if (CollectionUtil.isEmpty(projectIDList)) {
                return PageUtil.Page.empty();
            }
            projectIDNameMap = tbProjectInfos.stream().collect(
                    Collectors.toMap(TbProjectInfo::getID, TbProjectInfo::getProjectName));
        }
        Integer orderType = param.getOrderType();
        final Map<Integer, String> projectIDNameFinalMap = Optional.ofNullable(projectIDNameMap).orElse(new HashMap<>());
        final Map<Integer, TbMonitorItem> idItemMap = new HashMap<>();
        final Map<Integer, TbMonitorPoint> idPointMap = new HashMap<>();
        IPage<TbWarnRule> page = this.baseMapper.selectWarnRuleByPage(new Page<>(param.getCurrentPage(), param.getPageSize())
                        .addOrder(orderType == 1 ? OrderItem.desc("CreateTime") : OrderItem.asc("CreateTime")),
                param, projectIDList);
        List<TbWarnRule> records = page.getRecords();
        List<Integer> ruleIds = records.stream().peek(u -> {
            Optional.ofNullable(u.getMonitorItemID()).filter(w -> w != -1).ifPresent(s -> idItemMap.put(s, null));
            Optional.ofNullable(u.getMonitorPointID()).filter(w -> w != -1).ifPresent(s -> idPointMap.put(s, null));
        }).map(TbWarnRule::getID).toList();
        Map<Integer, String> productIDNameMap = queryProductIDNameMap(records);
        Optional.of(idItemMap).filter(ObjectUtil::isNotEmpty).ifPresent(w ->
                this.tbMonitorItemMapper.selectBatchIds(w.keySet()).stream().peek(u -> w.put(u.getID(), u)).toList());
        Optional.of(idPointMap).filter(ObjectUtil::isNotEmpty).ifPresent(w ->
                this.tbMonitorPointMapper.selectBatchIds(w.keySet()).stream().peek(u -> w.put(u.getID(), u)).toList());
        Map<Integer, List<WtTriggerActionInfo>> engineIdWarnMap = Optional.of(ruleIds).filter(u -> u.size() > 0)
                .map(tbWarnTriggerService::queryWarnStatusByEngineIds)
                .map(w -> w.stream().collect(Collectors.toMap(WtTriggerActionInfo::getWarnID, info -> info.setAction(info)
                        , WtTriggerActionInfo::setAction)))
                .map(Map::values).map(t -> t.stream().collect(Collectors.groupingBy(WtTriggerActionInfo::getEngineID)))
                .orElse(new HashMap<>());
        List<WtEngineInfo> collect = records.stream().map(u -> {
            List<WtWarnStatusInfo> dataList = Optional.ofNullable(engineIdWarnMap.get(u.getID())).map(w -> w.stream()
                    .filter(s -> ObjectUtil.isNotNull(s.getWarnID())).map(WtTriggerActionInfo::build)
                    .collect(Collectors.toList())).orElse(new ArrayList<>());
            return WtEngineInfo.build(u).setProductName(productIDNameMap.get(u.getProductID()))
                    .setProjectName(projectIDNameFinalMap.get(u.getProjectID())).setDataList(dataList)
                    .setMonitorItemName(Optional.ofNullable(idItemMap.get(u.getMonitorItemID()))
                            .map(TbMonitorItem::getName).orElse(null))
                    .setMonitorPointName(Optional.ofNullable(idPointMap.get(u.getMonitorPointID()))
                            .map(TbMonitorPoint::getName).orElse(null))
                    .setWhole(ObjectUtil.isNotEmpty(u.getExValue()) && CollectionUtil.isNotEmpty(dataList));
        }).collect(Collectors.toList());
        return new PageUtil.Page<>(page.getPages(), collect, page.getTotal());
    }

    @SuppressWarnings("unchecked")
    @Override
    public WtEngineDetail queryWtEngineDetail(QueryWtEngineDetailParam param) {
        Integer engineID = param.getEngineID();
        WtEngineDetail build = this.baseMapper.selectWtEngineDetail(engineID);
        Integer ruleType = build.getRuleType();
        boolean needSetCompareRule = Objects.nonNull(ruleType) && WarnType.MONITOR.equals(WarnType.formCode(ruleType));
        Optional.ofNullable(build.getProductID()).map(List::of).filter(CollectionUtil::isNotEmpty)
                .map(u -> {
                    QueryByProductIDListParam thirdParam = new QueryByProductIDListParam();
                    thirdParam.setProductIDList(u);
                    return thirdParam;
                })
                .map(iotService::queryByProductIDList).map(u -> {
                    if (!u.apiSuccess()) {
                        log.error("第三方服务调用失败,未能获取到对应产品名称信息...");
                    }
                    return u;
                }).filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData)
                .filter(u -> u.size() > 0).map(u -> u.get(0)).map(ProductBaseInfo::getProductName)
                .ifPresent(build::setProductName);
        Integer createUserID = build.getCreateUserID();
        Map<Integer, String> userIDNameMap = null;
        ResultWrapper<Object> wrapper = Optional.ofNullable(createUserID).map(List::of).map(w -> {
            QueryUserIDNameParameter pa = new QueryUserIDNameParameter();
            pa.setUserIDList(w);
            return pa;
        }).map(u -> userService.queryUserIDName(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret())).orElse(null);
        if (wrapper == null) {
            log.error("规则引擎缺少创建/修改用户ID,规则引擎ID: {}", engineID);
        } else {
            Object wrapperData = wrapper.getData();
            if (wrapperData == null) {
                log.error("第三方服务调用失败,未能获取到用户名称信息...");
            } else {
                userIDNameMap = ((Collection<Object>) wrapperData).stream()
                        .map(u -> JsonUtil.toObject(JsonUtil.toJson(u), UserIDName.class))
                        .collect(Collectors.toMap(UserIDName::getUserID, UserIDName::getUserName));
            }
        }
        Optional.ofNullable(userIDNameMap).flatMap(u -> Optional.ofNullable(createUserID).map(u::get))
                .ifPresent(build::setCreateUserName);
        List<WtTriggerActionInfo> infos = Optional.of(param).map(QueryWtEngineDetailParam::getEngineID).map(List::of)
                .map(tbWarnTriggerService::queryWarnStatusByEngineIds).orElse(new ArrayList<>());
        // nullable for draft
        Optional.ofNullable(infos.stream().collect(Collectors.toMap(
                                WtTriggerActionInfo::getWarnID, info -> info.setAction(info), WtTriggerActionInfo::setAction))
                        .values().stream().collect(Collectors.groupingBy(WtTriggerActionInfo::getEngineID)).get(engineID))
                .ifPresent(warnList -> {
                    //reduce unnecessary rule desc setting
                    Map<Integer, String> map = needSetCompareRule ? CompareIntervalDescUtil
                            .getCompareRuleDescMap(build.getMonitorTypeID(), warnList) : new HashMap<>();
                    List<WtWarnStatusDetailInfo> dataList = warnList.stream().filter(u -> Objects.nonNull(u) &&
                                    Objects.nonNull(u.getWarnID())).map(WtTriggerActionInfo::buildDetail)
                            .peek(u -> u.setCompareRuleDesc(map.get(u.getWarnID()))).map(FieldShowUtil::dealFieldShow).toList();
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
        build.setRuleType(WarnType.MONITOR.getCode().byteValue());
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
        if (Objects.nonNull(param.getEngineName()) || Objects.nonNull(param.getEngineDesc())
                || Objects.nonNull(param.getProjectID()) || Objects.nonNull(param.getMonitorItemID())
                || Objects.nonNull(param.getMonitorPointID())) {
            this.updateById(UpdateWtEngineParam.build(param));
        }
        List<WtWarnStatusDetailInfo> dataList = param.getDataList();
        if (CollectionUtil.isNotEmpty(dataList)) {
            List<Tuple<TbWarnTrigger, List<TbWarnAction>>> tupleList = dataList.stream().map(u -> {
                Tuple<TbWarnTrigger, List<TbWarnAction>> tuple = new Tuple<>();
                TbWarnTrigger trigger = new TbWarnTrigger();
                BeanUtil.copyProperties(u, trigger);
                trigger.setFieldToken(u.getFieldToken());
                trigger.setID(u.getWarnID());
                //ensure their relation won't be changed and tbWarnTriggerService would set one column at least.
                trigger.setRuleID(engineID);
                tuple.setItem1(trigger);
                tuple.setItem2(u.getAction());
                return tuple;
            }).toList();
            Optional.of(tupleList.stream().map(Tuple::getItem1).toList()).filter(CollectionUtil::isNotEmpty)
                    .ifPresent(tbWarnTriggerService::saveOrUpdateBatch);
            List<TbWarnAction> tbWarnActions = tupleList.stream().map(u -> {
                Integer warnID = u.getItem1().getID();
                return Optional.ofNullable(u.getItem2()).map(s -> s.stream().peek(w -> w.setTriggerID(warnID)).toList())
                        .orElse(new ArrayList<>());
            }).flatMap(Collection::stream).toList();
            Optional.of(tbWarnActions).filter(CollectionUtil::isNotEmpty).ifPresent(tbWarnActionService::saveOrUpdateBatch);
        }
    }

    @Override
    public void updateWtEngineEnable(BatchUpdateWtEngineEnableParam param) {
        List<Integer> engineIDList = param.getEngineIDList();
        if (param.getEnable()) {
            // ignore rule which not has trigger config while user try to enable it.
            engineIDList = this.baseMapper.selectRuleWarnIDListByRuleIDList(engineIDList);
        }
        if (CollectionUtil.isNotEmpty(engineIDList)) {
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
        if (CollectionUtil.isEmpty(tbWarnRules)) {
            return;
        }
        engineIDList = tbWarnRules.stream().map(TbWarnRule::getID).toList();
        this.removeBatchByIds(engineIDList);
        List<TbWarnTrigger> triggerIDList = tbWarnTriggerService.list(
                new LambdaQueryWrapper<TbWarnTrigger>().in(TbWarnTrigger::getRuleID, engineIDList));
        tbWarnTriggerService.deleteWarnStatusByList(triggerIDList);
    }

    private Map<Integer, String> queryProductIDNameMap(List<TbWarnRule> records) {
        List<Integer> productIDList = records.stream().map(TbWarnRule::getProductID).filter(Objects::nonNull).distinct().toList();
        if (CollectionUtil.isEmpty(productIDList)) {
            return new HashMap<>();
        }
        ResultWrapper<List<ProductBaseInfo>> wrapper = Optional.of(productIDList).map(u -> {
                    QueryByProductIDListParam thirdParam = new QueryByProductIDListParam();
                    thirdParam.setProductIDList(u);
                    return thirdParam;
                }).map(iotService::queryByProductIDList)
                .orElse(ResultWrapper.fail(new RuntimeException("查询产品信息时产生了未知的异常")));   //unable orElse
        if (!wrapper.apiSuccess()) {
            log.error("第三方服务调用失败,未能获取到产品型号名称");
            return new HashMap<>();
        }
        return wrapper.getData().stream().collect(Collectors.toMap(ProductBaseInfo::getProductID, ProductBaseInfo::getProductName));
    }

    @Override
    public Integer addWtDeviceWarnRule(AddWtDeviceWarnRuleParam pa, Integer userID) {
        TbWarnRule entity = Param2DBEntityUtil.fromAddWtDeviceWarnRuleParam2TbWarnRule(pa, userID);
        if (entity.getProductID() != null) {
            // iot 设备统计
            Collection<Integer> projectIDLIst = PermissionUtil.getHavePermissionProjectList(pa.getCompanyID());
            Map<String, String> idNameMap = tbProjectInfoMapper.selectBatchIds(projectIDLIst).stream().collect(Collectors.toMap(e -> e.getID().toString(), TbProjectInfo::getProjectName));
            QueryDeviceSimpleBySenderAddressParam request4Third = QueryDeviceSimpleBySenderAddressParam.builder()
                    .companyID(pa.getCompanyID())
                    .sendType(SendType.MDMBASE.toInt())
                    .sendAddressList(projectIDLIst.stream().map(String::valueOf).toList())
                    .productID(entity.getProductID())
                    .sendEnable(true)
                    .build();
            ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request4Third);
            List<SimpleDeviceV5> allData = result.getData();
            if (result.apiSuccess() && CollectionUtil.isNotEmpty(allData)) {
                if (!entity.getDeviceCSV().equals("all")) {
                    List<Integer> filterList = Arrays.stream(entity.getDeviceCSV().split(",")).map(Integer::valueOf).toList();
                    allData = allData.stream().filter(u -> filterList.contains(u.getDeviceID())).toList();
                }
                Map<String, Long> collect = allData.stream().flatMap(e -> e.getSendAddressList().stream()).collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                ));
                Map<String, Long> nameCount = collect.entrySet().stream().collect(Collectors.toMap(
                        e -> idNameMap.get(e.getKey()), Map.Entry::getValue
                ));
                if (StringUtils.isBlank(entity.getExValue())) {
                    entity.setExValue(JSONUtil.toJsonStr(Map.of("proCount", nameCount)));
                } else {
                    Map map = JSONUtil.toBean(entity.getExValue(), Map.class);
                    map.put("proCount", nameCount);
                    entity.setExValue(JSONUtil.toJsonStr(map));
                }
            }
        } else if (entity.getVideoType() != null) {
            // 视频设备统计
            List<TbSensor> tbSensors = tbSensorMapper.selectList(
                    new QueryWrapper<TbSensor>().lambda()
                            // 视频对应的监测类型为40
                            .eq(TbSensor::getMonitorType, 40)
                            .like(TbSensor::getExValues, entity.getVideoType())
            );
            if (CollectionUtil.isNotEmpty(tbSensors)) {
                if (!entity.getVideoCSV().equals("all")) {
                    List<String> videoSNList = Arrays.stream(entity.getVideoCSV().split(",")).toList();
                    tbSensors = tbSensors.stream().filter(
                            e -> {
                                String exValues = e.getExValues();
                                if (StringUtils.isBlank(exValues)) {
                                    return false;
                                } else {
                                    Map map = JSONUtil.toBean(exValues, Map.class);
                                    if (map.containsKey("seqNo")) {
                                        if (videoSNList.contains(map.get("seqNo").toString())) {
                                            return true;
                                        }

                                        return false;
                                    } else {
                                        return false;
                                    }

                                }
                            }
                    ).toList();
                }
            }
        }
        tbWarnRuleMapper.insert(entity);
        return entity.getID();
    }

    @Override
    public void mutateWarnRuleDevice(MutateWarnRuleDeviceParam pa, Integer userID) {
        List<String> strings = Arrays.stream(pa.getDeviceCSV().split(",")).toList();
        TbWarnRule tbWarnRule = pa.getTbWarnRule();
        if (tbWarnRule.getRuleType() == 2) {
            if (pa.getSign().equals("+")) {
                List<String> old = new ArrayList<>(Arrays.stream(tbWarnRule.getVideoCSV().split(",")).toList());
                old.addAll(strings);
                tbWarnRule.setVideoCSV(String.join(",", old));
            } else {
                List<String> old = new ArrayList<>(Arrays.stream(tbWarnRule.getVideoCSV().split(",")).toList());
                old.removeAll(strings);
                if (CollectionUtils.isEmpty(old)) {
                    throw new CustomBaseException(ResultCode.INVALID_PARAMETER.toInt(), "删除后设备为空");
                }
                tbWarnRule.setVideoCSV(String.join(",", old));

            }
        } else {
            if (pa.getSign().equals("+")) {
                List<String> old = new ArrayList<>(Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).toList());
                old.addAll(strings);
                tbWarnRule.setDeviceCSV(String.join(",", old));
            } else {
                List<String> old = new ArrayList<>(Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).toList());
                old.removeAll(strings);
                if (CollectionUtils.isEmpty(old)) {
                    throw new CustomBaseException(ResultCode.INVALID_PARAMETER.toInt(), "删除后设备为空");
                }
                tbWarnRule.setDeviceCSV(String.join(",", old));
            }
        }
        tbWarnRule.setUpdateTime(new Date());
        tbWarnRule.setUpdateUserID(userID);
        tbWarnRuleMapper.updateById(tbWarnRule);
    }
}
