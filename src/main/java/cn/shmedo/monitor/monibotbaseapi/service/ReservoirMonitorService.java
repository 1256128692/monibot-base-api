package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryReservoirMonitorPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;

public interface ReservoirMonitorService {

    SensorNewDataInfo queryReservoirMonitorPointList(QueryReservoirMonitorPointListParam pa);
}
