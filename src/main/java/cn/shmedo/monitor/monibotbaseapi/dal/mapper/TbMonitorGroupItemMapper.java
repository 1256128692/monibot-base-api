package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupItem;

public interface TbMonitorGroupItemMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorGroupItem record);

    int insertSelective(TbMonitorGroupItem record);

    TbMonitorGroupItem selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorGroupItem record);

    int updateByPrimaryKey(TbMonitorGroupItem record);
}