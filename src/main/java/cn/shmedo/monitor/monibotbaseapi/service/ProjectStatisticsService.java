package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.ProjectConditionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateDeviceCountStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DataCountStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DeviceAssetsStatisticsInfo;

public interface ProjectStatisticsService {
    Boolean updateDeviceCountStatistics(UpdateDeviceCountStatisticsParam pa);

    DeviceAssetsStatisticsInfo queryDeviceCountStatistics(ProjectConditionParam pa);

    DataCountStatisticsInfo queryDataCountStatistics(ProjectConditionParam pa);

    Object queryMonitorItemCountStatistics(ProjectConditionParam pa);
}
