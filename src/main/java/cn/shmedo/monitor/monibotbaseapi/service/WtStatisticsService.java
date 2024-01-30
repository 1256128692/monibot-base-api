package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryDeviceOnlineStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsByProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.ReservoirNewSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.DeviceOnlineStatsResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirNewSensorDataResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsByProjectResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse;

public interface WtStatisticsService {

    ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param);

    void cacheWarnStats();

    DeviceOnlineStatsResponse queryDeviceOnlineStats(QueryDeviceOnlineStatsParam param);

    void cacheDeviceOnlineStats();

    ReservoirNewSensorDataResponse queryReservoirNewSensorData(ReservoirNewSensorDataParam pa);

    ReservoirWarnStatsByProjectResponse queryWarnStatsByProject(QueryReservoirWarnStatsByProjectParam param);
}
