package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensorFile;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointPictureInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface TbSensorFileMapper  extends BasicMapper<TbSensorFile>{
    int deleteByPrimaryKey(Integer ID);

    int insert(TbSensorFile record);

    int insertSelective(TbSensorFile record);

    TbSensorFile selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensorFile record);

    int updateByPrimaryKey(TbSensorFile record);

    List<VideoMonitorPointPictureInfo> selectListByIDAndTime(Integer sensorID, Date beginTime, Date endTime);
}