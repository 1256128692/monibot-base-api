package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbMonitorTypeFieldMapper extends BaseMapper<TbMonitorTypeField> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorTypeField record);

    int insertSelective(TbMonitorTypeField record);

    TbMonitorTypeField selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorTypeField record);

    int updateByPrimaryKey(TbMonitorTypeField record);
}