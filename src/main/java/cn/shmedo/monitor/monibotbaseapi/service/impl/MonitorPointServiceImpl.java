package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.AddMonitorPointParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.UpdateMonitorPointParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorPointService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 13:48
 **/
@Service
@Slf4j
@AllArgsConstructor
public class MonitorPointServiceImpl implements MonitorPointService {
    private TbMonitorPointMapper tbMonitorPointMapper;
    private TbSensorMapper tbSensorMapper;
    @Override
    public void addMonitorPoint(AddMonitorPointParam pa, Integer userID) {
        TbMonitorPoint temp = Param2DBEntityUtil.fromAddMonitorPointParam2TbMonitorPoint(pa, userID);
        tbMonitorPointMapper.insert(temp);
    }

    @Override
    public void updateMonitorPoint(UpdateMonitorPointParam pa, Integer userID) {
        tbMonitorPointMapper.updateByPrimaryKey(pa.update(userID));
    }

    @Override
    public void deleteMonitorPoint(List<Integer> pointIDList) {
        tbMonitorPointMapper.deleteBatchIds(pointIDList);
    }

    @Override
    public void configMonitorPointSensors(Integer pointID, List<Integer> sensorIDList, Integer userID) {
        tbSensorMapper.updatePoint(pointID, sensorIDList, userID, new Date());
    }
}
