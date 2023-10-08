package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDeviceBaseInfoV2;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewSubGroupInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:19
 */
public interface ITbVideoDeviceService extends IService<TbVideoDevice> {
    List<VideoCompanyViewBaseInfo> queryVideoCompanyViewBaseInfo(QueryVideoCompanyViewBaseInfoParam param);

    List<VideoProjectViewBaseInfo> queryVideoProjectViewBaseInfo(QueryVideoProjectViewBaseInfo param);

    List<VideoProjectViewSubGroupInfo> queryVideoProjectViewBaseInfoV2(QueryVideoProjectViewBaseInfo param);

    VideoDeviceBaseInfoV2 queryHikVideoDeviceInfo(QueryHikVideoDeviceInfoParam param);

    ResultWrapper<Map<String,String>> queryHikVideoPlayBack(QueryHikVideoPlayBackParam param);

    String queryHikVideoTalk(QueryHikVideoTalkParam param);
}
