package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserContact;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnConfigClearDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnConfigEventDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevelStyle;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.CancelDataWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDataWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDataWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDataWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.ThresholdBaseConfigFieldInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnLevelAliasInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnHistoryInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnHistoryListInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnPageInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDataWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnCase.*;

/**
 * @author Chengfs on 2024/1/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TbDataWarnLogServiceImpl extends ServiceImpl<TbDataWarnLogMapper, TbDataWarnLog> implements ITbDataWarnLogService {

    private static final String WARN_CONTENT_FORMAT = "{} 内 {} {} 发生 {} — {}，实测数据：{} {} {}，请关注！";
    private static final String WARN_EMAIL_FORMAT = "尊敬的用户：\n       您好！\n\n       在 {} 中，我们监测到:\n" +
            "       {} 于 {} 发生 {} ，报警等级为 {}，实测 {} 为 {} (单位：{})\n\n请务必密切关注并及时采取相应措施，感谢您的配合！";

    private final TbWarnLevelAliasMapper tbWarnLevelAliasMapper;
    private final TbWarnThresholdConfigMapper thresholdConfigMapper;
    private final ITbWarnBaseConfigService baseConfigService;
    private final ITbWarnNotifyConfigService notifyConfigService;
    private final NotifyService notifyService;
    private final TbDataWarnLogHistoryMapper historyMapper;
    private final TbWarnNotifyRelationMapper notifyRelationMapper;
    private final TbSensorMapper sensorMapper;
    private final TbWarnLevelAliasMapper warnLevelAliasMapper;
    private final FileConfig fileConfig;
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDataWarnLog(SaveDataWarnParam param) {

        if (param.getWarnLevel() == 0) {
            this.update(Wrappers.<TbDataWarnLog>lambdaUpdate()
                    .eq(TbDataWarnLog::getWarnThresholdID, param.getThresholdID())
                    .ne(TbDataWarnLog::getDealStatus, 2)
                    .isNull(TbDataWarnLog::getWarnEndTime)
                    .set(TbDataWarnLog::getDataStatus, 0)
                    .set(TbDataWarnLog::getWarnEndTime, param.getWarnTime()));

            sensorMapper.autoUpdateStatusById(param.getSensorID());
            return;
        }

        //查询规则对应报警是否存在
        TbDataWarnLog existLog = this.getOne(Wrappers.<TbDataWarnLog>lambdaQuery()
                .eq(TbDataWarnLog::getWarnThresholdID, param.getThresholdID())
                .isNull(TbDataWarnLog::getWarnEndTime)
                .select(TbDataWarnLog::getId, TbDataWarnLog::getWarnThresholdID,
                        TbDataWarnLog::getWarnLevel, TbDataWarnLog::getWarnTime));

        //区分报警类型
        if (existLog != null) {
            if (Objects.equals(existLog.getWarnLevel(), param.getWarnLevel())) {
                //等级未变更，更新报警时间、通知内容，记录历史
                if (existLog.getWarnTime().equals(param.getWarnTime())) {
                    //同级别、同时间报警 直接忽略
                    return;
                }
                param.setWarnCase(SAME);
            } else if (existLog.getWarnLevel() > param.getWarnLevel()) {
                //报警升级，更新报警时间和内容，发送变更通知，记录历史
                param.setWarnCase(UPGRADE);
            } else {
                //报警降级，更新报警时间和内容，记录历史
                param.setWarnCase(DOWNLEVEL);
            }
        } else {
            //不存在报警，生成报警记录，发送通知，记录历史
            param.setWarnCase(NEW);
        }

        //查询阈值配置详情
        WarnThresholdConfig threshold = thresholdConfigMapper.getInfoById(param.getThresholdID());

        //重新构建通知内容
        TbWarnBaseConfig config = baseConfigService.queryByCompanyIDAndPlatform(threshold.getCompanyID(), threshold.getPlatform());
        WarnTag warnTag = WarnTag.fromCode(config.getWarnTag());
        //查询报警级别别名
        TbWarnLevelAlias levelAlias = warnLevelAliasMapper.selectOne(Wrappers.<TbWarnLevelAlias>lambdaQuery()
                .eq(TbWarnLevelAlias::getPlatform, threshold.getPlatform())
                .eq(TbWarnLevelAlias::getProjectID, threshold.getProjectID())
                .eq(TbWarnLevelAlias::getMonitorType, threshold.getMonitorType())
                .eq(TbWarnLevelAlias::getMonitorItemID, threshold.getMonitorItemID())
                .eq(TbWarnLevelAlias::getFieldID, threshold.getFieldID())
                .eq(TbWarnLevelAlias::getWarnLevel, param.getWarnLevel())
                .select(TbWarnLevelAlias::getAlias));
        String levelStr;
        if (levelAlias != null && StrUtil.isNotBlank(levelAlias.getAlias())) {
            levelStr = levelAlias.getAlias();
        } else {
            WarnLevelStyle style = WarnLevelStyle.fromCode(config.getWarnLevelStyle());
            levelStr = style.getDesc().split(StrUtil.COMMA)[param.getWarnLevel() - 1] + warnTag.getDesc();
        }

        String content = StrUtil.format(WARN_CONTENT_FORMAT, threshold.getProjectName(), threshold.getMonitorPointName(),
                DateUtil.format(param.getWarnTime(), DatePattern.NORM_DATETIME_FORMAT),
                threshold.getWarnName(), levelStr, threshold.getFieldName(), param.getWarnValue(), threshold.getFieldUnitEng());
        param.setWarnContent(content);
        param.setWarnLevelName(levelStr);


        //报警实体
        // 报警上来时,不再更新原报警的时间
        TbDataWarnLog warnLog = switch (param.getWarnCase()) {
            case NEW -> {
                TbDataWarnLog entity = new TbDataWarnLog();
                entity.setCompanyID(threshold.getCompanyID());
                entity.setPlatform(threshold.getPlatform());
                entity.setWarnThresholdID(param.getThresholdID());
                entity.setWarnLevel(param.getWarnLevel());
                entity.setWarnTime(param.getWarnTime());
                entity.setWarnContent(param.getWarnContent());
                entity.setDealStatus(0);
                entity.setDataStatus(1);
                yield entity;
            }
            case SAME -> {
                existLog.setWarnContent(param.getWarnContent());
//                existLog.setWarnTime(param.getWarnTime());
                existLog.setDataStatus(1);
                yield existLog;
            }
            case UPGRADE, DOWNLEVEL -> {
//                existLog.setWarnTime(param.getWarnTime());
                existLog.setWarnContent(param.getWarnContent());
                existLog.setWarnLevel(param.getWarnLevel());
                existLog.setDataStatus(1);
                yield existLog;
            }
        };
        this.saveOrUpdate(warnLog);
        //记录变更历史
        TbDataWarnLogHistory history = new TbDataWarnLogHistory();
        history.setWarnLogID(warnLog.getId());
        history.setWarnLevel(warnLog.getWarnLevel());
        history.setWarnTime(param.getWarnTime());
        this.historyMapper.insertOrUpdate(history);
        //更新传感器状态
        sensorMapper.autoUpdateStatusById(param.getSensorID());

        //通知 (新生成和升级 需要发送通知)
        if (!SAME.equals(param.getWarnCase()) && !DOWNLEVEL.equals(param.getWarnCase())) {
            WarnNotifyConfig notifyConfig = notifyConfigService.queryByProjectIDAndPlatform(threshold.getProjectID(),
                    threshold.getPlatform(), 2);
            if (notifyConfig != null) {
                if (notifyConfig.getLevels().contains(param.getWarnLevel())) {
                    if (notifyConfig.getMethods().contains(1)) {
                        // 平台消息
                        final String warnName = threshold.getWarnName();
                        List<Integer> notifyIds = null;
                        try {
                            notifyIds = notifyService.sysNotify(threshold.getCompanyID(),
                                    () -> List.of(new SysNotify.Notify(SysNotify.Type.ALARM, warnName,
                                            param.getWarnContent(), SysNotify.Status.UNREAD, param.getWarnTime())),
                                    notifyConfig.getContacts().keySet().toArray(Integer[]::new));
                        } catch (Exception e) {
                            log.error("规则:{} 项目: {}, 平台: {} 数据报警平台通知发送失败: {}", threshold.getId(),
                                    threshold.getProjectID(), threshold.getPlatform(), e.getMessage());
                            throw e;
                        }

                        //通知关联
                        Optional.ofNullable(notifyIds).filter(e -> !e.isEmpty())
                                .ifPresent(e -> notifyRelationMapper.insertBatchSomeColumn(e.stream()
                                        .map(item -> {
                                            TbWarnNotifyRelation relation = new TbWarnNotifyRelation();
                                            relation.setNotifyID(item);
                                            relation.setWarnLogID(warnLog.getId());
                                            relation.setType(1);
                                            return relation;
                                        }).toList()));
                    }

                    if (notifyConfig.getMethods().contains(2)) {
                        // 短信
                        Map<String, Object> paramMap = Map.of(
                                "warnType", UPGRADE.equals(param.getWarnCase()) ? "警报升级" : "警报通知",
                                "projectName", Optional.ofNullable(threshold.getProjectShortName())
                                        .filter(e -> !e.isBlank()).orElse(threshold.getProjectName()),
                                "pointName", threshold.getMonitorPointName(),
                                "time", DateUtil.format(param.getWarnTime(), DatePattern.NORM_DATETIME_FORMAT),
                                "warnName", threshold.getWarnName(),
                                "warnLevel", param.getWarnLevelName(),
                                "sensorName", threshold.getFieldName(),
                                "sensorData", param.getWarnValue().toString(),
                                "unit", Optional.ofNullable(threshold.getFieldUnitEng())
                                        .filter(e -> !e.isEmpty()).orElse("无"));
                        try {
                            boolean result = notifyService.smsNotify(DefaultConstant.SMS_SIGN_NAME,
                                    fileConfig.getDataWarnTemplateCode(), paramMap,
                                    notifyConfig.getContacts().values().stream().map(UserContact::getCellphone)
                                            .filter(Objects::nonNull).distinct().toArray(String[]::new));
                            Assert.isTrue(result);
                        } catch (Exception e) {
                            log.error("规则:{} 项目: {}, 平台: {} 数据报警短信发送失败: {}", threshold.getId(),
                                    threshold.getProjectID(), threshold.getPlatform(), e.getMessage());
                        }
                    }

                    if (notifyConfig.getMethods().contains(3)) {
                        // 邮件
                        try {
                            boolean result = notifyService.mailNotify(DefaultConstant.SMS_SIGN_NAME,
                                    false, () -> StrUtil.format(WARN_EMAIL_FORMAT, threshold.getProjectName(),
                                            threshold.getMonitorPointName(),
                                            DateUtil.format(param.getWarnTime(), DatePattern.NORM_DATETIME_FORMAT),
                                            threshold.getWarnName(), param.getWarnLevelName(), threshold.getFieldName(),
                                            param.getWarnValue().toString(),
                                            Optional.ofNullable(threshold.getFieldUnitEng()).filter(e -> !e.isEmpty()).orElse("无")),
                                    notifyConfig.getContacts().values()
                                            .stream().map(UserContact::getEmail).filter(Objects::nonNull)
                                            .toArray(String[]::new));
                            Assert.isTrue(result);
                        } catch (Exception e) {
                            log.error("规则:{} 项目: {}, 平台: {} 数据报警邮件发送失败: {}", threshold.getId(),
                                    threshold.getProjectID(), threshold.getPlatform(), e.getMessage());
                        }
                    }
                }
            } else {
                log.info("规则:{} 项目: {}, 平台: {} 数据报警未配置通知人, 无法发送通知", threshold.getId(),
                        threshold.getProjectID(), threshold.getPlatform());
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public PageUtil.PageWithMap<DataWarnPageInfo> queryDataWarnPage(QueryDataWarnPageParam param) {
        TbWarnBaseConfig tbWarnBaseConfig = param.getTbWarnBaseConfig();
        IPage<DataWarnPageInfo> page = this.baseMapper.selectDataWarnPage(new Page<>(param.getCurrentPage(), param.getPageSize()), param);
        List<DataWarnPageInfo> records = page.getRecords();
        Optional.of(records.stream().map(DataWarnPageInfo::getDealUserID).collect(Collectors.toSet()))
                .filter(CollUtil::isNotEmpty).map(u -> new QueryUserIDNameParameter(u.stream().toList()))
                .map(u -> userService.queryUserIDName(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).map(u -> u.stream().collect(Collectors
                        .toMap(UserIDName::getUserID, UserIDName::getUserName))).filter(CollUtil::isNotEmpty).ifPresent(u ->
                        records.stream().filter(w -> u.containsKey(w.getDealUserID())).peek(w ->
                                w.setDealUserName(u.get(w.getDealUserID()))).toList());
        return new PageUtil.PageWithMap<>(page.getPages(), page.getRecords(), page.getTotal(), Map.of(
                "warnTag", tbWarnBaseConfig.getWarnTag(),
                "warnLevelType", tbWarnBaseConfig.getWarnLevelType(),
                "warnLevelStyle", tbWarnBaseConfig.getWarnLevelStyle()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelDataWarn(Integer userID, CancelDataWarnParam param) {
        Integer silenceCycle = param.getSilenceCycle();
        Date current = new Date();
        TbDataWarnLog tbDataWarnLog = param.getTbDataWarnLog();
        Integer warnThresholdID = tbDataWarnLog.getWarnThresholdID();
        tbDataWarnLog.setDealTime(current);
        tbDataWarnLog.setDealStatus(2);
        tbDataWarnLog.setWarnEndTime(current);
        if (Objects.nonNull(silenceCycle)) {
            publisher.publishEvent(new WarnConfigEventDto(this, RedisKeys.WARN_SILENCE_CYCLE + warnThresholdID,
                    null, silenceCycle * 3600000L, tbDataWarnLog.getWarnLevel()));
        }
        publisher.publishEvent(new WarnConfigClearDto(this, RedisKeys.WARN_HIT + warnThresholdID));
        this.updateById(tbDataWarnLog);
    }

    @Override
    public DataWarnDetailInfo queryDataWarnDetail(QueryDataWarnDetailParam param) {
        final TbDataWarnLog tbDataWarnLog = param.getTbDataWarnLog();
        final Integer platform = tbDataWarnLog.getPlatform();
        final Integer warnLevel = tbDataWarnLog.getWarnLevel();
        TbWarnBaseConfig tbWarnBaseConfig = baseConfigService.queryByCompanyIDAndPlatform(param.getCompanyID(), platform);
        DataWarnDetailInfo info = this.thresholdConfigMapper.selectDataWarnRelativeByID(tbDataWarnLog.getWarnThresholdID());
        BeanUtil.copyProperties(tbDataWarnLog, info);
        BeanUtil.copyProperties(tbWarnBaseConfig, info);

        // user
        Integer dealUserID = info.getDealUserID();
        Map<Integer, String> userIDNameMap = Optional.ofNullable(dealUserID).map(List::of).map(QueryUserIDNameParameter::new)
                .map(u -> userService.queryUserIDName(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).map(u -> u.stream().collect(Collectors
                        .toMap(UserIDName::getUserID, UserIDName::getUserName))).orElse(Map.of());
        Optional.ofNullable(dealUserID).map(userIDNameMap::get).ifPresent(info::setDealUserName);

        WarnLevelAliasInfo warnLevelAliasInfo = tbWarnLevelAliasMapper.selectThresholdBaseConfigFieldInfoList(platform, info.getMonitorItemID())
                .stream().filter(u -> u.getFieldID().equals(info.getFieldID())).map(ThresholdBaseConfigFieldInfo::getAliasConfigList)
                .flatMap(Collection::stream).filter(u -> u.getWarnLevel().equals(warnLevel)).findAny().orElse(new WarnLevelAliasInfo(warnLevel, null));
        info.setAliasConfig(warnLevelAliasInfo);
        return info;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public DataWarnHistoryInfo queryDataWarnHistory(QueryDataWarnDetailParam param) {
        final Integer platform = param.getTbDataWarnLog().getPlatform();
        TbWarnBaseConfig tbWarnBaseConfig = this.baseConfigService.queryByCompanyIDAndPlatform(param.getCompanyID(), platform);
        TbWarnThresholdConfig tbWarnThresholdConfig = thresholdConfigMapper.selectById(param.getTbDataWarnLog().getWarnThresholdID());
        JSONObject configValue = Optional.ofNullable(tbWarnThresholdConfig.getValue()).filter(ObjectUtil::isNotEmpty)
                .map(u -> {
                    try {
                        return JSONUtil.parseObj(u);
                    } catch (JSONException e) {
                        log.error("parse json error,json: {}", u);
                        return null;
                    }
                }).orElse(new JSONObject());

        DataWarnHistoryInfo info = new DataWarnHistoryInfo();
        BeanUtil.copyProperties(tbWarnBaseConfig, info);
        List<TbDataWarnLogHistory> historyList = historyMapper.selectList(new LambdaQueryWrapper<TbDataWarnLogHistory>()
                .eq(TbDataWarnLogHistory::getWarnLogID, param.getWarnLogID()).orderByAsc(TbDataWarnLogHistory::getWarnTime));
        // 最多展示后三天
        if (CollUtil.isNotEmpty(historyList)) {
            Date startWarnTime = historyList.stream().findFirst().map(TbDataWarnLogHistory::getWarnTime).orElseThrow();
            DateTime showEndTime = DateUtil.offsetDay(startWarnTime, 3);
            historyList = historyList.stream().filter(u -> u.getWarnTime().before(showEndTime)).toList();
        }
        List<DataWarnHistoryListInfo> dataList = historyList.stream().map(u -> {
            Integer warnLevel = u.getWarnLevel();
            String thresholdStr = Optional.of(warnLevel).map(String::valueOf).map(configValue::get).map(JSONUtil::toJsonStr)
                    .orElse(StrUtil.EMPTY_JSON);
            return DataWarnHistoryListInfo.builder().warnTime(u.getWarnTime()).compareMode(tbWarnThresholdConfig.getCompareMode())
                    .threshold(thresholdStr).aliasConfig(new WarnLevelAliasInfo(warnLevel, null)).build();
        }).toList();
        info.setDataList(dataList);

        // alias
        Set<Integer> warnLevelSet = historyList.stream().map(TbDataWarnLogHistory::getWarnLevel).collect(Collectors.toSet());
        Map<Integer, String> aliasMap = Optional.of(warnLevelSet).filter(CollUtil::isNotEmpty).map(u ->
                tbWarnLevelAliasMapper.selectList(new LambdaQueryWrapper<TbWarnLevelAlias>()
                        .eq(TbWarnLevelAlias::getPlatform, platform)
                        .eq(TbWarnLevelAlias::getMonitorItemID, tbWarnThresholdConfig.getMonitorItemID())
                        .eq(TbWarnLevelAlias::getFieldID, tbWarnThresholdConfig.getFieldID())
                        .in(TbWarnLevelAlias::getWarnLevel, u))).map(u -> u.stream().collect(Collectors
                .toMap(TbWarnLevelAlias::getWarnLevel, TbWarnLevelAlias::getAlias))).orElse(Map.of());
        if (CollUtil.isNotEmpty(aliasMap)) {
            dataList.stream().peek(u -> {
                WarnLevelAliasInfo aliasConfig = u.getAliasConfig();
                Optional.of(aliasConfig.getWarnLevel()).map(aliasMap::get).ifPresent(aliasConfig::setAlias);
            }).toList();
        }
        return info;
    }
}