package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;

public interface TbProjectInfoMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectInfo record);

    int insertSelective(TbProjectInfo record);

    TbProjectInfo selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectInfo record);

    int updateByPrimaryKey(TbProjectInfo record);
}