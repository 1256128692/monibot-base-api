package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoCapture;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.CaptureInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryCaptureParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.SensorBaseInfoV1;

import java.util.Date;
import java.util.List;

public interface TbVideoCaptureMapper {
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
    int insert(TbVideoCapture record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TbVideoCapture record);

    /**
     * select by primary key
     * @param ID primary key
     * @return object by primary key
     */
    TbVideoCapture selectByPrimaryKey(Integer ID);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TbVideoCapture record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TbVideoCapture record);

    void deleteByVedioIDList(List<String> deviceSerialList);

    void insertBatch(List<SensorBaseInfoV1> captureSensorList);

    void insertBatchByCaptureList(List<CaptureInfo> list);

}