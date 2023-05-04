package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.QueryVideoBaseInfoResult;

public interface VideoService {
    QueryVideoBaseInfoResult queryVideoBaseInfo(QueryVideoBaseInfoParam param);
}
