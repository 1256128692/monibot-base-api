package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoMonitorPointHistoryLiveInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoMonitorPointLiveInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.HistoryLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.QueryVideoBaseInfoResult;

import java.util.List;

public interface VideoService {
    QueryVideoBaseInfoResult queryVideoBaseInfo(QueryVideoBaseInfoParam param);


    List<VideoMonitorPointLiveInfo> queryVideoMonitorPointLiveInfo(QueryVideoMonitorPointLiveInfoParam pa);

    HistoryLiveInfo queryVideoMonitorPointHistoryLiveInfo(QueryVideoMonitorPointHistoryLiveInfoParam pa);
}
