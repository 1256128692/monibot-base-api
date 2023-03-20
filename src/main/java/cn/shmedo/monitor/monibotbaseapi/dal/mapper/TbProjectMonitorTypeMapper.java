package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorType;

public interface TbProjectMonitorTypeMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectMonitorType record);

    int insertSelective(TbProjectMonitorType record);

    TbProjectMonitorType selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectMonitorType record);

    int updateByPrimaryKey(TbProjectMonitorType record);
}