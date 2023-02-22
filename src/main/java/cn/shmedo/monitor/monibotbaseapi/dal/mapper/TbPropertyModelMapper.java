package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;

public interface TbPropertyModelMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbPropertyModel record);

    int insertSelective(TbPropertyModel record);

    TbPropertyModel selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbPropertyModel record);

    int updateByPrimaryKey(TbPropertyModel record);
}