package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;

public interface TbProjectTypeMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectType record);

    int insertSelective(TbProjectType record);

    TbProjectType selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectType record);

    int updateByPrimaryKey(TbProjectType record);
}