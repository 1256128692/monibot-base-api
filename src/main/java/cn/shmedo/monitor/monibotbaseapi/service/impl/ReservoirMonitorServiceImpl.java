package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryReservoirMonitorPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ReservoirMonitorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservoirMonitorServiceImpl implements ReservoirMonitorService {



    @Override
    public SensorNewDataInfo queryReservoirMonitorPointList(QueryReservoirMonitorPointListParam pa) {

        SensorNewDataInfo sensorNewDataInfo = new SensorNewDataInfo();
        return sensorNewDataInfo;
    }
}
