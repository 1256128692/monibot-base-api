package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.SensorListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.sluice.QuerySluicePageRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.SensorBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.VideoDeviceInfoV6;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHistoryAvgDataResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorListResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sluice.Sluice;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.SensorIDWithFormulaBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCaptureBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.SensorWithMore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reactor.util.function.Tuple3;

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

    List<VideoCaptureBaseInfo> queryListByDeviceSerialList(List<String> deviceSerialList);

    Integer queryMaxDisplayOrderByMonitorType(Integer key);

    void insertSensorList(List<SensorBaseInfoV1> insertSensorList);

    void updateSensorList(List<SensorBaseInfoV1> updateSensorList);

    List<SensorBaseInfoV1> selectListByNameAndProjectID(List<String> sensorNameList, Integer projectID);

    List<VideoDeviceInfoV6> selectListByDeviceSerialList(List<String> deviceSerialList);

    void deleteBatchByDeviceSerialList(List<String> deviceSerialList);

    List<Sluice> listSluice(@Param("projectIDList") Collection<Integer> projectIDList);

    IPage<Integer> sluicePage(@Param("page") IPage<Integer> page, @Param("param")QuerySluicePageRequest param);

    /**
     * 查询闸门对应的物联网设备信息
     * @param projectID
     * @param sensorID
     * @return {@code List<Tuple3<Integer, String, Integer>>} 闸门ID、物联网uniqueToken、电机序号
     */
    List<Tuple3<Integer, String, String>> queryGateIotToken(@Param("projectID") Integer projectID, @Param("sensorID") Integer sensorID);
    /**
     * 查询传感器id和监测类型下全部扩展属性的fieldToken、公式和公式排序<br>
     * 仅是<b>扩展属性</b>,不是<b>全部属性</b>
     */
    List<SensorIDWithFormulaBaseInfo> selectSensorIDWithFormulaBaseInfoBySensorIDList(List<Integer> sensorIDList);
}