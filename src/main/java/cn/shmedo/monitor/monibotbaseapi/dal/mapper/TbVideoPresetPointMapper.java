package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoPresetPoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbVideoPresetPointMapper extends BaseMapper<TbVideoPresetPoint> {
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
    int insert(TbVideoPresetPoint record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbVideoPresetPoint record);

    /**
     * select by primary key
     *
     * @param ID primary key
     * @return object by primary key
     */
    TbVideoPresetPoint selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbVideoPresetPoint record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbVideoPresetPoint record);
}