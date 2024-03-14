package cn.shmedo.monitor.monibotbaseapi.service.notify;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbNotifyRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.third.TbService;
import cn.shmedo.monitor.monibotbaseapi.model.dto.ListenerEventAppend;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataDeviceWarnType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DeviceWarnDeviceType;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.AddNotifyRelationRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.SetNotifyStatusParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.DeleteNotifyParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.MailNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.SmsNotify;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyByProjectID;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyListByProjectID;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyPageResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceWarnLatestInfo;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.notify.MdNotifyService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
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
    @Resource(name = "userService")
    private UserService userService;

    private final TbNotifyRelationMapper tbNotifyRelationMapper;
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
        List<NotifyListByProjectID> notifyListByProjectIDList = tbNotifyRelationMapper.selectNotifyByProjectID(param.getProjectID());
        Optional.ofNullable(notifyListByProjectIDList)
                .filter(CollectionUtil::isNotEmpty)
                .map(n -> n.stream().map(NotifyListByProjectID::getNotifyID).collect(Collectors.toList()))
                .ifPresent(param::setNotifyIDList);

        // 分页查询通知信息（条件过滤）
        PageUtil.Page<NotifyPageInfo> page = Optional.ofNullable(param.build())
                .map(u -> userService.queryNotifyPageList(u, accessToken))
                .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).orElse(PageUtil.Page.empty());

        // 查询未读通知统计信息
        long unReadCount;
        switch (SysNotify.Status.getStatus(param.getStatus())) {
            case UNREAD -> unReadCount = page.totalCount();
            case READ -> unReadCount = 0;
            default -> {
                // status为查询全部时，查询未读统计
                param.setStatus(0);
                param.setCurrentPage(1);
                ResultWrapper<PageUtil.Page<NotifyPageInfo>> unReadPage = userService.queryNotifyPageList(param.build(), accessToken);
                unReadCount = Optional.ofNullable(unReadPage)
                        .filter(ResultWrapper::apiSuccess)
                        .map(ResultWrapper::getData)
                        .filter(Objects::nonNull)
                        .map(PageUtil.Page::totalCount).orElse(0L);
            }
        }
        return new NotifyPageResponse.Page<>(page.totalPage(),
                notify(page.currentPageData(), notifyListByProjectIDList),
                page.totalCount(),
                unReadCount);
    }

    @Override
    public List<NotifyPageResponse> queryNotifyList(QueryNotifyListParam param, String accessToken) {
        // 工程级别过滤
        List<NotifyListByProjectID> notifyListByProjectIDList = tbNotifyRelationMapper.selectNotifyByProjectID(param.getProjectID());
        Optional.ofNullable(notifyListByProjectIDList)
                .filter(CollectionUtil::isNotEmpty)
                .map(n -> n.stream().map(NotifyListByProjectID::getNotifyID).collect(Collectors.toList()))
                .ifPresent(param::setNotifyIDList);

        // 远程调用查询符合条件的通知
        ResultWrapper<List<NotifyPageResponse>> resultWrapper = userService.queryNotifyList(param, accessToken);
        return notify(resultWrapper.getData(), notifyListByProjectIDList);
    }


    private List<NotifyPageResponse> notify(Collection<? extends NotifyPageInfo> notifyList,
                                            Collection<NotifyListByProjectID> notifyListByProjectIDList) {
        if (CollectionUtil.isEmpty(notifyList) || CollectionUtil.isEmpty(notifyListByProjectIDList)) {
            return Collections.emptyList();
        }

        Map<Integer, NotifyListByProjectID> idMap = notifyListByProjectIDList
                .stream().collect(Collectors.toMap(NotifyListByProjectID::getNotifyID, Function.identity()));

        // 通知记录关系
        Map<Integer, TbNotifyRelation> notifyRelationMap = Optional.of(notifyList)
                .map(u -> u.stream().map(NotifyPageInfo::getNotifyID).toList()).filter(CollUtil::isNotEmpty)
                .map(u -> new LambdaQueryWrapper<TbNotifyRelation>().in(TbNotifyRelation::getNotifyID, u))
                .map(tbNotifyRelationMapper::selectList).map(u -> u.stream().collect(Collectors
                        .toMap(TbNotifyRelation::getNotifyID, Function.identity()))).orElse(Map.of());

        // 平台服务
        RedisService authRedisService = SpringUtil.getBean(RedisConstant.AUTH_REDIS_SERVICE, RedisService.class);
        Map<String, TbService> tbServiceMap = Optional.of(authRedisService.getAll(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, TbService.class)).get();

        // 组装返回结果
        List<NotifyPageResponse> list = Optional.of(notifyList)
                .map(u -> u.stream().map(w -> {
                    NotifyPageResponse response = new NotifyPageResponse();
                    BeanUtil.copyProperties(w, response);
                    Optional.ofNullable(response.getNotifyID())
                            .map(notifyRelationMap::get)
                            .ifPresent(s -> {
                                response.setRelationID(s.getRelationID());
                                response.setRelationType(s.getType());
                            });
                    if (Objects.nonNull(response.getServiceID())) {
                        response.setServiceName(tbServiceMap.get(String.valueOf(response.getServiceID())).getServiceDesc());
                    }
                    return response;
                }).toList()).orElse(List.of());
        return Optional.of(list)
                .filter(CollectionUtil::isNotEmpty)
                .map(d -> {
                    d.forEach(item -> {
                        NotifyListByProjectID notifyListByProjectID = idMap.get(item.getNotifyID());
                        if (Objects.nonNull(item.getRelationType())) {
                            DataDeviceWarnType dataDeviceWarnType = DataDeviceWarnType.fromCode(item.getRelationType());
                            if (Objects.nonNull(notifyListByProjectID)) {
                                item.setProjectID(notifyListByProjectID.getProjectID());
                                switch (dataDeviceWarnType) {
                                    case DATA -> item.setDataInfo(new NotifyPageResponse.DataInfo()
                                            .setMonitorItemID(notifyListByProjectID.getMonitorItemID())
                                            .setMonitorPointID(notifyListByProjectID.getMonitorPointID()));
                                    case DEVICE -> item.setDeviceInfo(new NotifyPageResponse.DeviceInfo()
                                            .setHistoryFlag(notifyListByProjectID.getHistoryFlag()));
                                }
                            }
                        }
                    });
                    return d;
                }).orElse(Collections.emptyList());
    }

    /**
     * 根据工程ID过滤消息通知，projectID为空时查询全部项目下
     *
     * @param projectID 工程ID
     * @return 消息ID列表
     */
    @Deprecated
    private NotifyByProjectID filterProject(Integer projectID) {
        List<NotifyListByProjectID> notifyListByProjectIDList = tbNotifyRelationMapper.selectNotifyByProjectID(projectID);
        return Optional.ofNullable(notifyListByProjectIDList)
                .map(n -> n.stream().collect(Collectors.groupingBy(NotifyListByProjectID::getType)))
                .map(n -> new NotifyByProjectID()
                        .setDataList(Optional.ofNullable(n.get(DataDeviceWarnType.DATA.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getNotifyID).collect(Collectors.toSet())).orElse(null))
                        .setDeviceList(Optional.ofNullable(n.get(DataDeviceWarnType.DEVICE.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getNotifyID).collect(Collectors.toSet())).orElse(null))
                        .setEventList(Optional.ofNullable(n.get(DataDeviceWarnType.EVENT.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getNotifyID).collect(Collectors.toSet())).orElse(null))
                        .setWorkOrderList(Optional.ofNullable(n.get(DataDeviceWarnType.WORK_ORDER.getCode()))
                                .map(m -> m.stream().map(NotifyListByProjectID::getNotifyID).collect(Collectors.toSet())).orElse(null))
                        .setNotifyListByProjectIDList(notifyListByProjectIDList))
                .orElse(null);
    }

    @Override
    public void setNotifyStatus(SetNotifyStatusParam pa, String accessToken) {
        if (CollectionUtil.isEmpty(pa.getNotifyIDList())) {
            return;
        }
        pa.setUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        userService.setNotifyStatus(pa, config.getAuthAppKey(), config.getAuthAppSecret());
    }

    /**
     * 清空对应的数据报警消息
     *
     * @param notifyIDList 消息IDList
     * @param append       事件附加信息
     */
    @Override
    public void clearNotify(List<Integer> notifyIDList, ListenerEventAppend append) {
        tbNotifyRelationMapper.delete(new LambdaQueryWrapper<TbNotifyRelation>().in(TbNotifyRelation::getNotifyID, notifyIDList));
        userService.deleteNotify(new DeleteNotifyParam(append.companyID(), notifyIDList), append.accessToken());
    }

    @Override
    public List<Integer> addNotifyRelation(AddNotifyRelationRequest request) {
        List<TbNotifyRelation> list = request.toNotifyRelationList();
        tbNotifyRelationMapper.insertBatchSomeColumn(list);
        return list.stream().map(TbNotifyRelation::getId).toList();
    }

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