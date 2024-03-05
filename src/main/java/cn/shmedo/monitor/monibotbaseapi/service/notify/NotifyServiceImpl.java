package cn.shmedo.monitor.monibotbaseapi.service.notify;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.third.TbService;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataDeviceWarnType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DeviceWarnDeviceType;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyByProjectID;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.SetNotifyStatusParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.QueryNotifyStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.MailNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.SmsNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyListByProjectID;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyPageResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceWarnLatestInfo;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.notify.MdNotifyService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 通知服务整合
 *
 * @author Chengfs on 2024/1/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final FileConfig config;
    private final MdNotifyService mdNotifyService;
    private final UserService userService;

    private final TbNotifyRelationMapper tbNotifyRelationMapper;
    private final TbDataWarnLogMapper tbDataWarnLogMapper;
    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;
    private final TbVideoDeviceMapper tbVideoDeviceMapper;

    @Override
    public boolean smsNotify(String signName, String templateCode, Supplier<Collection<SmsNotify.Param>> paramSupplier,
                             @Nonnull String... phoneNumbers) {
        SmsNotify param = SmsNotify.builder().signName(signName).templateCode(templateCode)
                .userPhones(Arrays.stream(phoneNumbers).distinct().toList())
                .params(paramSupplier.get().stream()
                        .map(e -> new SmsNotify.Param(e.keyName(), formatSmsParam(e.value()))).toList()).build();
        ResultWrapper<Void> result = mdNotifyService.sendSms(param);
        return Optional.of(result.apiSuccess()).filter(e -> e)
                .orElseThrow(() -> new CustomBaseException(result.getCode(), result.getMsg()));
    }

    @Override
    public boolean smsNotify(String signName, String templateCode, Map<String, Object> params, @Nonnull String... phoneNumbers) {
        return smsNotify(signName, templateCode, () -> params.entrySet().stream()
                .map(e -> new SmsNotify.Param(e.getKey(), e.getValue().toString())).toList(), phoneNumbers);
    }

    @Override
    public List<Integer> sysNotify(Integer companyID, Supplier<Collection<SysNotify.Notify>> notifySupplier,
                                   @Nonnull Integer... userIDs) {

        SysNotify param = SysNotify.builder().companyID(companyID)
                .notifyUsers(Arrays.stream(userIDs).distinct().toList())
                .notifyList(notifySupplier.get()).build();

        ResultWrapper<List<Integer>> result = userService.addSysNotify(param,
                config.getAuthAppKey(), config.getAuthAppSecret());

        Optional.of(result).filter(ResultWrapper::apiSuccess)
                .orElseThrow(() -> new CustomBaseException(result.getCode(), result.getMsg()));

        return Optional.ofNullable(result.getData()).orElse(List.of());
    }

    @Override
    public boolean mailNotify(String mailTag, boolean isHtml, Supplier<String> contentSupplier, @NotNull String... mailAddresses) {
        MailNotify param = MailNotify.builder().mailTag(mailTag)
                .isHtml(isHtml)
                .mailContent(contentSupplier.get())
                .userMailAddressList(Arrays.stream(mailAddresses).distinct().toList()).build();
        ResultWrapper<Void> result = mdNotifyService.sendMail(param);
        return Optional.of(result.apiSuccess()).filter(e -> e)
                .orElseThrow(() -> new CustomBaseException(result.getCode(), result.getMsg()));
    }

    @Override
    public NotifyPageResponse.Page<NotifyPageResponse> queryNotifyPage(QueryNotifyPageParam param, String accessToken) {
        // 工程级别过滤
        NotifyByProjectID notifyByProjectID = Optional.ofNullable(param.getProjectID())
                .map(this::filterProject).orElse(null);

        param.setNotifyByProjectID(notifyByProjectID);
        // 分页查询通知信息（条件过滤）
        PageUtil.Page<NotifyPageInfo> page = Optional.ofNullable(param.build())
                .map(u -> userService.queryNotifyPageList(u, accessToken))
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).orElse(PageUtil.Page.empty());

        // 查询系统通知统计信息
        QueryNotifyStatisticsParam notifyStatisticsParam = BeanUtil.toBean(param, QueryNotifyStatisticsParam.class);
        NotifyStatisticsInfo notifyStatisticsInfo = Optional.ofNullable(userService.queryNotifyStatistics(notifyStatisticsParam, accessToken))
                .filter(ResultWrapper::apiSuccess)
                .map(ResultWrapper::getData).orElse(new NotifyStatisticsInfo());

        // 条件查询通知关系
        Map<Integer, TbNotifyRelation> notifyRelationMap = Optional.of(page).map(PageUtil.Page::currentPageData)
                .map(u -> u.stream().map(NotifyPageInfo::getNotifyID).toList()).filter(CollUtil::isNotEmpty)
                .map(u -> new LambdaQueryWrapper<TbNotifyRelation>().in(TbNotifyRelation::getNotifyID, u))
                .map(tbNotifyRelationMapper::selectList).map(u -> u.stream().collect(Collectors
                        .toMap(TbNotifyRelation::getNotifyID, Function.identity()))).orElse(Map.of());

        // 平台服务
        RedisService authRedisService = SpringUtil.getBean(RedisConstant.AUTH_REDIS_SERVICE, RedisService.class);
        Map<String, TbService> tbServiceMap = Optional.of(authRedisService.getAll(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, TbService.class)).get();

        // 组装返回结果
        List<NotifyPageResponse> list = Optional.ofNullable(page.currentPageData()).map(u -> u.stream().map(w -> {
            NotifyPageResponse response = new NotifyPageResponse();
            BeanUtil.copyProperties(w, response);
            Optional.ofNullable(response.getNotifyID()).map(notifyRelationMap::get).ifPresent(s -> {
                response.setRelationID(s.getRelationID());
                response.setRelationType(s.getType());
            });
            if (Objects.nonNull(response.getServiceID())) {
                response.setServiceName(tbServiceMap.get(String.valueOf(response.getServiceID())).getServiceName());
            }
            return response;
        }).toList()).orElse(List.of());
        return new NotifyPageResponse.Page<>(page.totalPage(), list, page.totalCount(),
                Objects.isNull(notifyStatisticsInfo.getUnreadCount()) ? 0 : notifyStatisticsInfo.getUnreadCount());
    }

    @Override
    public List<NotifyPageResponse> queryNotifyList(QueryNotifyListParam param, String accessToken) {
        // 工程级别过滤
        NotifyByProjectID notifyByProjectID = Optional.ofNullable(param.getProjectID())
                .map(this::filterProject).orElse(null);

        param.setNotifyByProjectID(notifyByProjectID);
        ResultWrapper<List<NotifyPageResponse>> resultWrapper = userService.queryNotifyList(param, accessToken);
        return Optional.ofNullable(resultWrapper)
                .filter(ResultWrapper::apiSuccess)
                .map(ResultWrapper::getData)
                .filter(CollectionUtil::isNotEmpty).orElse(Collections.emptyList());
    }

    /**
     * 根据工程ID过滤消息通知
     *
     * @param projectID 工程ID
     * @return 消息ID列表
     */
    private NotifyByProjectID filterProject(Integer projectID) {
        List<NotifyListByProjectID> notifyListByProjectIDList = tbNotifyRelationMapper.selectNotifyByProjectID(projectID);
        return Optional.ofNullable(notifyListByProjectIDList)
                .map(n -> n.stream().collect(Collectors.groupingBy(NotifyListByProjectID::getType)))
                .map(n -> new NotifyByProjectID()
                        .setDataList(Optional.ofNullable(n.get(DataDeviceWarnType.DATA.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getProjectID).toList()).orElse(null))
                        .setDeviceList(Optional.ofNullable(n.get(DataDeviceWarnType.DEVICE.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getProjectID).toList()).orElse(null))
                        .setEventList(Optional.ofNullable(n.get(DataDeviceWarnType.EVENT.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getProjectID).toList()).orElse(null))
                        .setWorkOrderList(Optional.ofNullable(n.get(DataDeviceWarnType.WORK_ORDER.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getProjectID).toList()).orElse(null)))
                .orElse(null);
    }

    @Override
    public void setNotifyStatus(SetNotifyStatusParam pa, String accessToken) {
        userService.setNotifyStatus(pa, accessToken);
    }

