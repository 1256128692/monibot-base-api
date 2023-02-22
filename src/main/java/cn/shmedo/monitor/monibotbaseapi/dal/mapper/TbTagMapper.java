package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;

public interface TbTagMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTag record);

    int insertSelective(TbTag record);

    TbTag selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTag record);

    int updateByPrimaryKey(TbTag record);
}