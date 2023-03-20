package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;

public interface TbMonitorGroupMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorGroup record);

    int insertSelective(TbMonitorGroup record);

    TbMonitorGroup selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorGroup record);

    int updateByPrimaryKey(TbMonitorGroup record);
}