package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ReservoirMonitorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservoirMonitorServiceImpl implements ReservoirMonitorService {



    @Override
    public SensorNewDataInfo queryMonitorPointList(QueryMonitorPointListParam pa) {

        SensorNewDataInfo sensorNewDataInfo = new SensorNewDataInfo();
        return sensorNewDataInfo;
    }
}
