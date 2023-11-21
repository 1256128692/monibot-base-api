package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataEvent;
import cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent.QueryDataEventInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EventBaseInfo;

import java.util.List;
import java.util.Map;

public interface TbDataEventMapper {
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
    int insert(TbDataEvent record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbDataEvent record);

    /**
     * select by primary key
     *
     * @param ID primary key
     * @return object by primary key
     */
    TbDataEvent selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbDataEvent record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbDataEvent record);

    List<QueryDataEventInfo> selectListByProjectIDAndItemIDs(Integer projectID, List<Integer> monitorItemIDList);

    void deleteByEventIDList(List<Integer> eventIDList);

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> selectBaseInfoByIDList(List<Integer> eventIDList);

    List<EventBaseInfo> selectByIDs(List<Integer> eventIDList);

    int selectCountByName(Integer projectID, String name, Integer id);
}