package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint.AddPresetPointParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.presetPoint.PresetPointWithDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;
import java.util.Map;

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

    PageUtil.PageWithMap<VideoDevicePageInfo> queryVideoDevicePage(QueryVideoDevicePageParam pa);

    VideoDeviceBaseInfoV2 queryYsVideoDeviceInfo(QueryYsVideoDeviceInfoParam param);

    Map<String, String> queryYsVideoPlayBack(QueryYsVideoPlayBackParam param);

    List<VideoDeviceInfoV1> queryVideoDeviceList(QueryVideoDeviceListParam pa);

    Object deleteVideoDeviceList(DeleteVideoDeviceParam pa);

    VideoDeviceInfoV1 queryHkVideoDeviceBaseInfo(QueryHkVideoDeviceBaseInfoParam pa);

    Object updateVideoDeviceList(UpdateVideoDeviceParam pa);

    Object saveVideoDeviceSensorList(SaveVideoDeviceSensorParam pa);

    PageUtil.Page<VideoDeviceBaseInfoV1> queryYsVideoDeviceList(QueryYsVideoDeviceParam pa);

    PageUtil.Page<VideoDeviceBaseInfoV1> queryHkVideoDeviceList(QueryHkVideoDeviceParam pa);

    PageUtil.Page<VideoSensorFileInfo> queryCapturePage(QueryCapturePageParam pa);

    List<VideoSensorFileInfo> queryCaptureList(QueryCaptureParam pa);

    Boolean batchUpdateVideoDeviceStatus(BatchUpdateVideoDeviceStatusParam pa);

    ResultWrapper<Object> addPresetPoint(AddPresetPointParam param);

    ResultWrapper<Object> deletePresetPoint(List<PresetPointWithDeviceInfo> presetPointWithDeviceInfoList);

    ResultWrapper<Object> movePresetPoint(PresetPointWithDeviceInfo info);

    List<VideoDeviceInfoV1> queryVideoDeviceListV1(QueryVideoDeviceListParam pa);
}
