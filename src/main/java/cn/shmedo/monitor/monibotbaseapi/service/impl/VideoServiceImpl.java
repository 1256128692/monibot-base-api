package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.RedisCompanyInfoDao;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.YsTokenDao;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint.AddPresetPointParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.CompanyIDAndNameV2;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.CompanyIDListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDeviceWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.presetPoint.PresetPointWithDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DeviceBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDeviceWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.device.ys.YsUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final YsService ysService;
    private final FileConfig fileConfig;
    private final YsTokenDao ysTokenDao;

    private final TbSensorFileMapper sensorFileMapper;

    private final FileService fileService;

    private final HkVideoService hkVideoService;

    private final TbVideoDeviceMapper videoDeviceMapper;

    private final TbVideoDeviceSourceMapper videoDeviceSourceMapper;

    private final RedisCompanyInfoDao redisCompanyInfoDao;

    private final IotService iotService;
    private final TbVideoPresetPointMapper tbVideoPresetPointMapper;

    private final TbSensorMapper sensorMapper;

    private final TbVideoCaptureMapper videoCaptureMapper;

    private final TbProjectInfoMapper projectInfoMapper;

    private UserService userService;
    private final TbDeviceIntelLocationMapper tbDeviceIntelLocationMapper;

    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;
    private final ITbWarnNotifyConfigService iTbWarnNotifyConfigService;
    private final TbWarnNotifyConfigMapper tbWarnNotifyConfigMapper;

    private final TbWarnNotifyRelationMapper tbWarnNotifyRelationMapper;

    private final NotifyService notifyService;

    private final ITbDeviceWarnLogService tbDeviceWarnLogService;
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private RedisService monitorRedisService;

