package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;

public interface TbParameterMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbParameter record);

    int insertSelective(TbParameter record);

    TbParameter selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbParameter record);

    int updateByPrimaryKey(TbParameter record);
}