package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItem4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

public interface VideoService {
    QueryVideoBaseInfoResult queryVideoBaseInfo(QueryVideoBaseInfoParam param);


    List<VideoMonitorPointLiveInfo> queryVideoMonitorPointLiveInfo(QueryVideoMonitorPointLiveInfoParam pa);

    HistoryLiveInfo queryVideoMonitorPointHistoryLiveInfo(QueryVideoMonitorPointHistoryLiveInfoParam pa);

    ResultWrapper<Object> panControlVideoPoint(PanControlVideoPointParam pa);

    Object queryVideoMonitorPointPictureInfo(QueryVideoMonitorPointPictureInfoParam pa);

    /**
     * @see #panControlVideoPoint(PanControlVideoPointParam)
     */
    ResultWrapper<Object> panControlCompanyVideoPoint(PanControlCompanyVideoPointParam pa);

    ResultWrapper<Object> addVideoDeviceList(AddVideoDeviceListParam pa);

    PageUtil.Page<VideoDevicePageInfo> queryVideoDevicePage(QueryVideoDevicePageParam pa);

    VideoDeviceBaseInfoV2 queryYsVideoDeviceInfo(QueryYsVideoDeviceInfoParam param);

    String queryYsVideoPlayBack(QueryYsVideoPlayBackParam param);

    List<VideoDeviceInfoV1> queryVideoDeviceList(QueryVideoDeviceListParam pa);

    Object deleteVideoDeviceList(DeleteVideoDeviceParam pa);

    VideoDeviceInfoV1 queryHkVideoDeviceBaseInfo(QueryHkVideoDeviceBaseInfoParam pa);

    Object updateVideoDeviceList(UpdateVideoDeviceParam pa);
}
