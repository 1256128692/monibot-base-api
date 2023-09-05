package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoDeviceService;
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

    @Override
    public VideoDeviceBaseInfoV2 queryHikVideoDeviceInfo(QueryHikVideoDeviceInfoParam param) {
        TbVideoDevice device = param.getTbVideoDevice();
        VideoDeviceBaseInfoV2 build = VideoDeviceBaseInfoV2.build(device);
        final String deviceSerial = device.getDeviceSerial();
        final String url = hkVideoService.getStreamUrl(deviceSerial, param.getStreamType(), "ws", null, null, null);
        final HkDeviceInfo hkDeviceInfo = hkVideoService.queryDevice(deviceSerial);
        Optional.ofNullable(url).filter(ObjectUtil::isNotEmpty).ifPresent(build::setBaseUrl);
        Optional.ofNullable(hkDeviceInfo).map(HkDeviceInfo::getCapabilitySet).filter(ObjectUtil::isNotEmpty)
                .map(u -> u.split(",")).map(List::of).map(u -> Map.of("vss", u.stream().filter("vss"::equals)
                        .findAny().map(s -> 1).orElse(0), "ptz", u.stream().filter("ptz"::equals).findAny()
                        .map(s -> 1).orElse(0))).ifPresent(build::setCapabilitySet);
        return build;
    }

    @Override
    public Map<String, String> queryHikVideoPlayBack(QueryHikVideoPlayBackParam param) {
        String beginTime = DateUtil.format(param.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.format(param.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        //这里到时候看下能不能用ws，文档里回放接口没写支持这种协议，但是文档直播接口、前端给过来的海康H5Player的demo支持这种协议
        Map<String, Object> streamInfo = hkVideoService.getPlayBackStreamInfo(param.getTbVideoDevice().getDeviceSerial(), param.getRecordLocation().toString(),
                "ws", null, beginTime, endTime, param.getUuid(), null, null, null);
        return new HashMap<>() {
            {
                Optional.ofNullable(streamInfo).filter(u -> u.containsKey("url")).map(u -> u.get("url")).map(Object::toString).ifPresent(u -> put("baseUrl", u));
                Optional.ofNullable(streamInfo).filter(u -> u.containsKey("uuid")).map(u -> u.get("uuid")).map(Object::toString).ifPresent(u -> put("uuid", u));
            }
        };
    }
}
