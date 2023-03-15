package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItemField;

public interface TbMonitorItemFieldMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorItemField record);

    int insertSelective(TbMonitorItemField record);

    TbMonitorItemField selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorItemField record);

    int updateByPrimaryKey(TbMonitorItemField record);
}