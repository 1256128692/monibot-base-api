package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue.EigenValueInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicEigenValueData;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicEigenValueInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EigenBaseInfo;

import java.util.List;

public interface TbEigenValueMapper {
    /**
     * delete by primary key
     *
     * @param ID primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Integer ID);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(TbEigenValue record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbEigenValue record);

    /**
     * select by primary key
     *
     * @param ID primary key
     * @return object by primary key
     */
    TbEigenValue selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbEigenValue record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbEigenValue record);

    Integer selectCountByProjectIDAndItemIDAndFiledIDAndName(Integer projectID, Integer monitorItemID, Integer monitorTypeFieldID, String name, List<Integer> monitorPointIDList);

    List<EigenValueInfoV1> selectListByCondition(Integer monitorItemID, Integer projectID, List<Integer> monitorPointIDList, Integer scope);

    Integer selectCountByProjectIDAndItemIDAndFiledIDAndNameAndID(Integer projectID, Integer monitorItemID,
                                                                  Integer monitorTypeFieldID, String name,
                                                                  Integer eigenValueID);

    void deleteByEigenValueIDList(List<Integer> eigenValueIDList);

    List<ThematicEigenValueData> selectBaseInfoByIDList(List<Integer> eigenValueIDList, List<Integer> monitorPointIDList);

    List<ThematicEigenValueInfo> selectFieldInfoByPointIDList(List<Integer> monitorPointIDList);

    List<EigenBaseInfo> selectByIDs(List<Integer> eigenValueIDList);

    int insertBatchSelective(List<AddEigenValueParam> tbEigenValues);

    Integer selectMaxID();

    List<String> selectNameByMonitorIDList(List<Integer> monitorPointIDList, Integer projectID, Integer monitorItemID, Integer eigenValueID);
}