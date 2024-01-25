package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse;

public interface WtStatisticsService {

    ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param);

    void cacheWarnStats();
}
