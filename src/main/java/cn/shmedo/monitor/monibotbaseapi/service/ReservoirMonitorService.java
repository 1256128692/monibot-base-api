package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointAllInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointHistoryData;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointTypeStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;

import java.util.List;

public interface ReservoirMonitorService {

    List<SensorNewDataInfo> queryMonitorPointList(QueryMonitorPointListParam pa);


    List<FieldSelectInfo> getFieldSelectInfoListFromModleTypeFieldList(List<TbMonitorTypeField> list);

    SensorNewDataInfo querySingleMonitorPointNewData(QueryMonitorPointDescribeParam pa);

    MonitorPointTypeStatisticsInfo queryMonitorPointTypeStatistics(StatisticsMonitorPointTypeParam pa);

    MonitorPointHistoryData queryMonitorPointHistoryDataList(QueryMonitorPointSensorDataListParam pa);

    Object querySmcPointHistoryDataList(QueryMonitorPointSensorDataListParam pa);

    MonitorPointAllInfo queryMonitorPointBaseInfoList(Integer projectID);
}
