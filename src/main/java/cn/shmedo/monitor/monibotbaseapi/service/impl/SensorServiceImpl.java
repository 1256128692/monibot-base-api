package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.DataSourceCascadeResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.MonitorTypeCatalogResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorPageResponse;
import cn.shmedo.monitor.monibotbaseapi.service.SensorService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Chengfs on 2023/4/4
 */
@Service
public class SensorServiceImpl extends ServiceImpl<TbSensorMapper, TbSensor> implements SensorService {

    @Override
    public PageUtil.Page<SensorPageResponse> sensorPage(SensorPageRequest request) {
        return null;
    }

    @Override
    public DataSourceCascadeResponse dataSourceCascade(DataSourceCascadeRequest request) {
        return null;
    }

    @Override
    public MonitorTypeCatalogResponse monitorTypeCatalog(MonitorTypeCatalogRequest request) {
        return null;
    }

    @Override
    public Object addSensor(SaveSensorRequest request) {
        return null;
    }

    @Override
    public SensorInfoResponse sensorInfo(SensorInfoRequest request) {
        return null;
    }

    @Override
    public void deleteSensor(DeleteSensorRequest request) {

    }

    @Override
    public Object updateSensor(UpdateSensorRequest request) {
        return null;
    }

    @Override
    public Object getTryingParam(QueryTryingParamRequest request) {
        return null;
    }

    @Override
    public Object trying(TryingRequest request) {
        return null;
    }
}

    
    