package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqQueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.GqMonitorPointDataResponse;

public interface GqMonitorPointService {
    GqMonitorPointDataResponse gqQueryMonitorPointDataList(GqQueryMonitorPointDataParam pa);
}
