package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorItem;

public interface TbProjectMonitorItemMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectMonitorItem record);

    int insertSelective(TbProjectMonitorItem record);

    TbProjectMonitorItem selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectMonitorItem record);

    int updateByPrimaryKey(TbProjectMonitorItem record);
}