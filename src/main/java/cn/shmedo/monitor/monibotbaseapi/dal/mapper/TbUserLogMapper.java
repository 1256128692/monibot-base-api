package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserLog;

public interface TbUserLogMapper extends BasicMapper<TbUserLog> {
    int deleteByPrimaryKey(Integer ID);

    int insertSelective(TbUserLog record);

    TbUserLog selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbUserLog record);

    int updateByPrimaryKey(TbUserLog record);

    void cleanUserLog();

}