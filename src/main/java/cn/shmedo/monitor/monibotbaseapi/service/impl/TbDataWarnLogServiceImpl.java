package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogHistoryMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLogHistory;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevelStyle;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDataWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDeviceWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDataWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceWarnPageInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDataWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    private final TbWarnThresholdConfigMapper thresholdConfigMapper;
    private final ITbWarnBaseConfigService baseConfigService;
    private final ITbWarnNotifyConfigService notifyConfigService;
    private final NotifyService notifyService;
    private final TbDataWarnLogHistoryMapper historyMapper;
    private final TbWarnNotifyRelationMapper notifyRelationMapper;
    private final FileConfig fileConfig;
    private final UserService userService;

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
                param.setWarnCase(SAME);
            } else if (existLog.getWarnLevel() > param.getWarnLevel()) {
                //报警升级，更新报警时间和内容，发送变更通知，记录历史
                param.setWarnCase(UPGRADE);
            } else {
                //报警降级，更新报警时间和内容，记录历史
                param.setWarnCase(DOWNLEVEL);
            }
        } else {
            //不存在报警，生成报警记录，发送通知
            param.setWarnCase(NEW);
        }

        //历史记录 (除了新生成的报警，均需要记录变更历史)
        if (!NEW.equals(param.getWarnCase())) {
            TbDataWarnLogHistory history = new TbDataWarnLogHistory();
            history.setWarnLogID(existLog.getId());
            history.setWarnLevel(existLog.getWarnLevel());
            history.setWarnTime(existLog.getWarnTime());
            Assert.isTrue(this.historyMapper.insert(history) > 0, "存储数据报警失败");
        }

        //查询阈值配置详情
        WarnThresholdConfig threshold = thresholdConfigMapper.getInfoById(param.getThresholdID());

        //重新构建通知内容
        TbWarnBaseConfig config = baseConfigService.queryByCompanyIDAndPlatform(threshold.getCompanyID(), threshold.getPlatform());
        WarnTag warnTag = WarnTag.fromCode(config.getWarnTag());
        WarnLevelStyle style = WarnLevelStyle.fromCode(config.getWarnLevelStyle());
        String levelStr = style.getDesc().split(StrUtil.COMMA)[param.getWarnLevel() - 1] + warnTag.getDesc();

        String content = StrUtil.format(WARN_CONTENT_FORMAT, threshold.getProjectName(), threshold.getMonitorPointName(),
                DateUtil.format(param.getWarnTime(), DatePattern.NORM_DATETIME_FORMAT),
                threshold.getWarnName(), levelStr, threshold.getFieldName(), param.getWarnValue(), threshold.getFieldUnitEng());
        param.setWarnContent(content);
        param.setWarnLevelName(levelStr);


        //报警实体
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
                existLog.setWarnTime(param.getWarnTime());
                existLog.setDataStatus(1);
                yield existLog;
            }
            case UPGRADE, DOWNLEVEL -> {
                existLog.setWarnTime(param.getWarnTime());
                existLog.setWarnContent(param.getWarnContent());
                existLog.setWarnLevel(param.getWarnLevel());
                existLog.setDataStatus(1);
                yield existLog;
            }
        };
        this.saveOrUpdate(warnLog);

        //通知 (新生成和升级 需要发送通知)
        if (!SAME.equals(param.getWarnCase()) && !DOWNLEVEL.equals(param.getWarnCase())) {
            assert threshold != null;
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
                                    notifyConfig.getContacts().values().toArray(String[]::new));
                            Assert.isTrue(result);
                        } catch (Exception e) {
                            log.error("规则:{} 项目: {}, 平台: {} 数据报警短信发送失败: {}", threshold.getId(),
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

    @Override
    @Deprecated
    public PageUtil.PageWithMap<DeviceWarnPageInfo> queryDeviceWarnPage(QueryDeviceWarnPageParam param) {


//        return new PageUtil.PageWithMap<>(, , , Dict.of(param.getTbWarnBaseConfig()));
        return null;
    }

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
}