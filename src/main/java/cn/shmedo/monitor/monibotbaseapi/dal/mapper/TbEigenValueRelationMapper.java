package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValueRelation;

import java.util.List;

public interface TbEigenValueRelationMapper {
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
    int insert(TbEigenValueRelation record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbEigenValueRelation record);

    /**
     * select by primary key
     * @param ID primary key
     * @return object by primary key
     */
    TbEigenValueRelation selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbEigenValueRelation record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbEigenValueRelation record);

    void insertBatch(List<Integer> monitorPointIDList, Integer eigenValueID);

    void deleteByEigenValueIDList(List<Integer> eigenValueIDList);

    List<TbEigenValueRelation> selectByIDs(List<Integer> eigenValueIDList);
}