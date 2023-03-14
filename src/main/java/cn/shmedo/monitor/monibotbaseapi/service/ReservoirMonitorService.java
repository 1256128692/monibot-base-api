package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;

public interface ReservoirMonitorService {

    SensorNewDataInfo queryMonitorPointList(QueryMonitorPointListParam pa);
}
