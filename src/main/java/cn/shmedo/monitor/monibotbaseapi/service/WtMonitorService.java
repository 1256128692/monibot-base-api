package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorItemFieldResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHistoryAvgDataResponse;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

public interface WtMonitorService {

    List<SensorNewDataInfo> queryMonitorPointList(QueryMonitorPointListParam pa);


    List<FieldSelectInfo> getFieldSelectInfoListFromModleTypeFieldList(List<TbMonitorTypeField> list);

    SensorNewDataInfo querySingleMonitorPointNewData(QueryMonitorPointDescribeParam pa);

    MonitorPointTypeStatisticsInfo queryMonitorPointTypeStatistics(StatisticsMonitorPointTypeParam pa);

    MonitorPointHistoryData queryMonitorPointHistoryDataList(QueryMonitorPointSensorDataListParam pa);

    MonitorPointHistoryData querySmcPointHistoryDataList(QuerySmcPointHistoryDataListParam pa);

    MonitorPointAllInfo queryMonitorPointBaseInfoList(QueryMonitorPointBaseInfoListParam pa);

    RainMonitorPointHistoryData queryRainPointHistoryDataList(QueryRainMonitorPointSensorDataListParam pa);

    MonitorPointListHistoryData queryMonitorPointListHistoryDataList(QueryMonitorPointsSensorDataListParam pa);

    TriaxialDisplacementMonitorPointHistoryData queryDisplacementPointHistoryDataList(QueryDisplacementPointHistoryParam pa);

    List<TriaxialDisplacementSensorNewDataInfo> queryDisplacementMonitorPointNewDataList(QueryDisplacementMonitorPointNewDataParam pa);

    MonitorItemFieldResponse queryMonitorItemFieldList(QueryMonitorItemFieldListParam pa);

    List<SensorHistoryAvgDataResponse> queryMonitorPointHistoryAvgDataList(QueryMonitorPointHistoryAvgDataParam pa);

    PageUtil.PageWithMap<SensorHistoryAvgDataResponse> queryMonitorPointHistoryAvgDataPage(QueryMonitorPointHistoryAvgDataPageParam pa);

    List<SensorHistoryAvgDataResponse> querySensorHistoryAvgDataList(QuerySensorHistoryAvgDataParam pa);

    PageUtil.PageWithMap<SensorHistoryAvgDataResponse> querySensorHistoryAvgDataPage(QuerySensorHistoryAvgDataPageParam pa);

    List<SensorHistoryAvgDataResponse>  queryRainPointHistorySumDataList(QueryRainPointHistorySumDataParam pa);

    PageUtil.PageWithMap<SensorHistoryAvgDataResponse> queryRainPointHistorySumDataPage(QueryRainPointHistorySumDataPageParam pa);

    List<SensorHistoryAvgDataResponse> queryWaterRainSensorHistoryAvgDataList(QueryWaterRainSensorHistoryAvgDataParam pa);

    PageUtil.PageWithMap<SensorHistoryAvgDataResponse> queryWaterRainSensorHistoryAvgDataPage(QueryWaterRainSensorHistoryAvgDataPageParam pa);


    Object queryProjectLocation(QueryProjectLocationParam pa);
}
