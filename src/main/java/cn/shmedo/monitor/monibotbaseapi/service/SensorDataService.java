package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.QuerySensorHasDataCountParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.StatisticsSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHasDataCountResponse;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-18 15:15
 **/
public interface SensorDataService {
    void statisticsSensorData(StatisticsSensorDataParam pa);

    SensorHasDataCountResponse querySensorHasDataCount(QuerySensorHasDataCountParam pa);
}
