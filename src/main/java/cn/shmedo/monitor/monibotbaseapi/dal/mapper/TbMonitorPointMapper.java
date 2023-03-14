package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;

public interface TbMonitorPointMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorPoint record);

    int insertSelective(TbMonitorPoint record);

    TbMonitorPoint selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorPoint record);

    int updateByPrimaryKey(TbMonitorPoint record);
}