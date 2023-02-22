package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;

public interface TbProjectPropertyMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectProperty record);

    int insertSelective(TbProjectProperty record);

    TbProjectProperty selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectProperty record);

    int updateByPrimaryKey(TbProjectProperty record);
}