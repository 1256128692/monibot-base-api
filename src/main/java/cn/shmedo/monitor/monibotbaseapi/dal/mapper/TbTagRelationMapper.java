package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTagRelation;

public interface TbTagRelationMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTagRelation record);

    int insertSelective(TbTagRelation record);

    TbTagRelation selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTagRelation record);

    int updateByPrimaryKey(TbTagRelation record);
}