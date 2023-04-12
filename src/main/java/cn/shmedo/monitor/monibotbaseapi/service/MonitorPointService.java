package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.AddMonitorPointParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.UpdateMonitorPointParam;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 13:47
 **/
public interface MonitorPointService {
    void addMonitorPoint(AddMonitorPointParam pa, Integer userID);

    void updateMonitorPoint(UpdateMonitorPointParam pa, Integer userID);

    void deleteMonitorPoint(List<Integer> pointIDList);

    void configMonitorPointSensors(Integer pointID, List<Integer> sensorIDList, Integer userID);
}
