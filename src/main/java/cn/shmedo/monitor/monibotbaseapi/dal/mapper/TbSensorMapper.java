package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbSensorMapper extends BaseMapper<TbSensor> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbSensor record);

    int insertSelective(TbSensor record);

    TbSensor selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbSensor record);

    int updateByPrimaryKey(TbSensor record);
}