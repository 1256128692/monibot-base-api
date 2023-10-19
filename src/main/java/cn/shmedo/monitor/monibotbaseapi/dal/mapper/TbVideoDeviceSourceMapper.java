package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDeviceSource;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCaptureBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCaptureBaseInfoV2;

import java.util.List;

public interface TbVideoDeviceSourceMapper {
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
    int insert(TbVideoDeviceSource record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbVideoDeviceSource record);

    /**
     * select by primary key
     * @param ID primary key
     * @return object by primary key
     */
    TbVideoDeviceSource selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbVideoDeviceSource record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbVideoDeviceSource record);

    void batchInsert(List<TbVideoDeviceSource> deviceSourceList);

    void deleteByDeviceSerialList(List<String> deviceSerialList);

    List<VideoCaptureBaseInfoV2> selectByDeviceSerial(String deviceSerial);
}