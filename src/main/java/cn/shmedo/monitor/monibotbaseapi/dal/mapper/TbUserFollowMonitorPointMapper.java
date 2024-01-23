package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserFollowMonitorPoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbUserFollowMonitorPointMapper extends BaseMapper<TbUserFollowMonitorPoint> {
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
    int insert(TbUserFollowMonitorPoint record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbUserFollowMonitorPoint record);

    /**
     * select by primary key
     * @param ID primary key
     * @return object by primary key
     */
    TbUserFollowMonitorPoint selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbUserFollowMonitorPoint record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbUserFollowMonitorPoint record);

    void insertBatch(List<TbUserFollowMonitorPoint> userFollowMonitorPointList);

    void deleteBatch(List<Integer> monitorPointIDList, Integer userID);
}