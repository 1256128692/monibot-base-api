package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.SensorListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.SensorBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHistoryAvgDataResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorListResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCaptureBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.SensorWithMore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper
public interface TbSensorMapper extends BasicMapper<TbSensor> {
    int deleteByPrimaryKey(Integer ID);

    int insertSelective(TbSensor record);

    TbSensor selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensor record);

    int updateByPrimaryKey(TbSensor record);

    List<TbSensor> selectListByCompanyIDAndQueryTypeAndProjectIDList(Integer companyID, Integer queryType, List<Integer> projectIDList);

    <T extends SensorListRequest> IPage<SensorListResponse> selectSensorPage(IPage<SensorListResponse> page, @Param("pa") T request);

    <T extends SensorListRequest> List<SensorListResponse> selectSensorList(@Param("pa") T request);

    /**
     * 获取传感器名称序列号（已经+1）<br/>
     * 规则：同一项目下，同一监测类型，序列号从1开始递增
     *
     * @param projectID   项目ID
     * @param monitorType 监测类型
     * @return 传感器序列号
     */
    Integer getNameSerialNumber(@Param("projectID") Integer projectID, @Param("monitorType") Integer monitorType);

    void updatePoint(Integer pointID, List<Integer> sensorIDList, Integer userID, Date date);

    void updatePointByPoint(Integer oldPoint, Integer newPoint, Integer userID, Date date);

    List<SensorWithMore> querySensorWithMoreBy(Collection<String> uniqueTokens, Integer companyID, List<Integer> projectIDList, Integer monitorItemID);

    List<SensorBaseInfoResponse> selectListBymonitorPointIDList(List<Integer> monitorPointIDList);

    List<Tuple<Integer, Integer>> queryAllTypeAndID();

    List<SensorHistoryAvgDataResponse> selectListByMonitorPointIDsAndProjectIDs(List<Integer> monitorPointIDList, List<Integer> projectIDList);

    List<SensorHistoryAvgDataResponse> selectListBySensorIDsAndProjectIDs(List<Integer> sensorIDList, List<Integer> projectIDList);

    List<VideoCaptureBaseInfo> queryListByCondition(List<Integer> videoIDList);

    void deleteByVedioIDList(List<Integer> videoIDList);

    Integer queryMaxDisplayOrderByMonitorType(Integer key);

    void insertSensorList(List<SensorBaseInfoV1> insertSensorList);

    void updateSensorList(List<SensorBaseInfoV1> updateSensorList);

    List<SensorBaseInfoV1> selectListByNameAndProjectID(List<String> sensorNameList, Integer projectID);
}