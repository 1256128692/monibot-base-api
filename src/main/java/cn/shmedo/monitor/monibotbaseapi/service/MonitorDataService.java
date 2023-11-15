package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.DeleteBatchDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.UpdateDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.DeleteBatchEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.QueryEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.UpdateEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.QueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeConfigurationParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.MonitorPointDataInfo;

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
}
