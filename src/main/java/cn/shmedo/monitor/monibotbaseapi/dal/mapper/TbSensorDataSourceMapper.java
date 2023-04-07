package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensorDataSource;

public interface TbSensorDataSourceMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbSensorDataSource record);

    int insertSelective(TbSensorDataSource record);

    TbSensorDataSource selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensorDataSource record);

    int updateByPrimaryKey(TbSensorDataSource record);
}