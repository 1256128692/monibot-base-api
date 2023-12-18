package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqMonitorPointDataPushParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqQueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqQueryMonitorPointStatisticsDataPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.GqMonitorPointDataResponse;

public interface GqMonitorPointService {
    GqMonitorPointDataResponse gqQueryMonitorPointDataList(GqQueryMonitorPointDataParam pa);

    Object gqMonitorPointDataPush(GqMonitorPointDataPushParam pa);

    Object gqQueryMonitorPointStatisticsDataPage(GqQueryMonitorPointStatisticsDataPageParam pa);
}