//    @Override
//    public Map<String, Object> queryNotifyList(QueryNotifyListParam param, String accessToken) {
//        Map<String, Object> result = new HashMap<>();
//        TbNotifyRelation relation = tbNotifyRelationMapper.selectNotifyList(param);
//        Optional.ofNullable(relation).ifPresent(u -> {
//            final Integer companyID = param.getCompanyID();
//            final Integer relationID = u.getRelationID();
//            final Integer notifyID = u.getNotifyID();
//            Optional.of(new QueryNotifyDetailParam(companyID, notifyID))
//                    .map(w -> userService.queryNotifyDetail(w, accessToken))
//                    .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData)
//                    .filter(w -> SysNotify.Status.UNREAD.equals(w.getStatus())).ifPresent(w -> {
//                        switch (DataDeviceWarnType.fromCode(relation.getType())) {
//                            case DATA -> {
//                                DataWarnLatestInfo info = tbDataWarnLogMapper.selectDataWarnBaseInfoByID(relationID);
//                                info.setNotifyID(notifyID);
//                                result.put("dataWarn", info);
//                            }
//                            case DEVICE -> {
//                                DeviceWarnLatestInfo info = new DeviceWarnLatestInfo();
//                                TbDeviceWarnLog tbDeviceWarnLog = tbDeviceWarnLogMapper.selectById(relationID);
//                                info.setWarnLogID(tbDeviceWarnLog.getId());
//                                info.setWarnName("设备离线");
//                                info.setWarnTime(tbDeviceWarnLog.getWarnTime());
//                                info.setDeviceToken(tbDeviceWarnLog.getDeviceSerial());
//                                info.setNotifyID(notifyID);
//                                fillDeviceInfo(info, companyID);
//                                result.put("deviceWarn", info);
//                            }
//                        }
//                    });
//        });
//        return result;
//    }

    /**
     * 格式化短信参数内容，防止超过20个字符
     */
    private String formatSmsParam(String value) {
        if (value != null) {
            return value.substring(0, Math.min(value.length(), 20));
        }
        return StrUtil.EMPTY;
    }

    /**
     * 填充设备信息
     *
     * @param info      设备信息
     * @param companyID 公司ID
     */
    private void fillDeviceInfo(DeviceWarnLatestInfo info, Integer companyID) {
        List<DeviceWarnLatestInfo> infoList = List.of(info);
        Set<String> deviceTokens = Set.of(info.getDeviceToken());
        Map<String, TbVideoDevice> deviceTokenMap = tbVideoDeviceMapper.selectList(new LambdaQueryWrapper<TbVideoDevice>()
                .in(TbVideoDevice::getDeviceToken, deviceTokens)).stream().collect(Collectors
                .toMap(TbVideoDevice::getDeviceSerial, Function.identity()));
        TransferUtil.INSTANCE.applyDeviceBase(infoList,
                () -> QueryDeviceBaseInfoParam.builder().deviceTokens(deviceTokens).companyID(companyID).build(),
                DeviceWarnLatestInfo::getDeviceToken,
                (e, device) -> {
                    String deviceToken = e.getDeviceToken();
                    if (deviceTokenMap.containsKey(e.getDeviceToken())) {
                        e.setDeviceType(DeviceWarnDeviceType.VIDEO_DEVICE.getCode());
                        Optional.of(deviceToken).map(deviceTokenMap::get).map(TbVideoDevice::getDeviceType).ifPresent(e::setDeviceModel);
                    } else {
                        e.setDeviceType(DeviceWarnDeviceType.IOT_DEVICE.getCode());
                        e.setDeviceModel(device.getProductName());
                    }
                });
    }
}