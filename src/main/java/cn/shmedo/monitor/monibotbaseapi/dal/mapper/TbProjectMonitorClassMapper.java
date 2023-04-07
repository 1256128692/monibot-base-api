package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbProjectMonitorClassMapper extends BaseMapper<TbProjectMonitorClass> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectMonitorClass record);

    int insertSelective(TbProjectMonitorClass record);

    TbProjectMonitorClass selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectMonitorClass record);

    int updateByPrimaryKey(TbProjectMonitorClass record);
}