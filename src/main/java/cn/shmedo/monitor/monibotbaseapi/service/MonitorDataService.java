package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.DeleteBatchEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.QueryEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.UpdateEigenValueParam;

public interface MonitorDataService {
    void addEigenValue(AddEigenValueParam pa);

    Object queryEigenValueList(QueryEigenValueParam pa);

    void updateEigenValue(UpdateEigenValueParam pa);

    void deleteBatchEigenValue(DeleteBatchEigenValueParam pa);

    void addDataEvent(AddDataEventParam pa);

    Object queryDataEventList(QueryDataEventParam pa);
}
