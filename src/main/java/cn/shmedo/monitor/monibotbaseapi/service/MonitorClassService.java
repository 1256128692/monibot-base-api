package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateWtMonitorClassParam;

import java.util.List;

public interface MonitorClassService {


    void updateWtMonitorClass(UpdateWtMonitorClassParam request);

    List<TbProjectMonitorClass> queryMonitorClassList(QueryMonitorClassParam request);
}
