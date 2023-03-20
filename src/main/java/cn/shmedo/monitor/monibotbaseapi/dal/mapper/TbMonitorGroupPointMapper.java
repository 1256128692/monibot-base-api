package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;

public interface TbMonitorGroupPointMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorGroupPoint record);

    int insertSelective(TbMonitorGroupPoint record);

    TbMonitorGroupPoint selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorGroupPoint record);

    int updateByPrimaryKey(TbMonitorGroupPoint record);
}