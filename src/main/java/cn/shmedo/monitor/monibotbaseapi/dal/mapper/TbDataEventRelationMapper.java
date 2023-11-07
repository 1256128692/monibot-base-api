package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataEventRelation;

public interface TbDataEventRelationMapper {
    /**
     * delete by primary key
     * @param ID primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Integer ID);

    /**
     * insert record to table
     * @param record the record
     * @return insert count
     */
    int insert(TbDataEventRelation record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbDataEventRelation record);

    /**
     * select by primary key
     * @param ID primary key
     * @return object by primary key
     */
    TbDataEventRelation selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbDataEventRelation record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbDataEventRelation record);
}