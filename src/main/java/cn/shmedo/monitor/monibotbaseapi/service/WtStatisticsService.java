package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryDeviceOnlineStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsByProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.ReservoirNewSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.*;

import java.util.List;

public interface WtStatisticsService {

    ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param);

    void cacheWarnStats();

    DeviceOnlineStatsResponse queryDeviceOnlineStats(QueryDeviceOnlineStatsParam param);

    void cacheDeviceOnlineStats();

    ReservoirNewSensorDataResponse queryReservoirNewSensorData(ReservoirNewSensorDataParam pa);

    ReservoirWarnStatsByProjectResponse queryWarnStatsByProject(QueryReservoirWarnStatsByProjectParam param);

    ReservoirProjectStatisticsResult reservoirProjectStatistics(Integer companyID);

    ReservoirMonitorStatisticsResult reservoirMonitorStatistics(Integer companyID);

    ReservoirDetail reservoirProjectDetail(TbProjectInfo tbProjectInfo);

    List<PointWithProjectInfo> reservoirVideoMonitorPoint(Integer companyID, TbProjectInfo tbProjectInfo);

    ReservoirDeviceStatisticsResult reservoirDeviceStatistics(Integer companyID, Integer projectID);
}
