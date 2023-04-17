package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateWtMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectMonitorClassBaseInfo;

import java.util.List;

public interface MonitorClassService {


    void updateWtMonitorClass(UpdateWtMonitorClassParam request);

    List<ProjectMonitorClassBaseInfo> queryMonitorClassList(QueryMonitorClassParam request);
}
