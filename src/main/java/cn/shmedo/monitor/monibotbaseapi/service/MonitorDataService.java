package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.QueryEigenValueParam;

public interface MonitorDataService {
    void addEigenValue(AddEigenValueParam pa);

    Object queryEigenValueList(QueryEigenValueParam pa);
}
