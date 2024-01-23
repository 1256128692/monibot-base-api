package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.ProjectConditionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateDeviceCountStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DataCountStatisticsInfo;

public interface ProjectStatisticsService {
    Object updateDeviceCountStatistics(UpdateDeviceCountStatisticsParam pa);

    Object queryDeviceCountStatistics(ProjectConditionParam pa);

    DataCountStatisticsInfo queryDataCountStatistics(ProjectConditionParam pa);
}
