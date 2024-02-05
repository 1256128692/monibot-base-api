package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryDeviceOnlineStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsByProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.ReservoirNewSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.*;

import java.util.Collection;
import java.util.List;

public interface WtStatisticsService {

    ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param);

    void cacheWarnStats();

    DeviceOnlineStatsResponse queryDeviceOnlineStats(QueryDeviceOnlineStatsParam param);

    void cacheDeviceOnlineStats();

    List<ReservoirNewSensorDataResponse> queryReservoirNewSensorData(ReservoirNewSensorDataParam pa);

    ReservoirWarnStatsByProjectResponse queryWarnStatsByProject(QueryReservoirWarnStatsByProjectParam param);

    ReservoirProjectStatisticsResult reservoirProjectStatistics(Integer companyID, Collection<Integer> havePermissionProjectList);

    ReservoirMonitorStatisticsResult reservoirMonitorStatistics(Integer companyID, Collection<Integer> havePermissionProjectList);

    ReservoirDetail reservoirProjectDetail(TbProjectInfo tbProjectInfo);

    List<PointWithProjectInfo> reservoirVideoMonitorPoint(Integer companyID, Collection<Integer> havePermissionProjectList);

    ReservoirDeviceStatisticsResult reservoirDeviceStatistics(Integer companyID, Collection<Integer> havePermissionProjectList);

    void cacheTypePointStatistics();

    void cacheVideoPointIDStatistics();

    void cachedIntelDeviceStatistics();

    void cachedReservoirDetail();
}
