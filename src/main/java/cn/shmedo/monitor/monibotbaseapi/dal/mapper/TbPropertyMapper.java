package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;

import java.util.List;

public interface TbPropertyMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProperty record);

    int insertSelective(TbProperty record);

    TbProperty selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProperty record);

    int updateByPrimaryKey(TbProperty record);

    List<TbProperty> queryByPID(Integer projectID);

    int countByPIDAndNames(Integer projectID, List<String> nameList);

    void insertBatch(List<TbProperty> properties);
}