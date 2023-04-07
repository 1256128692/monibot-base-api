package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.SensorPageRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorPageResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbSensorMapper extends BasicMapper<TbSensor> {
    int deleteByPrimaryKey(Integer ID);

    int insertSelective(TbSensor record);

    TbSensor selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensor record);

    int updateByPrimaryKey(TbSensor record);

    List<TbSensor> selectStatisticsCountByCompanyID(Integer companyID);

    IPage<SensorPageResponse> selectSensorPage(IPage<SensorPageResponse> page, @Param("pa") SensorPageRequest request);

    /**
     * 获取传感器名称序列号（已经+1）<br/>
     * 规则：同一项目下，同一监测类型，序列号从1开始递增
     *
     * @param projectID   项目ID
     * @param monitorType 监测类型
     * @return 传感器序列号
     */
    Integer getNameSerialNumber(@Param("projectID") Integer projectID, @Param("monitorType") Integer monitorType);
}