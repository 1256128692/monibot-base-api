package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue.EigenValueInfoV1;

import java.util.List;

public interface TbEigenValueMapper {
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
    int insert(TbEigenValue record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbEigenValue record);

    /**
     * select by primary key
     * @param ID primary key
     * @return object by primary key
     */
    TbEigenValue selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbEigenValue record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbEigenValue record);

    Integer selectCountByProjectIDAndItemIDAndFiledIDAndName(Integer projectID, Integer monitorItemID, Integer monitorTypeFieldID, String name);

    List<EigenValueInfoV1> selectListByCondition(Integer monitorItemID, Integer projectID, List<Integer> monitorPointIDList);
}