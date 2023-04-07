package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.SensorPageRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbSensorMapper extends BaseMapper<TbSensor> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbSensor record);

    int insertSelective(TbSensor record);

    TbSensor selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensor record);

    int updateByPrimaryKey(TbSensor record);

    List<TbSensor> selectStatisticsCountByCompanyID(Integer companyID);

    IPage<SensorPageResponse> selectSensorPage(IPage<SensorPageResponse> page,@Param("pa") SensorPageRequest request);
}