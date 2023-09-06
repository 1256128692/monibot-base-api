package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorFileMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoCaptureMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.RedisCompanyInfoDao;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.YsTokenDao;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessProtocolType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.HikPtzCommandEnum;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkChannelInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DeviceBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.device.ys.YsUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    private final RedisCompanyInfoDao redisCompanyInfoDao;

    private final IotService iotService;

    private final TbSensorMapper sensorMapper;

    private final TbVideoCaptureMapper videoCaptureMapper;

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
                    Map<String, String> response = hkVideoService.controllingPtz(withSensorIDInfo.getDeviceSerial(), 0, command, null, null);
                    hkVideoService.controllingPtz(withSensorIDInfo.getDeviceSerial(), 1, command, null, null);
                    if (!"0".equals(response.getOrDefault("code", "1"))) {
                        return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, response.get("msg"));
                    }
                }
                default -> log.error("新平台云台操作暂未实现，平台: {}", platformType.name());
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
        switch (baseInfo.getAccessPlatform()) {
            case 0 -> {
                //ys
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
            case 1 -> {
                //hik
                String command = HikPtzCommandEnum.getByYsDirection(pa.getDirection()).getCommand();
                Map<String, String> startResponse = hkVideoService.controllingPtz(deviceSerial, 0, command, null, null);
                Map<String, String> endResponse = hkVideoService.controllingPtz(deviceSerial, 1, command, null, null);
                if (!"0".equals(startResponse.getOrDefault("code", "1"))) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, startResponse.get("msg"));
                }
                if (!"0".equals(endResponse.getOrDefault("code", "1"))) {
                    return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, startResponse.get("msg"));
                }
            }
            default -> {
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "该类型视频摄像头暂时无法进行云台操作");
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
                if (deviceInfoWrapper != null && deviceInfoWrapper.getMsg().equals("设备不存在")) {
                    // 查询不到则直接添加
                    YsResultWrapper ysResultWrapper = ysService.addDevice(ysToken, addVideoList.get(i).getDeviceSerial(), addVideoList.get(i).getValidateCode());
                    if (!ysResultWrapper.getMsg().equals("操作成功")) {
                        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "添加萤石云平台设备失败,设备序列号为:" + addVideoList.get(i).getDeviceSerial());
                    }
                    deviceInfoWrapper = ysService.getDeviceInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                    deviceChannelInfo = ysService.getDeviceChannelInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                    if (null == deviceInfoWrapper.getData() || CollectionUtil.isNullOrEmpty(deviceChannelInfo.getData())) {
                        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "未查到萤石云对应数据,设备序列号为:" + addVideoList.get(i).getDeviceSerial());
                    }
                } else {
                    deviceChannelInfo = ysService.getDeviceChannelInfo(ysToken, addVideoList.get(i).getDeviceSerial());
                    if (null == deviceInfoWrapper.getData() || CollectionUtil.isNullOrEmpty(deviceChannelInfo.getData())) {
                        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "未查到萤石云对应数据,设备序列号为:" + addVideoList.get(i).getDeviceSerial());
                    }
                }

                videoDeviceInfoList.add(VideoDeviceInfo.ysToNewValue(deviceInfoWrapper.getData(),
                        deviceChannelInfo.getData(), addVideoList.get(i), pa));
            }

        }

        // 海康数据
        if (pa.getAccessPlatform() == AccessPlatformType.HAI_KANG.getValue()) {
            String yyyyMMdd = DateUtil.format(DateUtil.date(), "yyyyMMdd");
            for (int i = 0; i < addVideoList.size(); i++) {
                Integer num = i + 1;
                HkDeviceInfo hkDeviceInfo = hkVideoService.queryDevice(addVideoList.get(i).getDeviceSerial());
                if (hkDeviceInfo == null) {
                    return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "未查到海康对应数据,设备序列号为:" + addVideoList.get(i).getDeviceSerial());
                }

                videoDeviceInfoList.add(VideoDeviceInfo.hkToNewValue(hkDeviceInfo, addVideoList.get(i), pa, "HIK" + yyyyMMdd + num.toString()));
            }
        }
        int successInsertCount = videoDeviceMapper.batchInsert(videoDeviceInfoList);

        if (successInsertCount > 0) {
            CreateMultipleDeviceParam iotRequest = new CreateMultipleDeviceParam();
            iotRequest.setCompanyID(pa.getCompanyID());

            if (pa.getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
                videoDeviceInfoList.forEach(v -> {
                    iotRequest.getDeviceList().add(new CreateMultipleDeviceItem(fileConfig.getYsProductID(),
                            v.getDeviceToken(), v.getDeviceName()));
                });
            } else {
                videoDeviceInfoList.forEach(v -> {
                    iotRequest.getDeviceList().add(new CreateMultipleDeviceItem(fileConfig.getHkProductID(),
                            v.getDeviceToken(), v.getDeviceName()));
                });
            }
            ResultWrapper<Boolean> multipleDevice = iotService.createMultipleDevice(iotRequest,
                    fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret(), pa.getToken());
            if (!multipleDevice.apiSuccess()) {
                return ResultWrapper.withCode(ResultCode.SUCCESS, "设备在iot服务已存在");
            }
        }
        return ResultWrapper.successWithNothing();
    }

    @Override
    public PageUtil.Page<VideoDevicePageInfo> queryVideoDevicePage(QueryVideoDevicePageParam pa) {

        Page<VideoDevicePageInfo> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        IPage<VideoDevicePageInfo> pageData = videoDeviceMapper.queryPageByCondition(page, pa.getDeviceSerial(), pa.getDeviceStatus(), pa.getAllocationStatus(),
                pa.getOwnedCompanyID(), pa.getProjectID(), pa.getBegin(), pa.getEnd());
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return PageUtil.Page.empty();
        }

        pageData.getRecords().forEach(record -> {
            record.setAccessPlatformStr(AccessPlatformType.getDescriptionByValue(record.getAccessPlatform()));
            record.setAccessProtocolStr(AccessProtocolType.getDescriptionByValue(record.getAccessProtocol()));
            if (redisCompanyInfoDao.selectCompanyInfo(record.getCompanyID()) != null) {
                record.setCompanyName(redisCompanyInfoDao.selectCompanyInfo(record.getCompanyID()).getFullName());
            }
        });

        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public VideoDeviceBaseInfoV2 queryYsVideoDeviceInfo(QueryYsVideoDeviceInfoParam param) {
        final String ysToken = getYsToken();
        final TbVideoDevice device = param.getTbVideoDevice();
        final String deviceSerial = device.getDeviceSerial();
        final Integer channelNo = Integer.parseInt(JSONUtil.parseObj(param.getTbSensor().getExValues()).getStr("ysChannelNo"));
        final VideoDeviceBaseInfoV2 build = VideoDeviceBaseInfoV2.build(device);
        YsResultWrapper<YsCapacityInfo> capacityInfo = ysService.capacity(ysToken, deviceSerial);
        YsResultWrapper<YsStreamUrlInfo> baseStreamInfo = ysService.getStreamInfo(ysToken, deviceSerial,
                channelNo, 1, null, null, "1", 2, null, null,
                null, null, null);
        Optional.ofNullable(baseStreamInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
                .map(YsStreamUrlInfo::getUrl).ifPresent(build::setBaseUrl);
        Optional.ofNullable(capacityInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
                .map(YsCapacityInfo::toMap).ifPresent(build::setCapabilitySet);

        // whether set {@code hdUrl} decided by {@code supportRateLimit} capability.
        if (build.getCapabilitySet().get("supportRateLimit") == 1) {
            YsResultWrapper<YsStreamUrlInfo> hdStreamInfo = ysService.getStreamInfo(ysToken, deviceSerial,
                    channelNo, 1, null, null, "1", 1, null, null,
                    null, null, null);
            Optional.ofNullable(hdStreamInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
                    .map(YsStreamUrlInfo::getUrl).ifPresent(build::setHdUrl);
        }
        return build;
    }

    @Override
    public String queryYsVideoPlayBack(QueryYsVideoPlayBackParam param) {
        String ysToken = getYsToken();
        TbVideoDevice device = param.getTbVideoDevice();
        Integer channelNo = Integer.parseInt(JSONUtil.parseObj(param.getTbSensor().getExValues()).getStr("ysChannelNo"));
        String startTime = DateUtil.format(param.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
        String stopTime = DateUtil.format(param.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        YsResultWrapper<YsStreamUrlInfo> streamInfo = ysService.getStreamInfo(ysToken, device.getDeviceSerial(), channelNo,
                null, null, null, param.getYsVideoType(), 2, startTime, stopTime, null, null, null);
        return Optional.of(streamInfo).filter(YsResultWrapper::callSuccess).map(YsResultWrapper::getData)
                .map(YsStreamUrlInfo::getUrl).orElseThrow(() -> new IllegalArgumentException("萤石云第三方接口调用失败!"));
    }

    @Override
    public List<VideoDeviceInfoV1> queryVideoDeviceList(QueryVideoDeviceListParam pa) {

        List<VideoDeviceInfoV1> list = videoDeviceMapper.queryListByCondition(pa.getDeviceSerialList());

        if (CollectionUtil.isNullOrEmpty(list)) {
            return Collections.emptyList();
        }

        List<VideoCaptureBaseInfo> sensorInfoList = sensorMapper.queryListByCondition(list.stream().map(VideoDeviceInfoV1::getVideoDeviceID).collect(Collectors.toList()));

        // 根据协议去转换json对象
        list.forEach(v -> {
            v.setAccessPlatformStr(AccessPlatformType.getDescriptionByValue(v.getAccessPlatform()));
            if (!CollectionUtils.isNotEmpty(sensorInfoList)) {
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
                                singleVideoSensorList.add(VideoCaptureBaseInfo.fromYsChannelInfo(ysChannelInfo, v.getDeviceName(), i + 1));
                            }
                        }
                    } else {
                        if (!CollectionUtil.isNullOrEmpty(v.getYsChannelInfoList())) {
                            List<YsChannelInfo> filteredYsChannelInfoList = v.getYsChannelInfoList().stream()
                                    .filter(ys -> v.getSensorList().stream().noneMatch(sensor -> sensor.getChannelNo().equals(ys.getChannelNo())))
                                    .collect(Collectors.toList());

                            for (int i = 0; i < filteredYsChannelInfoList.size(); i++) {
                                YsChannelInfo ysChannelInfo = filteredYsChannelInfoList.get(i);
                                // 添加到 singleVideoSensorList
                                singleVideoSensorList.add(VideoCaptureBaseInfo.fromYsChannelInfo(ysChannelInfo, v.getDeviceName(), i + 1));
                                singleVideoSensorList.addAll(v.getSensorList());
                            }
                        }
                    }
                }
            } else {
                if (!StringUtil.isNullOrEmpty(v.getExValue())) {
                    JSON json = JSONUtil.parse(v.getExValue());
                    HkChannelInfo hkChannelInfo = json.toBean(HkChannelInfo.class);
                    v.setHkChannelInfo(hkChannelInfo);
                    if (CollectionUtil.isNullOrEmpty(v.getSensorList())) {
                        singleVideoSensorList.add(VideoCaptureBaseInfo.fromHkChannelInfo(hkChannelInfo, v.getDeviceName()));
                    }
                }
            }
            v.setSensorList(singleVideoSensorList);
            v.setDeviceChannelNum(singleVideoSensorList.size());
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

        // 2. 删除关联的传感器
        sensorMapper.deleteByVedioIDList(videoIDList);

        // 3. 删除抓拍配置
        videoCaptureMapper.deleteByVedioIDList(pa.getDeviceSerialList());

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
            iotService.deleteDevice(build,
                    fileConfig.getAuthAppKey(),
                    fileConfig.getAuthAppSecret());
        }


        // TODO,因为目前没有验证码,暂时不验证删除萤石云设备
//        pa.getTbVideoDevices().forEach(d -> {
//            if (d.getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
//                ysService.deleteDevice(getYsToken(), d.getDeviceSerial());
//            }
//        });


        return ResultWrapper.successWithNothing();
    }

    @Override
    public VideoDeviceInfoV1 queryHkVideoDeviceBaseInfo(QueryHkVideoDeviceBaseInfoParam pa) {

        HkDeviceInfo hkDeviceInfo = hkVideoService.queryDevice(pa.getDeviceSerial());
        if (hkDeviceInfo == null) {
            return null;
        }
        VideoDeviceInfoV1 vo = new VideoDeviceInfoV1();
        vo.setDeviceChannelNum(1);
        vo.setAccessChannelNum(1);
        vo.setDeviceName(hkDeviceInfo.getCameraName());
        vo.setDeviceType(hkDeviceInfo.getCameraTypeName());
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
        ResultWrapper<Boolean> booleanResultWrapper = iotService.updateDeviceInfoBatch(param, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
        if (!booleanResultWrapper.apiSuccess() || !booleanResultWrapper.getData()) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "修改物联网视频设备失败,原因:"+booleanResultWrapper.getMsg());
        }

        // 3. 修改萤石云数据库,(海康无接口支持,不用修改)
        for (int i = 0; i < pa.getUpdateVideoList().size(); i++) {
            if (pa.getUpdateVideoList().get(i).getAccessPlatform().equals(AccessPlatformType.YING_SHI.getValue())) {
                YsResultWrapper ysResultWrapper = ysService.updateDevice(getYsToken(),
                        pa.getUpdateVideoList().get(i).getDeviceSerial(), pa.getUpdateVideoList().get(i).getDeviceName());
                if (!ysResultWrapper.getCode().equals("200")) {
                    return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "修改萤石云平台设备失败,设备序列号为:"
                            + pa.getUpdateVideoList().get(i).getDeviceSerial() + ",失败原因为:"+ysResultWrapper.getMsg());
                }
            }
        }

        return ResultWrapper.successWithNothing();
    }
}
