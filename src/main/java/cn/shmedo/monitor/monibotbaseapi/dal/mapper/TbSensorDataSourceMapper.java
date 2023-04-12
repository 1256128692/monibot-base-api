package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensorDataSource;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.SourceWithSensorRequest;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.DataSourceWithSensor;

import java.util.List;

public interface TbSensorDataSourceMapper extends BasicMapper<TbSensorDataSource> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbSensorDataSource record);

    int insertSelective(TbSensorDataSource record);

    TbSensorDataSource selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensorDataSource record);

    int updateByPrimaryKey(TbSensorDataSource record);

    List<DataSourceWithSensor> selectDataSourceWithSensor(SourceWithSensorRequest param);
}