package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorFileMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.RedisCompanyInfoDao;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.YsTokenDao;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessProtocolType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItem4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.TbMonitorTypeFieldWithItemID;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.device.ys.YsUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
        //TODO compatible with hik video device
        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {

            String ysToken = getYsToken();
            YsResultWrapper ysResultWrapper = ysService.startPtz(ysToken, liveInfos.get(0).getSeqNo(), Integer.valueOf(liveInfos.get(0).getYsChannelNo()), pa.getDirection(),
                    DefaultConstant.YS_DEFAULT_SPEED);
            ysService.stopPtz(ysToken, liveInfos.get(0).getSeqNo(), Integer.valueOf(liveInfos.get(0).getYsChannelNo()), pa.getDirection());
            if (!ysResultWrapper.callSuccess()) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, ysResultWrapper.getMsg());
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
                //TODO
            }
            default -> {
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "该类型视频摄像头暂时无法进行云台操作");
            }
        }
        return ResultWrapper.successWithNothing();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addVideoDeviceList(AddVideoDeviceListParam pa) {

        List<VideoDeviceBaseInfo> addVideoList = pa.getAddVideoList();
        String ysToken = getYsToken();

        List<VideoDeviceInfo> videoDeviceInfoList = new LinkedList<>();
        // 萤石云数据
        if (pa.getAccessPlatform() == AccessPlatformType.YING_SHI.getValue()) {
            addVideoList.forEach(a -> {
                // 先去查询萤石云接口
                YsResultWrapper<YsDeviceInfo> deviceInfoWrapper = ysService.getDeviceInfo(ysToken, a.getDeviceSerial());
                YsResultWrapper<List<YsChannelInfo>> deviceChannelInfo = null;
                if (deviceInfoWrapper != null && deviceInfoWrapper.getMsg().equals("设备不存在")) {
                    // 查询不到则直接添加
                    YsResultWrapper ysResultWrapper = ysService.addDevice(ysToken, a.getDeviceSerial(), a.getValidateCode());
                    if (!ysResultWrapper.getMsg().equals("操作成功")) {
                        throw new RuntimeException("添加萤石云平台设备失败");
                    }
                    deviceInfoWrapper = ysService.getDeviceInfo(ysToken, a.getDeviceSerial());
                    deviceChannelInfo = ysService.getDeviceChannelInfo(ysToken, a.getDeviceSerial());
                    if (null == deviceInfoWrapper.getData() || CollectionUtil.isNullOrEmpty(deviceChannelInfo.getData())) {
                        throw new RuntimeException("未查到萤石云对应数据");
                    }
                } else {
                    deviceChannelInfo = ysService.getDeviceChannelInfo(ysToken, a.getDeviceSerial());
                    if (null == deviceInfoWrapper.getData() || CollectionUtil.isNullOrEmpty(deviceChannelInfo.getData())) {
                        throw new RuntimeException("未查到萤石云对应数据");
                    }
                }

                videoDeviceInfoList.add(VideoDeviceInfo.ysToNewValue(deviceInfoWrapper.getData(),
                        deviceChannelInfo.getData(), a, pa));

            });
        }

        // 海康数据
        if (pa.getAccessPlatform() == AccessPlatformType.HAI_KANG.getValue()) {
            addVideoList.forEach(a -> {
                HkDeviceInfo hkDeviceInfo = hkVideoService.queryDevice(a.getDeviceSerial());
                if (hkDeviceInfo == null) {
                    throw new RuntimeException("未查到海康对应数据");
                }

                videoDeviceInfoList.add(VideoDeviceInfo.hkToNewValue(hkDeviceInfo, a, pa));
            });
        }
        int successInsertCount = videoDeviceMapper.batchInsert(videoDeviceInfoList);

        if (successInsertCount > 0) {
            // TODO:同步到物联网平台
        }

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
}
