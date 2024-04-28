package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.DeleteBatchDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.UpdateDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeConfigurationParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.MonitorPointDataInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.MonitorPointListPageDataInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

public interface MonitorDataService {
    void addEigenValue(AddEigenValueParam pa);

    Object queryEigenValueList(QueryEigenValueParam pa);

    void updateEigenValue(UpdateEigenValueParam pa);

    void deleteBatchEigenValue(DeleteBatchEigenValueParam pa);

    void addDataEvent(AddDataEventParam pa);

    Object queryDataEventList(QueryDataEventParam pa);

    void updateDataEvent(UpdateDataEventParam pa);

    void deleteBatchDataEvent(DeleteBatchDataEventParam pa);

    Object queryMonitorTypeConfiguration(QueryMonitorTypeConfigurationParam pa);

    List<MonitorPointDataInfo> queryMonitorPointDataList(QueryMonitorPointDataParam pa);

    PageUtil.Page<MonitorPointListPageDataInfo> queryMonitorPointDataListPage(QueryMonitorPointDataListPageParam pa);

    Object queryMonitorPointHasDataCount(QueryMonitorPointHasDataCountParam pa);

    List<MonitorPointDataInfo> queryMonitorPointFilterDataList(QueryMonitorPointDataParam pa);

    Object queryMonitorTypeFieldList(QueryMonitorTypeFieldParam pa);

    void addEigenValueList(AddEigenValueListParam pa);

    Object queryMonitorPointDataPage(QueryMonitorPointDataPageParam pa);

    Object queryDisMonitorTypeHasDataCountByMonitorPoints(QueryDisMonitorTypeHasDataCountByMonitorPointsParam pa);
}