//    /**
//     * @see YsCapacityInfo
//     * @see YsService#capacity(String, String)
//     */
//    private static final String SUPPORT_RATE_LIMIT_KEY = "supportRateLimit";


    /**
     * 获取萤石云TOKEN，如果REDIS中没有，则从接口中获取
     *
     * @return
     */
    public String getYsToken() {
        String token = ysTokenDao.getToken();
        if (StrUtil.isNotBlank(token)) {
            return token;
        } else {
            YsResultWrapper<YsTokenInfo> tokenInfoYsResultWrapper =
                    ysService.getToken(fileConfig.getYsAppKey(), fileConfig.getYsSecret());
            if (tokenInfoYsResultWrapper == null || StrUtil.isBlank(tokenInfoYsResultWrapper.getCode())
                    || tokenInfoYsResultWrapper.getData() == null || tokenInfoYsResultWrapper.getData().getAccessToken() == null
                    || (!YsCode.SUCCESS.equals(tokenInfoYsResultWrapper.getCode()))) {
                throw new RuntimeException("ys service error:" + tokenInfoYsResultWrapper);
            }
            String httpToken = tokenInfoYsResultWrapper.getData().getAccessToken();
            ysTokenDao.setToken(httpToken);
            return httpToken;
        }
    }

    @Override
    public QueryVideoBaseInfoResult queryVideoBaseInfo(QueryVideoBaseInfoParam param) {
        String ysToken = getYsToken();
        YsResultWrapper<YsDeviceInfo> deviceInfoWrapper = ysService.getDeviceInfo(ysToken, param.getVideoSn());
        if (deviceInfoWrapper == null || StrUtil.isBlank(deviceInfoWrapper.getCode())
                || deviceInfoWrapper.getData() == null ||
                (!YsCode.SUCCESS.equals(deviceInfoWrapper.getCode()))) {
            throw new RuntimeException("ys service error:" + deviceInfoWrapper);
        }
        return QueryVideoBaseInfoResult.valueOf(deviceInfoWrapper.getData());
    }


    @Override
    public List<VideoMonitorPointLiveInfo> queryVideoMonitorPointLiveInfo(QueryVideoMonitorPointLiveInfoParam pa) {

        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();

        String ysToken = getYsToken();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            liveInfos.forEach(item -> {
                String baseUrl = YsUtil.getEzOpenAddress(item.getSeqNo(), false, item.getYsChannelNo());
                String hdUrl = YsUtil.getEzOpenAddress(item.getSeqNo(), true, item.getYsChannelNo());
                item.setBaseUrl(baseUrl);
                item.setHdUrl(hdUrl);
                item.setYsToken(ysToken);
            });

            return liveInfos;
        }
        return Collections.emptyList();
    }

    @Override
    public HistoryLiveInfo queryVideoMonitorPointHistoryLiveInfo(QueryVideoMonitorPointHistoryLiveInfoParam pa) {
        HistoryLiveInfo vo = new HistoryLiveInfo();
        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            String ysToken = getYsToken();
            String ezOpenHistoryAddress = YsUtil.getEzOpenHistoryAddress(liveInfos.get(0).getSeqNo(), liveInfos.get(0).getYsChannelNo(), pa.getBeginTime(),
                    pa.getEndTime());
            vo.setHistoryLiveAddress(ezOpenHistoryAddress);
            vo.setYsToken(ysToken);
        }

        return vo;
    }

    @Override
    public ResultWrapper<Object> panControlVideoPoint(PanControlVideoPointParam pa) {
        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        VideoDeviceWithSensorIDInfo withSensorIDInfo = pa.getWithSensorIDInfo();
        AccessPlatformType platformType = AccessPlatformType.getByValue(withSensorIDInfo.getAccessPlatform());
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            switch (platformType) {
                case YING_SHI -> {
                    String ysToken = getYsToken();
                    YsResultWrapper ysResultWrapper = ysService.startPtz(ysToken, liveInfos.get(0).getSeqNo(), Integer.valueOf(liveInfos.get(0).getYsChannelNo()), pa.getDirection(),
                            DefaultConstant.YS_DEFAULT_SPEED);
                    ysService.stopPtz(ysToken, liveInfos.get(0).getSeqNo(), Integer.valueOf(liveInfos.get(0).getYsChannelNo()), pa.getDirection());
                    if (!ysResultWrapper.callSuccess()) {
                        return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, ysResultWrapper.getMsg());
                    }
                }
                case HAI_KANG -> {
                    String command = HikPtzCommandEnum.getByYsDirection(pa.getDirection()).getCommand();
                    Map<String, String> response = hkVideoService.controllingPtz(withSensorIDInfo.getDeviceSerial(),
                            DefaultConstant.HikVideoConstant.HIK_PTZ_ACTION_START, command,
                            DefaultConstant.HikVideoConstant.HIK_DEFAULT_PTZ_SPEED, null);
                    hkVideoService.controllingPtz(withSensorIDInfo.getDeviceSerial(), DefaultConstant.HikVideoConstant.HIK_PTZ_ACTION_END,
                            command, DefaultConstant.HikVideoConstant.HIK_DEFAULT_PTZ_SPEED, null);
                    if (!DefaultConstant.HikVideoParamKeys.HIK_SUCCESS_CODE.equals(response.getOrDefault("code", "1"))) {
                        return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, response.get(DefaultConstant.HikVideoParamKeys.HIK_MSG));
                    }
                }
                default -> {
                    log.error("该平台云台操作暂未实现，平台: {}", platformType.getDescription());
                    return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, platformType.getDescription() + "视频摄像头云台操作功能暂不可用");
                }
            }
        }
        return ResultWrapper.successWithNothing();
    }

    @Override
    public List<VideoMonitorPointPictureInfo> queryVideoMonitorPointPictureInfo(QueryVideoMonitorPointPictureInfoParam pa) {

        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            List<VideoMonitorPointPictureInfo> list = sensorFileMapper.selectListByIDAndTime(liveInfos.get(0).getSensorID(), pa.getBeginTime(), pa.getEndTime());
            List<String> filePathList = list.stream().map(VideoMonitorPointPictureInfo::getFilePath).collect(Collectors.toList());
            List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(filePathList, liveInfos.get(0).getCompanyID());
            if (!CollectionUtil.isNullOrEmpty(fileUrlList)) {
                list.forEach(item -> {
                    FileInfoResponse fileInfoResponse = fileUrlList.stream().filter(pojo -> pojo.getFilePath().equals(item.getFilePath())).findFirst().orElse(null);
                    assert fileInfoResponse != null;
                    item.setFilePath(fileInfoResponse.getAbsolutePath());
                });
            }

            return list.stream()
                    .sorted(Comparator.comparing(VideoMonitorPointPictureInfo::getUploadTime).reversed())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public ResultWrapper<Object> panControlCompanyVideoPoint(PanControlCompanyVideoPointParam pa) {
        final VideoCompanyViewBaseInfo baseInfo = pa.getBaseInfo();
        final String deviceSerial = baseInfo.getDeviceSerial();
        final Integer deviceChannel = pa.getDeviceChannel();
        AccessPlatformType platformType = AccessPlatformType.getByValue(baseInfo.getAccessPlatform().byteValue());
        switch (platformType) {
            case YING_SHI -> {
                String ysToken = getYsToken();
                YsResultWrapper<?> startWrapper = ysService.startPtz(ysToken, deviceSerial, deviceChannel, pa.getDirection(), DefaultConstant.YS_DEFAULT_SPEED);
                YsResultWrapper<?> endWrapper = ysService.stopPtz(ysToken, deviceSerial, deviceChannel, pa.getDirection());
                if (!startWrapper.callSuccess()) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, startWrapper.getMsg());
                }
                if (!endWrapper.callSuccess()) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, endWrapper.getMsg());
                }
            }
            case HAI_KANG -> {
                String command = HikPtzCommandEnum.getByYsDirection(pa.getDirection()).getCommand();
                Map<String, String> startResponse = hkVideoService.controllingPtz(deviceSerial, DefaultConstant.HikVideoConstant.HIK_PTZ_ACTION_START, command, DefaultConstant.HikVideoConstant.HIK_DEFAULT_PTZ_SPEED, null);
                Map<String, String> endResponse = hkVideoService.controllingPtz(deviceSerial, DefaultConstant.HikVideoConstant.HIK_PTZ_ACTION_END, command, DefaultConstant.HikVideoConstant.HIK_DEFAULT_PTZ_SPEED, null);
                if (!DefaultConstant.HikVideoParamKeys.HIK_SUCCESS_CODE.equals(startResponse.getOrDefault(DefaultConstant.HikVideoParamKeys.HIK_CODE, "-1"))) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, startResponse.get(DefaultConstant.HikVideoParamKeys.HIK_MSG));
                }
                if (!DefaultConstant.HikVideoParamKeys.HIK_SUCCESS_CODE.equals(endResponse.getOrDefault(DefaultConstant.HikVideoParamKeys.HIK_CODE, "-1"))) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, startResponse.get(DefaultConstant.HikVideoParamKeys.HIK_MSG));
                }
            }
            default -> {
                log.error("该平台云台操作暂未实现，平台: {}", platformType.getDescription());
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, platformType.getDescription() + "视频摄像头云台操作功能暂不可用");
            }
        }
        return ResultWrapper.successWithNothing();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultWrapper<Object> addVideoDeviceList(AddVideoDeviceListParam pa) {

        List<VideoDeviceBaseInfo> addVideoList = pa.getAddVideoList();
        String ysToken = getYsToken();

        List<VideoDeviceInfo> videoDeviceInfoList = new LinkedList<>();
        // 萤石云数据
        if (pa.getAccessPlatform() == AccessPlatformType.YING_SHI.getValue()) {
            for (int i = 0; i < addVideoList.size(); i++) {
                // 先去查询萤石云接口
                YsResultWrapper<YsDeviceInfo> deviceInfoWrapper = ysService.getDeviceInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                YsResultWrapper<List<YsChannelInfo>> deviceChannelInfo = null;
                if (deviceInfoWrapper != null && deviceInfoWrapper.getCode().equals(DefaultConstant.YS_DEVICE_ERROR_CODE_NO_EXIST)) {
                    // 查询不到则直接添加
                    YsResultWrapper ysResultWrapper = ysService.addDevice(ysToken, addVideoList.get(i).getDeviceSerial(), addVideoList.get(i).getValidateCode());
                    if (!ysResultWrapper.getMsg().equals(DefaultConstant.YS_DEVICE_NORMAL_MSG)) {
                        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "添加萤石云平台设备失败,设备序列号为:" + addVideoList.get(i).getDeviceSerial());
                    }
                    deviceInfoWrapper = ysService.getDeviceInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                    deviceChannelInfo = ysService.getDeviceChannelInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                    if (null == deviceInfoWrapper.getData()) {
                        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "设备SN号输入错误，请核实后重新输入！");
                    }
                } else {
                    deviceChannelInfo = ysService.getDeviceChannelInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                    if (null == deviceInfoWrapper.getData()) {
                        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "设备SN号输入错误，请核实后重新输入！");
                    }
                }

                videoDeviceInfoList.add(VideoDeviceInfo.ysToNewValue(deviceInfoWrapper.getData(),
                        deviceChannelInfo.getData(), addVideoList.get(i), pa, i + 1));
            }

        }

        // 海康数据
        if (pa.getAccessPlatform() == AccessPlatformType.HAI_KANG.getValue()) {
            String formatStar = DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss");
            for (int i = 0; i < addVideoList.size(); i++) {
                Integer num = i + 1;
                HkDeviceInfo hkDeviceInfo = hkVideoService.queryDevice(addVideoList.get(i).getDeviceSerial());
                if (hkDeviceInfo == null) {
                    return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "设备SN号输入错误，请核实后重新输入！");
                }

                videoDeviceInfoList.add(VideoDeviceInfo.hkToNewValue(hkDeviceInfo, addVideoList.get(i), pa, "HIK" + formatStar + num.toString()));
            }
        }


        CreateMultipleDeviceParam iotRequest = new CreateMultipleDeviceParam();
        iotRequest.setCompanyID(pa.getCompanyID());

        if (pa.getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
            videoDeviceInfoList.forEach(v -> {
                iotRequest.getDeviceList().add(new CreateMultipleDeviceItem(fileConfig.getYsProductID(),
                        v.getDeviceToken(), v.getDeviceToken()));
            });
        } else {
            videoDeviceInfoList.forEach(v -> {
                iotRequest.getDeviceList().add(new CreateMultipleDeviceItem(fileConfig.getHkProductID(),
                        v.getDeviceToken(), v.getDeviceName()));
            });
        }
        iotService.createMultipleDevice(iotRequest, pa.getToken());

        ResultWrapper<List<DeviceBaseInfo>> deviceBaseInfoWrapper = iotService.queryDeviceBaseInfo(QueryDeviceBaseInfoParam.builder()
                .companyID(pa.getCompanyID())
                .deviceTokens(videoDeviceInfoList.stream().map(VideoDeviceInfo::getDeviceToken).collect(Collectors.toSet()))
                .build());

        if (deviceBaseInfoWrapper.apiSuccess() && !CollectionUtil.isNullOrEmpty(deviceBaseInfoWrapper.getData())) {
            videoDeviceInfoList.forEach(v -> {
                List<DeviceBaseInfo> dataList = deviceBaseInfoWrapper.getData();
                DeviceBaseInfo deviceBaseInfo = dataList.stream().filter(d -> d.getDeviceToken().equals(v.getDeviceToken())).findFirst().orElse(null);
                if (deviceBaseInfo != null) {
                    v.getTbVideoDeviceSourceList().forEach(v1 -> v1.setIotUniqueToken(deviceBaseInfo.getUniqueToken()));
                }
            });

            int successInsertCount = videoDeviceMapper.batchInsert(videoDeviceInfoList);

            if (successInsertCount != 0) {
                videoDeviceSourceMapper.batchInsert(videoDeviceInfoList.stream()
                        .map(VideoDeviceInfo::getTbVideoDeviceSourceList)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
            }
        }

        return ResultWrapper.successWithNothing();
    }

    @Override
    public PageUtil.PageWithMap<VideoDevicePageInfo> queryVideoDevicePage(QueryVideoDevicePageParam pa) {

        Page<VideoDevicePageInfo> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        IPage<VideoDevicePageInfo> pageData = videoDeviceMapper.queryPageByCondition(page, pa.getDeviceSerial(), pa.getFuzzyItem(), pa.getDeviceStatus(), pa.getAllocationStatus(),
                pa.getOwnedCompanyID(), pa.getProjectID(), pa.getBegin(), pa.getEnd());
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return PageUtil.PageWithMap.empty();
        }
        Integer onlineCount = videoDeviceMapper.queryOnlineCount(pa.getDeviceSerial(), pa.getFuzzyItem(), pa.getDeviceStatus(), pa.getAllocationStatus(),
                pa.getOwnedCompanyID(), pa.getProjectID(), pa.getBegin(), pa.getEnd());

        CompanyIDListParam param = new CompanyIDListParam();
        param.setCompanyIDList(pageData.getRecords().stream().map(VideoDevicePageInfo::getCompanyID).distinct().collect(Collectors.toList()));
        ResultWrapper<List<CompanyIDAndNameV2>> resultWrapper = userService.listCompanyIDName(param, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
        List<CompanyIDAndNameV2> companyInfoList;
        if (resultWrapper.apiSuccess()) {
            companyInfoList = resultWrapper.getData();
        } else {
            companyInfoList = null;
        }

        List<VideoDeviceSourceBaseInfo> videoDeviceSourceBaseInfos = videoDeviceSourceMapper.selectByDeviceSerialList(pageData.getRecords().stream().map(VideoDevicePageInfo::getDeviceSerial).collect(Collectors.toList()));

        pageData.getRecords().forEach(record -> {
            if (!CollectionUtil.isNullOrEmpty(videoDeviceSourceBaseInfos)) {
                record.setChannelNoList(videoDeviceSourceBaseInfos.stream()
                        .filter(v -> v.getDeviceSerial().equals(record.getDeviceSerial()))
                        .map(VideoDeviceSourceBaseInfo::getChannelCode).collect(Collectors.toList()));
            }
            record.setAccessPlatformStr(AccessPlatformType.getDescriptionByValue(record.getAccessPlatform()));
            record.setAccessProtocolStr(AccessProtocolType.getDescriptionByValue(record.getAccessProtocol()));
            if (!CollectionUtil.isNullOrEmpty(companyInfoList)) {
                record.setCompanyName(Objects.requireNonNull(companyInfoList.stream().filter(c -> c.getCompanyID().equals(record.getCompanyID())).findFirst().orElse(null)).getCompanyName());
            }
        });

        return new PageUtil.PageWithMap<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal(),
                Map.of("onlineCount", onlineCount));
    }

    @Override
    public VideoDeviceBaseInfoV2 queryYsVideoDeviceInfo(QueryYsVideoDeviceInfoParam param) {
        final String ysToken = getYsToken();
        final TbVideoDevice device = param.getTbVideoDevice();
        final String deviceSerial = device.getDeviceSerial();
        final String channelNo = param.channelNo();
        final VideoDeviceBaseInfoV2 build = VideoDeviceBaseInfoV2.build(device);
        Optional.of(deviceSerial).map(videoCaptureMapper::selectByDeviceSerial).filter(CollUtil::isNotEmpty)
                .map(u -> u.get(0)).map(TbVideoCapture::getImageCapture).ifPresent(build::setCaptureStatus);
        build.setBaseUrl(YsUtil.getEzOpenAddress(deviceSerial, false, channelNo));
        build.setHdUrl(YsUtil.getEzOpenAddress(deviceSerial, true, channelNo));
        build.setYsToken(ysToken);
        YsResultWrapper<YsCapacityInfo> capacityInfo = ysService.capacity(ysToken, deviceSerial);
//        YsResultWrapper<YsStreamUrlInfo> baseStreamInfo = ysService.getStreamInfo(ysToken, deviceSerial,
//                channelNo, 1, null, 300, "1", 2, null, null,
//                null, null, null);
//        Optional.ofNullable(baseStreamInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
//                .map(YsStreamUrlInfo::getUrl).ifPresent(build::setBaseUrl);
        Optional.ofNullable(capacityInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
                .map(YsCapacityInfo::toMap).ifPresent(build::setCapabilitySet);

        // whether set {@code hdUrl} decided by {@code supportRateLimit} capability.
//        if (build.getCapabilitySet().get(SUPPORT_RATE_LIMIT_KEY) == 1) {
//            YsResultWrapper<YsStreamUrlInfo> hdStreamInfo = ysService.getStreamInfo(ysToken, deviceSerial,
//                    channelNo, 1, null, 300, "1", 1, null, null,
//                    null, null, null);
//            Optional.ofNullable(hdStreamInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
//                    .map(YsStreamUrlInfo::getUrl).ifPresent(build::setHdUrl);
//        }
        return build;
    }

    @Override
    public Map<String, String> queryYsVideoPlayBack(QueryYsVideoPlayBackParam param) {
        String ysToken = getYsToken();
        TbVideoDevice device = param.getTbVideoDevice();
        String channelNo = param.channelNo();
//        String startTime = DateUtil.format(param.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
//        String stopTime = DateUtil.format(param.getEndTime(), "yyyy-MM-dd HH:mm:ss");
//        YsResultWrapper<YsStreamUrlInfo> streamInfo = ysService.getStreamInfo(ysToken, device.getDeviceSerial(), channelNo,
//                null, null, null, param.getYsVideoType(), 2, startTime, stopTime, null, null, null);
//        return Optional.of(streamInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
//                .map(YsStreamUrlInfo::getUrl).orElseThrow(() -> new IllegalArgumentException("萤石云第三方接口调用失败!"));
        return Map.of("baseUrl", YsUtil.getEzPlayBackAddress(device.getDeviceSerial(), channelNo,
                        param.getRecordLocation() == 0, param.getBeginTime()),
                "ysToken", ysToken);
    }

    @Override
    public List<VideoDeviceInfoV1> queryVideoDeviceList(QueryVideoDeviceListParam pa) {

        List<VideoDeviceInfoV1> list = videoDeviceMapper.queryListByDeviceSerialListAndCompanyID(
                pa.getDeviceSerialList(), pa.getCompanyID().equals(DefaultConstant.MD_ID) ? null : pa.getCompanyID(), pa.getDeviceStatus());

        if (CollectionUtil.isNullOrEmpty(list)) {
            return Collections.emptyList();
        }

        List<VideoCaptureBaseInfo> sensorInfoList = sensorMapper.queryListByCondition(list.stream().map(VideoDeviceInfoV1::getVideoDeviceID).collect(Collectors.toList()));

        if (!CollectionUtil.isNullOrEmpty(sensorInfoList)) {
            sensorInfoList.forEach(s -> {
                if (!StringUtil.isNullOrEmpty(s.getExValues())) {
                    Dict dict = JSONUtil.toBean(s.getExValues(), Dict.class);
                    if (dict.get("imageCapture") != null) {
                        s.setImageCapture((Boolean) dict.get("imageCapture"));
                    }
                }
            });
        }

        // 根据协议去转换json对象
        list.forEach(v -> {
            v.setAccessPlatformStr(AccessPlatformType.getDescriptionByValue(v.getAccessPlatform()));
            if (CollectionUtils.isNotEmpty(sensorInfoList)) {
                v.setSensorList(sensorInfoList.stream().filter(s -> s.getVideoDeviceID().equals(v.getVideoDeviceID())).collect(Collectors.toList()));
            }

            List<VideoCaptureBaseInfo> singleVideoSensorList = new LinkedList<>();
            if (v.getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
                if (!StringUtil.isNullOrEmpty(v.getExValue())) {
                    JSONArray jsonArray = JSONUtil.parseArray(v.getExValue());
                    v.setYsChannelInfoList(jsonArray.toList(YsChannelInfo.class));
                    // 如果传感器列表为空，遍历 ysChannelInfoList 并转换成 VideoCaptureBaseInfo
                    if (CollectionUtil.isNullOrEmpty(v.getSensorList())) {
                        if (!CollectionUtil.isNullOrEmpty(v.getYsChannelInfoList())) {
                            for (int i = 0; i < v.getYsChannelInfoList().size(); i++) {
                                YsChannelInfo ysChannelInfo = v.getYsChannelInfoList().get(i);
                                // 添加到 singleVideoSensorList
                                singleVideoSensorList.add(VideoCaptureBaseInfo.fromYsChannelInfo(ysChannelInfo, v.getDeviceName()));
                            }
                        }
                        v.setDeviceChannelNum(0);
                    } else {
                        if (!CollectionUtil.isNullOrEmpty(v.getYsChannelInfoList())) {
                            List<YsChannelInfo> filteredYsChannelInfoList = v.getYsChannelInfoList().stream()
                                    .filter(ys -> v.getSensorList().stream().noneMatch(sensor -> sensor.getChannelNo().equals(ys.getChannelNo())))
                                    .collect(Collectors.toList());
                            v.setDeviceChannelNum(v.getSensorList().size());

                            for (int i = 0; i < filteredYsChannelInfoList.size(); i++) {
                                YsChannelInfo ysChannelInfo = filteredYsChannelInfoList.get(i);
                                // 添加到 singleVideoSensorList
                                singleVideoSensorList.add(VideoCaptureBaseInfo.fromYsChannelInfo(ysChannelInfo, v.getDeviceName()));
                            }
                            singleVideoSensorList.addAll(v.getSensorList());
                        }
                    }
                }
                v.setSensorList(singleVideoSensorList);

            } else {
                v.setDeviceChannelNum(1);
                if (!StringUtil.isNullOrEmpty(v.getExValue())) {
                    JSON json = JSONUtil.parse(v.getExValue());
                    HkChannelInfo hkChannelInfo = json.toBean(HkChannelInfo.class);
                    v.setHkChannelInfo(hkChannelInfo);
                    if (CollectionUtil.isNullOrEmpty(v.getSensorList())) {
                        singleVideoSensorList.add(VideoCaptureBaseInfo.fromHkChannelInfo(hkChannelInfo, v.getDeviceName()));
                        v.setSensorList(singleVideoSensorList);
                    }
                }
            }
        });
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object deleteVideoDeviceList(DeleteVideoDeviceParam pa) {

        // 1. 删除视频设备
        if (CollectionUtil.isNullOrEmpty(pa.getTbVideoDevices())) {
            return ResultWrapper.successWithNothing();
        }

        List<Integer> videoIDList = pa.getTbVideoDevices().stream()
                .map(TbVideoDevice::getID).collect(Collectors.toList());
        videoDeviceMapper.deleteBatchIds(videoIDList);

        // 2.删除传感器
        sensorMapper.deleteBatchByDeviceSerialList(pa.getDeviceSerialList());

        // 3. 删除抓拍配置
        videoCaptureMapper.deleteByVedioIDList(pa.getDeviceSerialList());

        // 删除通道视频设备
        videoDeviceSourceMapper.deleteByDeviceSerialList(pa.getDeviceSerialList());

        // 4. 删除物联网平台设备
        ResultWrapper<List<DeviceBaseInfo>> listResultWrapper = iotService.queryDeviceBaseInfo(QueryDeviceBaseInfoParam.builder()
                .companyID(pa.getCompanyID())
                .deviceTokens(pa.getTbVideoDevices().stream().map(TbVideoDevice::getDeviceToken).collect(Collectors.toSet()))
                .build());

        if (listResultWrapper.apiSuccess() && !CollectionUtil.isNullOrEmpty(listResultWrapper.getData())) {
            DeleteDeviceParam build = DeleteDeviceParam.builder().companyID(pa.getCompanyID())
                    .idList(listResultWrapper.getData().stream().map(DeviceBaseInfo::getDeviceID).collect(Collectors.toList()))
                    .saveData(false)
                    .build();
            iotService.deleteDevice(build);
        }


        // ,因为目前没有验证码,暂时不验证删除萤石云设备
//        pa.getTbVideoDevices().forEach(d -> {
//            if (d.getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
//                ysService.deleteDevice(getYsToken(), d.getDeviceSerial());
//            }
//        });


        return ResultWrapper.successWithNothing();
    }

    @Override
    public VideoDeviceInfoV1 queryHkVideoDeviceBaseInfo(QueryHkVideoDeviceBaseInfoParam pa) {

        VideoDeviceInfoV1 vo = new VideoDeviceInfoV1();
        vo.setDeviceChannelNum(1);
        vo.setAccessChannelNum(1);
        vo.setDeviceName(pa.getHkDeviceInfo().getCameraName());
        vo.setDeviceType(pa.getHkDeviceInfo().getCameraTypeName());
        vo.setDeviceSerial(pa.getDeviceSerial());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object updateVideoDeviceList(UpdateVideoDeviceParam pa) {

        // 1. 修改中台数据库
        Integer successNum = videoDeviceMapper.batchUpdate(pa.getUpdateVideoList());
        if (successNum != 1) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "修改中台视频设备失败");
        }

        UpdateDeviceInfoBatchParam param = new UpdateDeviceInfoBatchParam();
        List<DeviceInfoV1> deviceInfoV1List = new LinkedList<>();
        pa.getVideoDeviceList().stream().forEach(item -> {
            DeviceInfoV1 deviceInfoV1 = new DeviceInfoV1();
            deviceInfoV1.setDeviceToken(item.getDeviceToken());
            VideoDeviceInfoV2 videoDeviceInfoV2 = pa.getUpdateVideoList().stream().filter(u -> u.getDeviceSerial().equals(item.getDeviceSerial())).findFirst().orElse(null);
            assert videoDeviceInfoV2 != null;
            deviceInfoV1.setDeviceName(videoDeviceInfoV2.getDeviceName());
            deviceInfoV1List.add(deviceInfoV1);
        });
        param.setList(deviceInfoV1List);
        // 2. 修改物联网数据库
        ResultWrapper<Boolean> booleanResultWrapper = iotService.updateDeviceInfoBatch(param);
        if (!booleanResultWrapper.apiSuccess() || !booleanResultWrapper.getData()) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "修改物联网视频设备失败,原因:" + booleanResultWrapper.getMsg());
        }

        // 3. 修改萤石云数据库,(海康无接口支持,不用修改)
        for (int i = 0; i < pa.getUpdateVideoList().size(); i++) {
            if (pa.getUpdateVideoList().get(i).getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
                YsResultWrapper ysResultWrapper = ysService.updateDevice(getYsToken(),
                        pa.getUpdateVideoList().get(i).getDeviceSerial(), pa.getUpdateVideoList().get(i).getDeviceName());
                if (!ysResultWrapper.getCode().equals("200")) {
                    return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "修改萤石云平台设备失败,设备序列号为:"
                            + pa.getUpdateVideoList().get(i).getDeviceSerial() + ",失败原因为:" + ysResultWrapper.getMsg());
                }
            }
        }

        return ResultWrapper.successWithNothing();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object saveVideoDeviceSensorList(SaveVideoDeviceSensorParam pa) {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();

        // 1. 筛选出来不同企业的视频设备,然后同步到iot进行转移
        List<VideoDeviceInfoV4> transferVideoDeviceList = pa.getVideoDeviceList().stream().filter(i -> !i.getCompanyID().equals(pa.getCompanyID())).collect(Collectors.toList());
        if (!CollectionUtil.isNullOrEmpty(transferVideoDeviceList)) {
            Object o = handlerTransferDevice(transferVideoDeviceList, pa.getCompanyID());
            if (ObjectUtil.isNotNull(o)) {
                return o;
            }
        }

        // 2. 直接批量修改视频设备所属公司,以及所属工程
        videoDeviceMapper.batchUpdateCompanyAndProject(pa.getVideoDeviceList(), pa.getCompanyID());

        // 3. 批量生成传感器设备,过滤出来传感器ID为空的数据,为新增数据,  不为空的数据,为批量修改数据
        List<SensorBaseInfoV1> insertSensorList = new LinkedList<>();
        List<SensorBaseInfoV1> updateSensorList = new LinkedList<>();
        // 抓拍配置
        List<SensorBaseInfoV1> captureSensorList = new LinkedList<>();
        List<SensorBaseInfoV1> finalCaptureSensorList = captureSensorList;
        pa.getList().forEach(v -> {
            List<SensorBaseInfoV1> addSensorList = v.getAddSensorList();
            if (!CollectionUtil.isNullOrEmpty(addSensorList)) {
                Integer maxDisplayOrder = sensorMapper.queryMaxDisplayOrderByMonitorType(MonitorType.VIDEO.getKey());
                for (int i = 0; i < addSensorList.size(); i++) {
                    if (addSensorList.get(i).getSensorID() == null) {
                        if (addSensorList.get(i).getSensorEnable()) {
                            insertSensorList.add(SensorBaseInfoV1.createNewSensor(addSensorList.get(i),
                                    subjectID, v, maxDisplayOrder + i + 1));
                        }
                    } else {
                        updateSensorList.add(SensorBaseInfoV1.createUpdateSensor(addSensorList.get(i), subjectID, v));
                    }
                }
                finalCaptureSensorList.clear();
                finalCaptureSensorList.addAll(insertSensorList);
                finalCaptureSensorList.addAll(updateSensorList);
            }
        });
        // 3.进行分流,传感器ID为空的新增,传感器ID不为空的修改
        if (!CollectionUtil.isNullOrEmpty(insertSensorList)) {
            sensorMapper.insertSensorList(insertSensorList);
        }
        if (!CollectionUtil.isNullOrEmpty(updateSensorList)) {
            sensorMapper.updateSensorList(updateSensorList);
        }

        // 4.批量插入定时抓拍
        if (!CollectionUtil.isNullOrEmpty(captureSensorList)) {
            List<SensorBaseInfoV1> sensorList = sensorMapper.selectListByNameAndProjectID(captureSensorList.stream().map(SensorBaseInfoV1::getSensorName).collect(Collectors.toList()),
                    captureSensorList.get(0).getProjectID());
            captureSensorList.forEach(c -> {
                SensorBaseInfoV1 sensorBaseInfoV1 = sensorList.stream().filter(s -> s.getSensorName().equals(c.getSensorName())).findFirst().orElse(null);
                if (sensorBaseInfoV1 != null) {
                    c.setSensorID(sensorBaseInfoV1.getSensorID());
                    c.setDeviceSerial(sensorBaseInfoV1.getDeviceSerial());
                }
            });
            videoCaptureMapper.insertBatch(captureSensorList);
        }

        return ResultWrapper.successWithNothing();
    }

    @Override
    public PageUtil.Page<VideoDeviceBaseInfoV1> queryYsVideoDeviceList(QueryYsVideoDeviceParam pa) {

        String ysToken = getYsToken();
        int pageSize = 50;
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        YsResultPageWrapper<VideoDeviceBaseInfoV1> baseDeviceInfoByPage = ysService.getBaseDeviceInfoByPage(
                ysToken,
                0,
                1);
        if (!baseDeviceInfoByPage.callSuccess() || baseDeviceInfoByPage.getData() == null) {
            return PageUtil.Page.empty();
        }

        // 创建查询任务列表
        List<Callable<List<VideoDeviceBaseInfoV1>>> tasks = new ArrayList<>();
        // 总设备数
        int totalDevices = baseDeviceInfoByPage.getPage().getTotal();

        for (int pageStart = 0; pageStart < totalDevices; pageStart += pageSize) {
            int currentPage = (pageStart / pageSize);
            tasks.add(() -> {
                // 发起查询请求
                YsResultPageWrapper<VideoDeviceBaseInfoV1> result = ysService.getBaseDeviceInfoByPage(getYsToken(), currentPage, pageSize);
                List<VideoDeviceBaseInfoV1> deviceList = result.getData();
                if (deviceList == null) {
                    // 如果为null，创建一个空列表
                    deviceList = new ArrayList<>();
                }
                return deviceList;
            });
        }

        // 并发执行任务
        try {
            List<Future<List<VideoDeviceBaseInfoV1>>> futures = executorService.invokeAll(tasks);

            // 处理任务结果
            List<VideoDeviceBaseInfoV1> totalYsDeviceList = new ArrayList<>();
            for (Future<List<VideoDeviceBaseInfoV1>> future : futures) {
                List<VideoDeviceBaseInfoV1> deviceList = future.get();
                totalYsDeviceList.addAll(deviceList);
            }

            // 关闭线程池
            executorService.shutdown();

            List<String> deviceSerialList = totalYsDeviceList.stream().map(VideoDeviceBaseInfoV1::getDeviceSerial).collect(Collectors.toList());
            List<VideoDeviceInfoV1> videoDeviceInfoV1s = videoDeviceMapper.queryListByCondition(deviceSerialList);

            if (!CollectionUtil.isNullOrEmpty(videoDeviceInfoV1s)) {
                List<String> deviceSerialsToRemove = videoDeviceInfoV1s.stream().map(VideoDeviceInfoV1::getDeviceSerial).collect(Collectors.toList());
                totalYsDeviceList.removeIf(device -> deviceSerialsToRemove.contains(device.getDeviceSerial()));
            }

            int totalPageSize = totalYsDeviceList.size() / pa.getPageSize();
            if (totalYsDeviceList.size() % pa.getPageSize() > 0) {
                // 如果余数大于0，总页数加1
                totalPageSize++;
            }
            // 根据参数进行分页
            PageUtil.Page<VideoDeviceBaseInfoV1> page = PageUtil.page(totalYsDeviceList, pa.getPageSize(), pa.getCurrentPage());

            // 返回分页结果
            return new PageUtil.Page<>(totalPageSize, page.currentPageData(), totalYsDeviceList.size());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PageUtil.Page<VideoDeviceBaseInfoV1> queryHkVideoDeviceList(QueryHkVideoDeviceParam pa) {

        List<VideoDeviceBaseInfoV1> allVideoDevices = new ArrayList<>();

        // 首次查询
        HkMonitorPointInfo hkMonitorPointInfo = hkVideoService.queryHkVideoPage(1);

        if (hkMonitorPointInfo == null || hkMonitorPointInfo.getTotal() == 0) {
            return PageUtil.Page.empty();
        }

        allVideoDevices.addAll(convertMonitorPointDetailToVideoDeviceBaseInfoV1(hkMonitorPointInfo.getList()));

        if (hkMonitorPointInfo.getTotal() > 1000) {
            int total = hkMonitorPointInfo.getTotal();
            int pageSize = 1000;
            int jkTotalPageSize = total / pageSize;
            if (total % pageSize > 0) {
                // 如果余数大于0，总页数加1
                jkTotalPageSize++;
            }

            // 从第二页开始查询，因为第一页已经查询过了
            for (int pageNo = 2; pageNo <= jkTotalPageSize; pageNo++) {
                hkMonitorPointInfo = hkVideoService.queryHkVideoPage(pageNo);
                if (hkMonitorPointInfo != null) {
                    allVideoDevices.addAll(convertMonitorPointDetailToVideoDeviceBaseInfoV1(hkMonitorPointInfo.getList()));
                }
            }
        }

        List<String> deviceSerialList = allVideoDevices.stream().map(VideoDeviceBaseInfoV1::getDeviceSerial).collect(Collectors.toList());
        List<VideoDeviceInfoV1> videoDeviceInfoV1s = videoDeviceMapper.queryListByCondition(deviceSerialList);

        if (!CollectionUtil.isNullOrEmpty(videoDeviceInfoV1s)) {
            List<String> deviceSerialsToRemove = videoDeviceInfoV1s.stream().map(VideoDeviceInfoV1::getDeviceSerial).collect(Collectors.toList());
            allVideoDevices.removeIf(device -> deviceSerialsToRemove.contains(device.getDeviceSerial()));
        }

        // 根据参数进行分页
        PageUtil.Page<VideoDeviceBaseInfoV1> page = PageUtil.page(allVideoDevices, pa.getPageSize(), pa.getCurrentPage());

        int totalPageSize = allVideoDevices.size() / pa.getPageSize();
        if (allVideoDevices.size() % pa.getPageSize() > 0) {
            // 如果余数大于0，总页数加1
            totalPageSize++;
        }
        // 返回分页结果
        return new PageUtil.Page<>(totalPageSize, page.currentPageData(), allVideoDevices.size());

    }

    @Override
    public PageUtil.Page<VideoSensorFileInfo> queryCapturePage(QueryCapturePageParam pa) {

        Page<VideoDevicePageInfo> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        IPage<VideoSensorFileInfo> pageData = sensorFileMapper.queryPageByCondition(page, pa.getVideoDeviceSourceID(),
                pa.getBegin(), pa.getEnd());
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return PageUtil.Page.empty();
        }

        List<String> filePathList = pageData.getRecords().stream().map(VideoSensorFileInfo::getFilePath).collect(Collectors.toList());
        List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(filePathList, pa.getCompanyID());

        pageData.getRecords().forEach(record -> {
            FileInfoResponse fileInfoResponse = fileUrlList.stream().filter(f -> f.getFilePath().equals(record.getFilePath())).findFirst().orElse(null);
            if (fileInfoResponse != null) {
                record.setPath(fileInfoResponse.getAbsolutePath());
            }
        });

        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());

    }

    @Override
    public List<VideoSensorFileInfo> queryCaptureList(QueryCaptureParam pa) {

        List<VideoSensorFileInfo> videoSensorFileInfos = sensorFileMapper.selectListBySensorIDAndTime(pa);

        if (CollectionUtils.isEmpty(videoSensorFileInfos)) {
            return Collections.emptyList();
        }

        List<String> filePathList = videoSensorFileInfos.stream().map(VideoSensorFileInfo::getFilePath).collect(Collectors.toList());
        List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(filePathList, pa.getCompanyID());

        videoSensorFileInfos.forEach(record -> {
            FileInfoResponse fileInfoResponse = fileUrlList.stream().filter(f -> f.getFilePath().equals(record.getFilePath())).findFirst().orElse(null);
            if (fileInfoResponse != null) {
                record.setPath(fileInfoResponse.getAbsolutePath());
            }
        });
        return videoSensorFileInfos;
    }

    @Override
    public Boolean batchUpdateVideoDeviceStatus(BatchUpdateVideoDeviceStatusParam pa) {

        // 查询到所有萤石云设备,海康设备
        List<VideoDeviceBaseInfoV1> allVideoDevices = new LinkedList<>();
        List<VideoDeviceBaseInfoV1> hkAllVideoDevices = new LinkedList<>();
        List<VideoDeviceBaseInfoV1> ysAllVideoDevices = new LinkedList<>();
        // 首次查询
        DeviceListResponse hkMonitorPointInfo = hkVideoService.queryHkVideoStatus(1);

        if (hkMonitorPointInfo != null && hkMonitorPointInfo.getTotal() != 0) {
            hkAllVideoDevices.addAll(convertHkDeviceStatusInfoToVideoDeviceBaseInfoV1(hkMonitorPointInfo.getList()));

            if (hkMonitorPointInfo.getTotal() > 1000) {
                int total = hkMonitorPointInfo.getTotal();
                int pageSize = 1000;
                int jkTotalPageSize = total / pageSize;
                if (total % pageSize > 0) {
                    // 如果余数大于0，总页数加1
                    jkTotalPageSize++;
                }

                // 从第二页开始查询，因为第一页已经查询过了
                for (int pageNo = 2; pageNo <= jkTotalPageSize; pageNo++) {
                    hkMonitorPointInfo = hkVideoService.queryHkVideoStatus(pageNo);
                    if (hkMonitorPointInfo != null) {
                        hkAllVideoDevices.addAll(convertHkDeviceStatusInfoToVideoDeviceBaseInfoV1(hkMonitorPointInfo.getList()));
                    }
                }
            }
        }


        String ysToken = getYsToken();
        int pageSize = 50;
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        YsResultPageWrapper<VideoDeviceBaseInfoV1> baseDeviceInfoByPage = ysService.getBaseDeviceInfoByPage(
                ysToken,
                0,
                1);
        if (baseDeviceInfoByPage.callSuccess() && baseDeviceInfoByPage.getData() != null) {
            // 创建查询任务列表
            List<Callable<List<VideoDeviceBaseInfoV1>>> tasks = new ArrayList<>();
            // 总设备数
            int totalDevices = baseDeviceInfoByPage.getPage().getTotal();

            for (int pageStart = 0; pageStart < totalDevices; pageStart += pageSize) {
                int currentPage = (pageStart / pageSize);
                tasks.add(() -> {
                    // 发起查询请求
                    YsResultPageWrapper<VideoDeviceBaseInfoV1> result = ysService.getBaseDeviceInfoByPage(getYsToken(), currentPage, pageSize);
                    List<VideoDeviceBaseInfoV1> deviceList = result.getData();
                    if (deviceList == null) {
                        // 如果为null，创建一个空列表
                        deviceList = new ArrayList<>();
                    }
                    return deviceList;
                });
            }

            // 并发执行任务
            try {
                List<Future<List<VideoDeviceBaseInfoV1>>> futures = executorService.invokeAll(tasks);

                // 处理任务结果
                for (Future<List<VideoDeviceBaseInfoV1>> future : futures) {
                    List<VideoDeviceBaseInfoV1> deviceList = future.get();
                    ysAllVideoDevices.addAll(deviceList);
                }

                // 关闭线程池
                executorService.shutdown();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        allVideoDevices.addAll(ysAllVideoDevices);
        allVideoDevices.addAll(hkAllVideoDevices);

        List<TbDeviceWarnLog> tbDeviceWarnLogs = tbDeviceWarnLogMapper.selectList(null);
        List<TbVideoDevice> tbVideoDevices = videoDeviceMapper.selectAllList();
        if (CollectionUtil.isNullOrEmpty(tbVideoDevices) || CollectionUtil.isNullOrEmpty(allVideoDevices)) {
            return true;
        } else {
            // 使用CompletableFuture来异步执行比对任务
            CompletableFuture<Void> compareFuture = CompletableFuture.runAsync(() -> {
                tbVideoDevices.forEach(v -> {
                    VideoDeviceBaseInfoV1 videoDeviceBaseInfoV1 = allVideoDevices.stream()
                            .filter(total -> total.getDeviceSerial().equals(v.getDeviceSerial())).findFirst().orElse(null);

                    if (videoDeviceBaseInfoV1 != null) {

                        if ((videoDeviceBaseInfoV1.getStatus() != null && v.getProjectID() != null && v.getProjectID() != -1)) {
                            List<Integer> platformIDList = projectInfoMapper.selectPlatformListByProjectID(v.getProjectID());
                            if (!CollectionUtil.isNullOrEmpty(platformIDList)) {
                                platformIDList.forEach(platform -> {
                                    DateTime date = DateUtil.date();
                                    TbDeviceWarnLog tbDeviceWarnLog = tbDeviceWarnLogs.stream()
                                            .filter(deviceWarn -> deviceWarn.getDeviceToken().equals(v.getDeviceToken())
                                                    && deviceWarn.getPlatform().equals(platform)
                                                    && deviceWarn.getProjectID().equals(v.getProjectID())
                                                    && deviceWarn.getWarnEndTime() == null).findFirst().orElse(null);
                                    tbDeviceWarnLogService.saveDeviceWarnLog(new SaveDeviceWarnParam(
                                            v.getCompanyID(), platform, v.getProjectID(), date, v.getDeviceType(),
                                            v.getDeviceToken(), v.getDeviceSerial(), v.getProjectName(), "视频设备",
                                            tbDeviceWarnLog, videoDeviceBaseInfoV1.getStatus()
                                    ));

                                });
                            }
                        }

                        v.setDeviceStatus(videoDeviceBaseInfoV1.getStatus());
                    }
                });
            });

            // 等待异步任务完成
            compareFuture.join();

            if (tbVideoDevices.size() > 100) {

                List<List<TbVideoDevice>> splitList = ListUtil.split(tbVideoDevices, 100);
                for (int i = 0; i < splitList.size(); i++) {
                    videoDeviceMapper.batchUpdateDeviceStatus(splitList.get(i));
                }


            } else {
                videoDeviceMapper.batchUpdateDeviceStatus(tbVideoDevices);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public ResultWrapper<Object> addPresetPoint(AddPresetPointParam param) {
        final TbVideoDevice tbVideoDevice = param.getTbVideoDevice();
        final String deviceSerial = tbVideoDevice.getDeviceSerial();
        final Integer channelNo = param.getChannelNo();
        AccessPlatformType platformType = AccessPlatformType.getByValue(tbVideoDevice.getAccessPlatform());
        switch (platformType) {
            case YING_SHI -> {
                String ysToken = getYsToken();
                YsResultWrapper<Map<String, Integer>> ysResultPageWrapper = ysService.addPresetPoint(ysToken, deviceSerial, channelNo);
                Optional.of(ysResultPageWrapper).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
                        .filter(u -> u.containsKey(DefaultConstant.YS_PRESET_POINT_INDEX_KEY))
                        .map(u -> u.get(DefaultConstant.YS_PRESET_POINT_INDEX_KEY)).ifPresent(param::setPresetPointIndex);
            }
            case HAI_KANG -> {
                //acquire the video device maximum {@code presetPointIndex} currently,then execute the RPC to generate a preset point in hik platform.
                TbVideoPresetPoint maxPresetPoint = tbVideoPresetPointMapper.selectOne(new LambdaQueryWrapper<TbVideoPresetPoint>()
                        .eq(TbVideoPresetPoint::getVideoDeviceID, tbVideoDevice.getID()).orderByDesc(TbVideoPresetPoint::getPresetPointIndex).last("LIMIT 1"));
                Integer presetPointIndex = Objects.isNull(maxPresetPoint) || Objects.isNull(maxPresetPoint.getPresetPointIndex()) ?
                        1 : maxPresetPoint.getPresetPointIndex() + 1;
                Map<String, String> response = hkVideoService.managePresetPoint(deviceSerial, param.getPresetPointName(), presetPointIndex);
                if (!DefaultConstant.HikVideoParamKeys.HIK_SUCCESS_CODE.equals(response.getOrDefault(DefaultConstant.HikVideoParamKeys.HIK_CODE, "-1"))) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, response.get(DefaultConstant.HikVideoParamKeys.HIK_MSG));
                }
                param.setPresetPointIndex(presetPointIndex);
            }
            default -> {
                log.error("该平台预置点暂未实现，平台: {}", platformType.getDescription());
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, platformType.getDescription() + "视频摄像头预置点功能暂不可用");
            }
        }
        if (Objects.isNull(param.getPresetPointIndex())) {
            return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR,
                    AccessPlatformType.getDescriptionByValue(tbVideoDevice.getAccessPlatform()) + "新增预置点失败");
        }
        TbVideoPresetPoint point = new TbVideoPresetPoint();
        point.setPresetPointName(param.getPresetPointName());
        point.setChannelNo(channelNo);
        point.setVideoDeviceID(param.getVideoDeviceID());
        point.setPresetPointIndex(param.getPresetPointIndex());
        tbVideoPresetPointMapper.insert(point);
        return ResultWrapper.successWithNothing();
    }

    @Override
    @Transactional
    public ResultWrapper<Object> deletePresetPoint(List<PresetPointWithDeviceInfo> presetPointWithDeviceInfoList) {
        Map<AccessPlatformType, List<PresetPointWithDeviceInfo>> collect = presetPointWithDeviceInfoList.stream()
                .collect(Collectors.groupingBy(u -> AccessPlatformType.getByValue(u.getAccessPlatform().byteValue())));
        List<AccessPlatformType> unsupportPlatformList = collect.keySet().stream()
                .filter(u -> !(AccessPlatformType.YING_SHI.equals(u) || AccessPlatformType.HAI_KANG.equals(u))).toList();
        if (CollUtil.isNotEmpty(unsupportPlatformList)) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "存在不支持的平台,不支持平台名称列表: {}"
                    + unsupportPlatformList.stream().map(AccessPlatformType::getDescription).toList());
        }
        List<Integer> failedPresetIDList = new ArrayList<>();

        //ys
        String ysToken = getYsToken();
        List<PresetPointWithDeviceInfo> ysPresetPointWithDeviceInfoList = Optional.ofNullable(collect.get(AccessPlatformType.YING_SHI)).orElse(List.of());
        List<PresetPointWithDeviceInfo> deleteSuccessPresetPointWithDeviceInfoList = new ArrayList<>();
        for (PresetPointWithDeviceInfo ysInfo : ysPresetPointWithDeviceInfoList) {
            String deviceSerial = ysInfo.getDeviceSerial();
            Integer channelNo = ysInfo.getChannelNo();
            Integer presetPointIndex = ysInfo.getPresetPointIndex();
            YsResultWrapper ysResultWrapper = ysService.clearPresetPoint(ysToken, deviceSerial, channelNo, presetPointIndex);
            if (ysResultWrapper.callSuccess()) {
                deleteSuccessPresetPointWithDeviceInfoList.add(ysInfo);
            } else {
                log.error("萤石设备预置点删除失败,设备唯一标识:{}, 通道号:{}, 预置点编号:{}", deviceSerial, channelNo, presetPointIndex);
                failedPresetIDList.add(ysInfo.getPresetPointID());
            }
        }

        //hik
        List<PresetPointWithDeviceInfo> hikPresetPointWithDeviceInfoList = Optional.ofNullable(collect.get(AccessPlatformType.HAI_KANG)).orElse(List.of());
        for (PresetPointWithDeviceInfo hikInfo : hikPresetPointWithDeviceInfoList) {
            String deviceSerial = hikInfo.getDeviceSerial();
            Integer presetPointIndex = hikInfo.getPresetPointIndex();
            try {
                hkVideoService.deletePresetPoint(deviceSerial, presetPointIndex);
                deleteSuccessPresetPointWithDeviceInfoList.add(hikInfo);
            } catch (Exception e) {
                log.error("海康设备预置点删除失败,设备唯一标识:{}, 预置点编号:{}", deviceSerial, presetPointIndex);
                failedPresetIDList.add(hikInfo.getPresetPointID());
            }
        }
        Optional.of(deleteSuccessPresetPointWithDeviceInfoList).filter(CollUtil::isNotEmpty).map(u -> u.stream()
                .map(PresetPointWithDeviceInfo::getPresetPointID).toList()).ifPresent(tbVideoPresetPointMapper::deleteBatchIds);
        return CollUtil.isEmpty(failedPresetIDList) ? ResultWrapper.successWithNothing() :
                ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, "部分预置点删除失败,失败的预置点ID List:" + failedPresetIDList);
    }

    @Override
    public ResultWrapper<Object> movePresetPoint(PresetPointWithDeviceInfo info) {
        AccessPlatformType platformType = AccessPlatformType.getByValue(info.getAccessPlatform().byteValue());
        switch (platformType) {
            case YING_SHI -> {
                YsResultWrapper ysResultWrapper = ysService.movePresetPoint(getYsToken(), info.getDeviceSerial(), info.getChannelNo(), info.getPresetPointIndex());
                if (!ysResultWrapper.callSuccess()) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, ysResultWrapper.getMsg());
                }
            }
            case HAI_KANG -> {
                Map<String, String> response = hkVideoService.controllingPtz(info.getDeviceSerial(), DefaultConstant.HikVideoConstant.HIK_PTZ_ACTION_START,
                        HikPtzCommandEnum.GOTO_PRESET.getCommand(), DefaultConstant.HikVideoConstant.HIK_DEFAULT_PTZ_SPEED, info.getPresetPointIndex());
                if (!DefaultConstant.HikVideoParamKeys.HIK_SUCCESS_CODE.equals(response.getOrDefault(DefaultConstant.HikVideoParamKeys.HIK_CODE, "-1"))) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, response.get(DefaultConstant.HikVideoParamKeys.HIK_MSG));
                }
            }
            default -> {
                log.error("该平台移动到预置点暂未实现，平台: {}", platformType.getDescription());
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, platformType.getDescription() + "视频摄像头移动到预置点功能暂不可用");
            }
        }
        return ResultWrapper.successWithNothing();
    }

    @Override
    public List<VideoDeviceInfoV1> queryVideoDeviceListV1(QueryVideoDeviceListParam pa) {
        List<VideoDeviceInfoV1> list = videoDeviceMapper.queryListByDeviceSerialListAndCompanyID(
                pa.getDeviceSerialList(), pa.getCompanyID().equals(DefaultConstant.MD_ID) ? null : pa.getCompanyID(), pa.getDeviceStatus());

        if (CollectionUtil.isNullOrEmpty(list)) {
            return Collections.emptyList();
        }

        List<VideoCaptureBaseInfo> sensorInfoList = sensorMapper.queryListByDeviceSerialList(list.stream().map(VideoDeviceInfoV1::getDeviceSerial).collect(Collectors.toList()));

        list.forEach(v -> {
            v.setAccessPlatformStr(AccessPlatformType.getDescriptionByValue(v.getAccessPlatform()));
            if (CollectionUtils.isNotEmpty(sensorInfoList)) {
                v.setSensorList(sensorInfoList.stream().filter(s -> s.getDeviceSerial().equals(v.getDeviceSerial())).collect(Collectors.toList()));
            }

            List<VideoCaptureBaseInfo> singleVideoSensorList = new LinkedList<>();
            // 传感器列表
            List<VideoCaptureBaseInfo> sensorList = v.getSensorList().stream().filter(s -> s.getSensorID() != null).collect(Collectors.toList());
            // 通道列表
            List<VideoCaptureBaseInfo> notGenerateSensorList = v.getSensorList().stream().filter(s -> s.getSensorID() == null).collect(Collectors.toList());
            if (v.getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {

                // 如果传感器列表为空，根据通道号数量去转换传感器
                if (CollectionUtil.isNullOrEmpty(sensorList)) {
                    if (!CollectionUtil.isNullOrEmpty(v.getSensorList())) {
                        for (int i = 0; i < v.getSensorList().size(); i++) {
                            // 添加到 singleVideoSensorList
                            singleVideoSensorList.add(VideoCaptureBaseInfo.fromChannelInfo(v.getSensorList().get(i), v.getDeviceName()));
                        }
                    }
                    v.setDeviceChannelNum(0);
                } else {
                    v.setDeviceChannelNum(sensorList.size());
                    if (!CollectionUtil.isNullOrEmpty(notGenerateSensorList)) {
//                        v.setDeviceChannelNum(sensorList.size());
                        for (int i = 0; i < notGenerateSensorList.size(); i++) {
                            // 添加到 singleVideoSensorList
                            singleVideoSensorList.add(VideoCaptureBaseInfo.fromChannelInfo(notGenerateSensorList.get(i), v.getDeviceName()));
                        }
                    }
                    singleVideoSensorList.addAll(sensorList);
                }
                v.setSensorList(singleVideoSensorList);

            } else {
                v.setDeviceChannelNum(1);
                if (CollectionUtil.isNullOrEmpty(sensorList)) {
                    if (!CollectionUtil.isNullOrEmpty(v.getSensorList())) {
                        singleVideoSensorList.add(VideoCaptureBaseInfo.fromChannelInfo(v.getSensorList().get(0), v.getDeviceName()));
                        v.setSensorList(singleVideoSensorList);
                    }
                }
            }
        });
        return list;
    }

    @Override
    public Object saveVideoDeviceCaptureList(SaveVideoDeviceCaptureParam pa) {

        videoCaptureMapper.insertBatchByCaptureList(pa.getList());
        return ResultWrapper.successWithNothing();
    }

    @Override
    public Object saveVideoSensorList(SaveVideoDeviceSensorParam pa) {

        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();

        // 1. 筛选出来不同企业的视频设备,然后同步到iot进行转移
        List<VideoDeviceInfoV4> transferVideoDeviceList = pa.getVideoDeviceList().stream().filter(i -> !i.getCompanyID().equals(pa.getCompanyID())).collect(Collectors.toList());
        if (!CollectionUtil.isNullOrEmpty(transferVideoDeviceList)) {
            Object o = handlerTransferDevice(transferVideoDeviceList, pa.getCompanyID());
            if (ObjectUtil.isNotNull(o)) {
                return o;
            }
        }

        // 2. 直接批量修改视频设备所属公司,以及所属工程
        videoDeviceMapper.batchUpdateCompanyAndProject(pa.getVideoDeviceList(), pa.getCompanyID());

        // 3. 批量生成传感器设备,过滤出来传感器ID为空的数据,为新增数据,  不为空的数据,为批量修改数据
        List<SensorBaseInfoV1> insertSensorList = new LinkedList<>();
        List<SensorBaseInfoV1> updateSensorList = new LinkedList<>();
        pa.getList().forEach(v -> {
            List<SensorBaseInfoV1> addSensorList = v.getAddSensorList();
            if (!CollectionUtil.isNullOrEmpty(addSensorList)) {
                Integer maxDisplayOrder = sensorMapper.queryMaxDisplayOrderByMonitorType(MonitorType.VIDEO.getKey());
                for (int i = 0; i < addSensorList.size(); i++) {
                    if (addSensorList.get(i).getSensorID() == null) {
                        if (addSensorList.get(i).getSensorEnable()) {
                            insertSensorList.add(SensorBaseInfoV1.createNewSensor(addSensorList.get(i),
                                    subjectID, v, maxDisplayOrder + i + 1));
                        }
                    } else {
                        updateSensorList.add(SensorBaseInfoV1.createUpdateSensor(addSensorList.get(i), subjectID, v));
                    }
                }
            }
        });
        // 3.进行分流,传感器ID为空的新增,传感器ID不为空的修改
        if (!CollectionUtil.isNullOrEmpty(insertSensorList)) {
            sensorMapper.insertSensorList(insertSensorList);
        }
        if (!CollectionUtil.isNullOrEmpty(updateSensorList)) {
            sensorMapper.updateSensorList(updateSensorList);
        }

        return ResultWrapper.successWithNothing();
    }

    @Override
    public Object queryCaptureDate(QueryCaptureParam pa) {

        List<Date> captureDateList = sensorFileMapper.queryCaptureDate(pa);
        // 对列表中的日期进行格式化
        List<String> formattedDateList = captureDateList.stream()
                .map(date -> DateUtil.format(date, "yyyy-MM-dd"))
                .collect(Collectors.toList());

        // 对格式化后的列表进行去重
        return CollUtil.distinct(formattedDateList);
    }

    @Override
    public VideoDeviceDetailInfo QueryVideoDeviceDetail(QueryVideoDeviceDetailParam pa) {

        VideoDeviceDetailInfo info = videoDeviceMapper.queryDeviceDetail(
                pa.getDeviceSerial(), pa.getCompanyID().equals(DefaultConstant.MD_ID) ? null : pa.getCompanyID());

        List<VideoCaptureBaseInfoV2> videoDeviceSourceList = videoDeviceSourceMapper.selectByDeviceSerial(pa.getDeviceSerial());

        info.setVideoDeviceSourceList(videoDeviceSourceList);
        info.setAccessPlatformStr(AccessPlatformType.getByValue(info.getAccessPlatform()).getDescription());
        if (!CollectionUtil.isNullOrEmpty(videoDeviceSourceList)) {
            long count = videoDeviceSourceList.stream().filter(v -> v.getSensorID() != null).count();
            info.setDeviceChannelNum(Math.toIntExact(count));

            Collection<Object> areas = videoDeviceSourceList.stream().filter(e -> StrUtil.isNotEmpty(e.getLocation())).map(e -> {
                JSONObject json = JSONUtil.parseObj(e.getLocation());
                e.setLocation(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
                return e.getLocation();
            }).filter(Objects::nonNull).collect(Collectors.toSet());
            Map<String, String> areaMap = areas.isEmpty() ?
                    Collections.emptyMap() :
                    monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                            .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
            videoDeviceSourceList.forEach(item -> {
                if (!StringUtil.isNullOrEmpty(item.getLocation())) {
                    String location = areaMap.getOrDefault(item.getLocation(), null);
                    item.setLocationInfo(location);
                }
            });
        }
        // 设置设备状态
        info.setLocation(
                tbDeviceIntelLocationMapper.selectOne(
                        new LambdaQueryWrapper<TbDeviceIntelLocation>()
                                .eq(TbDeviceIntelLocation::getDeviceToken, info.getDeviceSerial())
                                .eq(TbDeviceIntelLocation::getType, IntelDeviceType4Location.VIDEO.getType())

                )
        );
        return info;
    }

    @Override
    public Object queryVideoDeviceListV2(QueryVideoDeviceListParam pa) {
        List<VideoBaseInfo> videoInfoList = videoDeviceMapper.selectListByCompanyID(pa.getCompanyID(), pa.getDeviceStatus(), pa.getQueryContent());
        if (CollectionUtil.isNullOrEmpty(videoInfoList)) {
            return Collections.emptyList();
        }

        List<VideoDeviceSourceBaseInfo> videoSourceInfoList = videoDeviceSourceMapper.selectByDeviceSerialList(videoInfoList.stream().map(VideoBaseInfo::getDeviceSerial).collect(Collectors.toList()));
        List<ProjectVideoInfo> list = new LinkedList<ProjectVideoInfo>();
        ProjectVideoInfo notAllocatedProject = new ProjectVideoInfo();
        notAllocatedProject.setProjectID(-1);
        notAllocatedProject.setProjectName("未分配工程");
        videoInfoList.forEach(videoInfo -> {
            if (videoInfo.getProjectID() == null) {
                videoInfo.setProjectID(-1);
            }
            videoInfo.setAccessPlatformStr(AccessPlatformType.getDescriptionByValue(videoInfo.getAccessPlatform()));
            if (!CollectionUtil.isNullOrEmpty(videoSourceInfoList)) {
                videoInfo.setVideoSourceInfoList(videoSourceInfoList.stream().filter(videoSourceInfo -> videoSourceInfo.getDeviceSerial().equals(videoInfo.getDeviceSerial())).collect(Collectors.toList()));
            }
        });

        list.addAll(projectInfoMapper.selectListByIDs(videoInfoList.stream().map(VideoBaseInfo::getProjectID).distinct().collect(Collectors.toList())));
        list.add(notAllocatedProject);
        list.forEach(i -> {
            i.setVideoInfoList(videoInfoList.stream().filter(v -> v.getProjectID().equals(i.getProjectID())).collect(Collectors.toList()));
        });
        return list;
    }

    private List<VideoDeviceBaseInfoV1> convertMonitorPointDetailToVideoDeviceBaseInfoV1(List<HkMonitorPointInfo.MonitorPointDetail> monitorPointDetails) {
        return monitorPointDetails.stream()
                .map(detail -> {
                    VideoDeviceBaseInfoV1 videoDevice = new VideoDeviceBaseInfoV1();
                    videoDevice.setDeviceSerial(detail.getCameraIndexCode());
                    videoDevice.setDeviceName(detail.getCameraName());
                    videoDevice.setDeviceType(detail.getCameraTypeName());
                    if (detail.getStatus() == null || detail.getStatus() == 0) {
                        videoDevice.setStatus(false);
                    } else if (detail.getStatus() == 1) {
                        videoDevice.setStatus(true);
                    } else {
                        videoDevice.setStatus(false);
                    }
                    return videoDevice;
                })
                .collect(Collectors.toList());
    }


    private List<VideoDeviceBaseInfoV1> convertHkDeviceStatusInfoToVideoDeviceBaseInfoV1(List<HkDeviceStatusInfo> monitorPointDetails) {
        return monitorPointDetails.stream()
                .map(detail -> {
                    VideoDeviceBaseInfoV1 videoDevice = new VideoDeviceBaseInfoV1();
                    videoDevice.setDeviceSerial(detail.getIndexCode());
                    if (detail.getOnline() == null || detail.getOnline() == 0) {
                        videoDevice.setStatus(false);
                    } else if (detail.getOnline() == 1) {
                        videoDevice.setStatus(true);
                    } else {
                        videoDevice.setStatus(false);
                    }
                    return videoDevice;
                })
                .collect(Collectors.toList());
    }


    /**
     * 处理转移iot设备
     */
    private Object handlerTransferDevice(List<VideoDeviceInfoV4> transferVideoDeviceList, Integer companyID) {

        ResultWrapper<List<DeviceBaseInfo>> listResultWrapper = iotService.queryDeviceBaseInfo(QueryDeviceBaseInfoParam.builder()
                .companyID(transferVideoDeviceList.get(0).getCompanyID())
                .deviceTokens(transferVideoDeviceList.stream().map(VideoDeviceInfoV4::getDeviceToken).collect(Collectors.toSet()))
                .build());
        if (!listResultWrapper.apiSuccess()) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "查询物联网设备失败,原因:" + listResultWrapper.getMsg());
        }
        if (!CollectionUtil.isNullOrEmpty(listResultWrapper.getData())) {
            Map<String, Integer> deviceTokenToIdMap = listResultWrapper.getData().stream()
                    .collect(Collectors.toMap(DeviceBaseInfo::getDeviceToken, DeviceBaseInfo::getDeviceID));

            transferVideoDeviceList.forEach(videoDeviceInfo -> {
                Integer deviceID = deviceTokenToIdMap.get(videoDeviceInfo.getDeviceToken());
                if (deviceID != null) {
                    videoDeviceInfo.setIotDeviceID(deviceID);
                }
            });
        }

        TransferDeviceParam param = TransferDeviceParam.builder()
                .deviceIDList(transferVideoDeviceList.stream().map(VideoDeviceInfoV4::getIotDeviceID).collect(Collectors.toList()))
                .companyID(companyID)
                .originalCompanyID(transferVideoDeviceList.get(0).getCompanyID())
                .build();

        ResultWrapper<Boolean> booleanResultWrapper = iotService.transferDevice(param);
        if (!booleanResultWrapper.apiSuccess() || !booleanResultWrapper.getData()) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "转移物联网设备失败");
        } else {
            return null;
        }
    }
}
