package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;

import java.util.List;

public interface ReservoirMonitorService {

    List<SensorNewDataInfo> queryMonitorPointList(QueryMonitorPointListParam pa);


    List<FieldSelectInfo> getFieldSelectInfoListFromModleTypeFieldList(List<TbMonitorTypeField> list);

    SensorNewDataInfo querySingleMonitorPointNewData(QueryMonitorPointDescribeParam pa);

    MonitorPointTypeStatisticsInfo queryMonitorPointTypeStatistics(StatisticsMonitorPointTypeParam pa);

    MonitorPointHistoryData queryMonitorPointHistoryDataList(QueryMonitorPointSensorDataListParam pa);

    MonitorPointHistoryData querySmcPointHistoryDataList(QuerySmcPointHistoryDataListParam pa);

    MonitorPointAllInfo queryMonitorPointBaseInfoList(Integer projectID);

    MonitorPointHistoryData queryRainPointHistoryDataList(QueryRainMonitorPointSensorDataListParam pa);

    MonitorPointListHistoryData queryMonitorPointListHistoryDataList(QueryMonitorPointsSensorDataListParam pa);
}
