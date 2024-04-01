package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.WarnInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DataCountStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DeviceAssetsStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.MonitorItemCountStatisticsInfo;

import java.util.List;

public interface ProjectStatisticsService {
    Boolean updateDeviceCountStatistics(UpdateDeviceCountStatisticsParam pa);

    DeviceAssetsStatisticsInfo queryDeviceCountStatistics(ProjectConditionParam pa);

    DataCountStatisticsInfo queryDataCountStatistics(ProjectConditionParam pa);

    List<MonitorItemCountStatisticsInfo> queryMonitorItemCountStatistics(ProjectConditionParam pa);

    WarnInfo queryDistinctWarnTypeMonitorPointCount(ProjectConditionParam pa);

    void addUserCollectionMonitorPoint(AddUserCollectionMonitorPointParam pa);

    void deleteUserCollectionMonitorPoint(DeleteUserCollectionMonitorPointParam pa);

    Object querySingleProjectMonitorPointInfoList(QuerySingleProjectMonitorPointInfoListParam pa);

    Boolean updateSensorOnlineStatusByIot(UpdateDeviceCountStatisticsParam pa);

    Object querySingleProjectMonitorPointNewDataPage(QuerySingleProjectMonitorPointNewDataPageParam pa);

    Object queryCompanyMonitorPointInfoList(QueryCompanyMonitorPointInfoParam pa);
}
