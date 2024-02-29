package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserContact;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceStateInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.UpdateDeviceGroupSenderDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DeviceWarnDeviceType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceStateListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserContactParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDeviceWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDeviceWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDeviceWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.UpdateDeviceGroupSenderEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.DeviceWarnHistoryInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceWarnPageInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDeviceWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class TbDeviceWarnLogServiceImpl extends ServiceImpl<TbDeviceWarnLogMapper, TbDeviceWarnLog> implements ITbDeviceWarnLogService {

    private static final String WARN_CONTENT_FORMAT = "{} 内 {} 于 {} 发生离线报警，设备型号: {} ，设备SN: {}，请关注！";

    private static final String WARN_EMAIL_FORMAT = "尊敬的用户：\n" +
            "   您好!\n\n" +
            "   在 {} 中，我们监测到：\n" +
            "   有 {} 于时间：{} 发生离线报警!\n" +
            "   设备型号：{} \n" +
            "   设备SN：{}\n\n" +
            "请务必关注并采取相应措施,感谢您的配合";


    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;
    private final NotifyService notifyService;
    private final TbWarnNotifyRelationMapper notifyRelationMapper;
    private final FileConfig fileConfig;
    private final ApplicationEventPublisher publisher;

    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbWarnNotifyConfigMapper tbWarnNotifyConfigMapper;
    private final TbWarnLogMapper tbWarnLogMapper;
    private final TbVideoDeviceMapper tbVideoDeviceMapper;
    private final UserService userService;
    private final IotService iotService;
    private final RedisService redisService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDeviceWarnLog(SaveDeviceWarnParam param) {


        String content = StrUtil.format(WARN_CONTENT_FORMAT,
                param.getProjectName(), param.getDeviceSource(),
                param.getTime(), param.getDeviceType(), param.getDeviceSerial());

        if (param.getTbDeviceWarnLog() == null) {
            TbDeviceWarnLog tbDeviceWarnLog = new TbDeviceWarnLog(null, param.getCompanyID(), param.getPlatform(),
                    param.getProjectID(), param.getDeviceSerial(), param.getDeviceToken(), param.getTime(), null,
                    content, null, 0, null, null, null, null,
                    null, null);

            if (param.getStatus() != null && !param.getStatus()) {
                TbWarnNotifyConfig warnNotifyConfig = tbWarnNotifyConfigMapper.queryByCompanyIDAndPlatformID(param.getCompanyID(),
                        param.getPlatform(), 1, param.getProjectID());

                tbDeviceWarnLogMapper.insert(tbDeviceWarnLog);

                if (ObjectUtil.isNotNull(warnNotifyConfig)) {
                    //通知 (需要发送通知)
                    if (warnNotifyConfig.getNotifyMethod() != null) {

                        ResultWrapper<Map<Integer, UserContact>> wrapper = userService.queryUserContact(QueryUserContactParam.builder()
                                .depts(JSONUtil.toList(warnNotifyConfig.getDepts(), Integer.class))
                                .roles(JSONUtil.toList(warnNotifyConfig.getRoles(), Integer.class))
                                .users(JSONUtil.toList(warnNotifyConfig.getUsers(), Integer.class))
                                .build(), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());

                        // 邮件
                        if (warnNotifyConfig.getNotifyMethod().contains("3")) {
                            Map<Integer, UserContact> emailMap = Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of());
                            try {
                                boolean result = notifyService.mailNotify(DefaultConstant.SMS_SIGN_NAME,
                                        true, () -> StrUtil.format(WARN_EMAIL_FORMAT, param.getProjectName(),
                                                param.getDeviceSource(), DateUtil.format(param.getTime(), DatePattern.NORM_DATETIME_FORMAT),
                                                param.getDeviceType(), param.getDeviceSerial()), emailMap.values()
                                                .stream().map(UserContact::getEmail).filter(Objects::nonNull)
                                                .toArray(String[]::new));
                                Assert.isTrue(result);
                            } catch (Exception e) {
                                log.error(" 项目: {}, 平台: {} 设备报警邮件发送失败: {}",
                                        param.getProjectID(), param.getPlatform(), e.getMessage());
                            }
                        }

                        // 短信推送
                        if (warnNotifyConfig.getNotifyMethod().contains("2")) {
                            Map<Integer, UserContact> phoneMap = Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of());

                            try {
                                boolean result = notifyService.smsNotify(DefaultConstant.SMS_SIGN_NAME, fileConfig.getDeviceWarnTemplateCode(),
                                        Map.of("projectName", param.getProjectName(),
                                                "deviceModel", param.getDeviceSource(),
                                                "time", param.getTime(),
                                                "deviceType", param.getDeviceType(),
                                                "deviceSn", param.getDeviceSerial()), phoneMap.values().stream().map(UserContact::getCellphone)
                                                .filter(Objects::nonNull).distinct().toArray(String[]::new));
                                assert result;
                            } catch (Exception e) {
                                log.error("设备SN: {}, 平台: {} 报警短信发送失败: {}", param.getDeviceSerial(),
                                        param.getPlatform(), e.getMessage());
                            }
                        }

                        // 平台推送
                        if (warnNotifyConfig.getNotifyMethod().contains("1")) {
                            // 绑定通知ID与设备预警日志
                            final String warnName = "设备下线";
                            List<Integer> notifyIds = null;
                            Map<Integer, UserContact> phoneUserMap = Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of());
                            try {
                                notifyIds = notifyService.sysNotify(param.getCompanyID(),
                                        () -> List.of(new SysNotify.Notify(SysNotify.Type.ALARM, warnName,
                                                content, SysNotify.Status.UNREAD, param.getTime())),
                                        phoneUserMap.keySet().toArray(Integer[]::new));
                            } catch (Exception e) {
                                log.error("设备SN: {}, 平台: {} 报警平台通知发送失败: {}", param.getDeviceSerial(),
                                        param.getPlatform(), e.getMessage());
                            }

//                            通知关联
                            Optional.ofNullable(notifyIds).filter(e -> !e.isEmpty())
                                    .ifPresent(e -> notifyRelationMapper.insertBatchSomeColumn(e.stream()
                                            .map(item -> {
                                                TbWarnNotifyRelation relation = new TbWarnNotifyRelation();
                                                relation.setNotifyID(item);
                                                relation.setWarnLogID(tbDeviceWarnLog.getId());
                                                relation.setType(1);
                                                return relation;
                                            }).toList()));

                        }
                    } else {
                        log.error("设备SN:{}  平台: {} 未配置通知人, 无法发送通知", param.getDeviceToken(), param.getPlatform());
                    }
                }
            }
        } else {
            if (param.getStatus() != null && param.getStatus()) {
                param.getTbDeviceWarnLog().setWarnEndTime(param.getTime());
                param.getTbDeviceWarnLog().setDataStatus(0);
                tbDeviceWarnLogMapper.updateById(param.getTbDeviceWarnLog());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PageUtil.PageWithMap<DeviceWarnPageInfo> queryDeviceWarnPage(QueryDeviceWarnPageParam param) {
        Integer companyID = param.getCompanyID();
        TbWarnBaseConfig tbWarnBaseConfig = param.getTbWarnBaseConfig();
        Map<String, Object> warnBaseInfo = Map.of("warnTag", tbWarnBaseConfig.getWarnTag(),
                "warnLevelType", tbWarnBaseConfig.getWarnLevelType(),
                "warnLevelStyle", tbWarnBaseConfig.getWarnLevelStyle());

        LambdaQueryWrapper<TbDeviceWarnLog> wrapper = new LambdaQueryWrapper<TbDeviceWarnLog>()
                .eq(TbDeviceWarnLog::getCompanyID, companyID).eq(TbDeviceWarnLog::getPlatform, param.getPlatform())
                .orderByDesc(TbDeviceWarnLog::getWarnTime, TbDeviceWarnLog::getId);
        if (param.getIsRealTime()) {
            wrapper.isNull(TbDeviceWarnLog::getWarnEndTime);
        } else {
            wrapper.isNotNull(TbDeviceWarnLog::getWarnEndTime);
        }
        Optional.ofNullable(param.getQueryCode()).filter(ObjectUtil::isNotEmpty).ifPresent(u -> {
            if (!"离线报警".contains(u)) {
                wrapper.and(wr -> wr.like(TbDeviceWarnLog::getDeviceSerial, u));
            }
        });
        Optional.ofNullable(param.getProjectID()).ifPresent(u -> wrapper.eq(TbDeviceWarnLog::getProjectID, u));
        Optional.ofNullable(param.getStartTime()).ifPresent(u -> wrapper.ge(TbDeviceWarnLog::getWarnTime, u));
        Optional.ofNullable(param.getEndTime()).ifPresent(u -> wrapper.le(TbDeviceWarnLog::getWarnTime, u));
        Optional.ofNullable(param.getDealStatus()).ifPresent(u -> wrapper.eq(TbDeviceWarnLog::getDealStatus, u));
        List<TbDeviceWarnLog> tbDeviceWarnLogList = this.list(wrapper);
        List<DeviceWarnPageInfo> deviceWarnPageInfoList = queryDeviceWarnList(tbDeviceWarnLogList, companyID, param.build(), param.getDeviceType());
        if (CollUtil.isNotEmpty(deviceWarnPageInfoList)) {
            PageUtil.Page<DeviceWarnPageInfo> page = PageUtil.page(deviceWarnPageInfoList, param.getPageSize(), param.getCurrentPage());
            return new PageUtil.PageWithMap<>(page.totalPage(), page.currentPageData(), page.totalCount(), warnBaseInfo);
        }
        return PageUtil.PageWithMap.emptyWithMap(warnBaseInfo);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public DeviceWarnHistoryInfo queryDeviceWarnHistory(TbDeviceWarnLog tbDeviceWarnLog) {
        Date warnTime = tbDeviceWarnLog.getWarnTime();
        DeviceWarnHistoryInfo info = new DeviceWarnHistoryInfo();
        info.setWarnTime(warnTime);
        info.setWarnEndTime(tbDeviceWarnLog.getWarnEndTime());
        Optional.of(QueryDeviceStateListParam.builder().deviceToken(tbDeviceWarnLog.getDeviceSerial())
                        .begin(DateUtil.offsetDay(warnTime, -3)).end(DateUtil.offsetDay(warnTime, 3)).build())
                .map(iotService::queryDeviceStateList)
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).ifPresent(u -> {
                    List<Map<String, Object>> fourGSignalList = new ArrayList<>();
                    List<Map<String, Object>> extPowerVoltList = new ArrayList<>();
                    u.stream().sorted(Comparator.comparing(DeviceStateInfo::getTime)).peek(w -> {
                        Date time = w.getTime();
                        fourGSignalList.add(Map.of("time", time, "value", w.getFourGSignal()));
                        extPowerVoltList.add(Map.of("time", time, "value", w.getExtPowerVolt()));
                    }).toList();
                    info.setFourGSignalList(fourGSignalList);
                    info.setExtPowerVoltList(extPowerVoltList);
                });
        return info;
    }

    @Override
    public DeviceWarnPageInfo queryDeviceWarnDetail(QueryDeviceWarnDetailParam param) {
        return queryDeviceWarnList(List.of(param.getTbDeviceWarnLog()), param.getCompanyID(), new QueryWtTerminalWarnLogPageParam(), null)
                .stream().findAny().orElseThrow(() -> new RuntimeException("Unreachable Exception"));
    }

    @Override
    public void updateDeviceGroupSenderEvent(List<UpdateDeviceGroupSenderEventParam> param) {
        this.publisher.publishEvent(new UpdateDeviceGroupSenderDto(this, param));
    }

    public @Nullable String getLowestAreaCode(final String areaJson) {
        try {
            return Optional.ofNullable(areaJson).map(JSONUtil::parseObj).flatMap(u ->
                    Stream.of("town", "area", "city", "province")
                            .filter(u::containsKey).findFirst().map(u::getStr)).orElseThrow(IllegalArgumentException::new);
        } catch (JSONException | IllegalArgumentException e) {
            log.error("parse area json failed,area json: {}", areaJson);
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<DeviceWarnPageInfo> queryDeviceWarnList(final List<TbDeviceWarnLog> tbDeviceWarnLogList, final Integer companyID,
                                                         final QueryWtTerminalWarnLogPageParam param, @Nullable Integer deviceType) {
        Map<Integer, TbProjectInfo> projectInfoMap = Optional.of(tbDeviceWarnLogList.stream().map(TbDeviceWarnLog::getProjectID)
                .collect(Collectors.toSet())).filter(CollUtil::isNotEmpty).map(u -> tbProjectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>().in(TbProjectInfo::getID, u))).map(u -> u.stream()
                .collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()))).orElse(Map.of());
        List<DeviceWarnPageInfo> deviceWarnPageInfoList = tbDeviceWarnLogList.stream().map(u -> {
            DeviceWarnPageInfo info = new DeviceWarnPageInfo();
            BeanUtil.copyProperties(u, info);
            return info;
        }).toList();
        Set<String> deviceTokens = deviceWarnPageInfoList.stream().map(DeviceWarnPageInfo::getDeviceToken)
                .filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(deviceTokens)) {
            List<TbVideoDevice> tbVideoDeviceList = tbVideoDeviceMapper.selectList(new LambdaQueryWrapper<TbVideoDevice>()
                    .in(TbVideoDevice::getDeviceToken, deviceTokens));
            Map<String, TbVideoDevice> deviceTokenMap = tbVideoDeviceList.stream().collect(Collectors
                    .toMap(TbVideoDevice::getDeviceToken, Function.identity()));
            TransferUtil.INSTANCE.applyDeviceBase(deviceWarnPageInfoList,
                    () -> QueryDeviceBaseInfoParam.builder().deviceTokens(deviceTokens).companyID(companyID).build(),
                    DeviceWarnPageInfo::getDeviceToken,
                    (e, device) -> {
                        e.setWarnName("设备离线");
                        e.setGpsLocation(device.getGpsLocation());
                        e.setFirmwareVersion(device.getFirmwareVersion());
                        e.setProductID(device.getProductID());
                        e.setUniqueToken(device.getUniqueToken());
                        String deviceToken = e.getDeviceToken();
                        if (deviceTokenMap.containsKey(deviceToken)) {
                            e.setDeviceType(DeviceWarnDeviceType.VIDEO_DEVICE.getCode());
                            Optional.of(deviceToken).map(deviceTokenMap::get).map(TbVideoDevice::getDeviceType).ifPresent(e::setDeviceModel);
                        } else {
                            e.setDeviceType(DeviceWarnDeviceType.IOT_DEVICE.getCode());
                            e.setDeviceModel(device.getProductName());
                        }
                    });
            Set<String> uniqueTokenSet = deviceWarnPageInfoList.stream().map(DeviceWarnPageInfo::getUniqueToken).filter(StrUtil::isNotEmpty)
                    .collect(Collectors.toSet());
            if (!uniqueTokenSet.isEmpty()) {
                List<WtTerminalWarnLog> wtTerminalWarnLogs = tbWarnLogMapper.queryTerminalWarnListByUniqueToken(param, uniqueTokenSet);
                // user
                List<Integer> dealUserIDList = deviceWarnPageInfoList.stream().map(DeviceWarnPageInfo::getDealUserID).filter(Objects::nonNull)
                        .distinct().toList();
                Map<Integer, String> userIDNameMap = Optional.of(dealUserIDList).filter(CollUtil::isNotEmpty)
                        .map(QueryUserIDNameParameter::new)
                        .map(u -> userService.queryUserIDName(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                        .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).map(u -> u.stream().collect(
                                Collectors.toMap(UserIDName::getUserID, UserIDName::getUserName))).orElse(Map.of());

                // region
                Map<String, String> regionMap = wtTerminalWarnLogs.stream().map(WtTerminalWarnLog::getProjectList)
                        .flatMap(Collection::stream).map(WtTerminalWarnLog.Project::getRegionArea).map(item1 -> {
                            try {
                                return Optional.ofNullable(getLowestAreaCode(item1)).map(item2 -> new Tuple<>(item1, item2)).orElse(null);
                            } catch (JSONException e) {
                                log.error("parse json error,regionArea: {}", item1);
                                return null;
                            }
                        }).filter(Objects::nonNull).collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2, (o1, o2) -> o1));
                Optional.of(projectInfoMap).filter(CollUtil::isNotEmpty).map(Map::values).map(u -> u.stream()
                        .map(TbProjectInfo::getLocation).toList()).ifPresent(u -> u.stream().distinct().map(item1 -> {
                    try {
                        return Optional.ofNullable(getLowestAreaCode(item1)).map(item2 -> new Tuple<>(item1, item2)).orElse(null);
                    } catch (JSONException e) {
                        log.error("parse json error,regionArea: {}", item1);
                        return null;
                    }
                }).filter(Objects::nonNull).peek(w -> regionMap.putIfAbsent(w.getItem1(), w.getItem2())).toList());

                Map<String, String> regionNameMap = new HashMap<>();
                Optional.of(regionMap).filter(CollUtil::isNotEmpty).map(Map::values).map(u -> {
                    List<Object> regionList = u.stream().map(w -> (Object) w).toList();
                    return redisService.multiGet(RedisKeys.REGION_AREA_KEY, regionList, RegionArea.class);
                }).ifPresent(u -> {
                    Map<String, String> reverseRegionMap = regionMap.entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getValue, Map.Entry::getKey, (o1, o2) -> o1));
                    u.stream().peek(w -> Optional.ofNullable(w.getAreaCode()).map(String::valueOf).map(reverseRegionMap::get)
                            .ifPresent(s -> regionNameMap.put(s, w.getName()))).toList();
                });

                Map<String, WtTerminalWarnLog> uniqueTokenProjectInfoMap = wtTerminalWarnLogs
                        .stream().collect(Collectors.toMap(WtTerminalWarnLog::getUniqueToken, Function.identity()));
                return deviceWarnPageInfoList.stream().peek(item -> {
                    if (uniqueTokenProjectInfoMap.containsKey(item.getUniqueToken())) {
                        Optional.ofNullable(item.getDealUserID()).map(userIDNameMap::get).ifPresent(item::setDealUserName);
                        item.setProjectList(uniqueTokenProjectInfoMap.get(item.getUniqueToken()).getProjectList()
                                .stream().map(u -> u.getMonitorPointList().stream().map(w -> {
                                    String regionArea = u.getRegionArea();
                                    return DeviceProjectInfo.builder().projectID(u.getProjectID()).projectName(u.getProjectName())
                                            .projectShortName(u.getProjectShortName()).gpsLocation(w.getMonitorPointLocation())
                                            .regionArea(regionArea).regionAreaName(regionNameMap.get(regionArea))
                                            .monitorPointID(w.getMonitorPointID()).monitorPointName(w.getMonitorPointName())
                                            .monitorItemID(w.getMonitorItemID()).monitorItemName(w.getMonitorItemName())
                                            .monitorItemAlias(w.getMonitorItemAlias()).build();
                                }).toList()).flatMap(Collection::stream).toList());
                    } else {
                        Optional.of(item.getProjectID()).map(projectInfoMap::get).ifPresent(u -> {
                            String location = u.getLocation();
                            item.setProjectList(List.of(DeviceProjectInfo.builder().projectID(u.getID())
                                    .projectName(u.getProjectName()).projectShortName(u.getShortName()).regionArea(location)
                                    .regionAreaName(regionNameMap.get(location)).build()));
                        });
                    }
                }).filter(u -> Optional.ofNullable(deviceType).filter(ObjectUtil::isNotEmpty).map(w ->
                        w.equals(u.getDeviceType())).orElse(true)).toList();
            }
        }
        return List.of();
    }
}
