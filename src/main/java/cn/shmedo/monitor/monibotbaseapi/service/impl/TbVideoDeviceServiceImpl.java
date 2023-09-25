package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoDeviceService;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:20
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbVideoDeviceServiceImpl extends ServiceImpl<TbVideoDeviceMapper, TbVideoDevice> implements ITbVideoDeviceService {
    private final HkVideoService hkVideoService;

    @Override
    public List<VideoCompanyViewBaseInfo> queryVideoCompanyViewBaseInfo(QueryVideoCompanyViewBaseInfoParam param) {
        return this.baseMapper.selectVideoCompanyViewBaseInfo(param);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public List<VideoProjectViewBaseInfo> queryVideoProjectViewBaseInfo(QueryVideoProjectViewBaseInfo param) {
        List<VideoProjectViewBaseInfo> videoProjectViewBaseInfos = this.baseMapper.selectVideoProjectViewBaseInfo(param);
        return videoProjectViewBaseInfos.stream().peek(u -> u.getMonitorGroupDataList().stream()
                .map(VideoProjectViewSubGroupInfo::getMonitorPointDataList).flatMap(Collection::stream)
                .peek(VideoProjectViewPointInfo::afterProperties).toList()).toList();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public List<VideoProjectViewSubGroupInfo> queryVideoProjectViewBaseInfoV2(QueryVideoProjectViewBaseInfo param) {
        return this.baseMapper.selectVideoProjectViewBaseInfo(param).stream().map(VideoProjectViewBaseInfo::getMonitorGroupDataList)
                .flatMap(Collection::stream).peek(u -> u.getMonitorPointDataList().stream()
                        .peek(VideoProjectViewPointInfo::afterProperties).toList()).toList();
    }

    @Override
    public VideoDeviceBaseInfoV2 queryHikVideoDeviceInfo(QueryHikVideoDeviceInfoParam param) {
        TbVideoDevice device = param.getTbVideoDevice();
        VideoDeviceBaseInfoV2 build = VideoDeviceBaseInfoV2.build(device);
        final String deviceSerial = device.getDeviceSerial();
        final String url = hkVideoService.getStreamUrl(deviceSerial, param.getStreamType(), DefaultConstant.HikVideoParamKeys.HIK_PROTOCOL_WS, null, null, null);
        final HkDeviceInfo hkDeviceInfo = hkVideoService.queryDevice(deviceSerial);
        Optional.ofNullable(url).filter(ObjectUtil::isNotEmpty).ifPresent(build::setBaseUrl);
        Optional.ofNullable(hkDeviceInfo).map(HkDeviceInfo::getCapabilitySet).filter(ObjectUtil::isNotEmpty)
                .map(u -> u.split(",")).map(List::of).map(u -> Map.of("vss", u.stream().filter(DefaultConstant
                                .HikVideoParamKeys.HIK_VSS_KEY::equals).findAny().map(s -> 1).orElse(0), "ptz",
                        u.stream().filter(DefaultConstant.HikVideoParamKeys.HIK_PTZ_KEY::equals).findAny().map(s -> 1)
                                .orElse(0))).ifPresent(build::setCapabilitySet);
        return build;
    }

    @Override
    public Map<String, String> queryHikVideoPlayBack(QueryHikVideoPlayBackParam param) {
        String beginTime = DateUtil.format(param.getBeginTime(), TimeUtil.HIK_PLAY_BACK_TIME_FORMAT);
        String endTime = DateUtil.format(param.getEndTime(), TimeUtil.HIK_PLAY_BACK_TIME_FORMAT);
        Map<String, Object> streamInfo = hkVideoService.getPlayBackStreamInfo(param.getTbVideoDevice().getDeviceSerial(), param.getRecordLocation().toString(),
                DefaultConstant.HikVideoParamKeys.HIK_PROTOCOL_WS, null, beginTime, endTime, param.getUuid(), null, null, null);
        return new HashMap<>() {
            {
                Optional.ofNullable(streamInfo).filter(u -> u.containsKey(DefaultConstant.HikVideoParamKeys.HIK_STREAM_URL))
                        .map(u -> u.get(DefaultConstant.HikVideoParamKeys.HIK_STREAM_URL)).map(Object::toString).ifPresent(u -> put("baseUrl", u));
                Optional.ofNullable(streamInfo).filter(u -> u.containsKey(DefaultConstant.HikVideoParamKeys.HIK_PLAYBACK_UUID))
                        .map(u -> u.get(DefaultConstant.HikVideoParamKeys.HIK_PLAYBACK_UUID)).map(Object::toString).ifPresent(u -> put("uuid", u));
            }
        };
    }

    @Override
    public String queryHikVideoTalk(QueryHikVideoTalkParam param) {
        return hkVideoService.getTalkStreamInfo(param.getTbVideoDevice().getDeviceSerial(),
                DefaultConstant.HikVideoParamKeys.HIK_PROTOCOL_WS, null, null, null);
    }
}
