package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbMonitorItemMapper extends BaseMapper<TbMonitorItem> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorItem record);

    int insertSelective(TbMonitorItem record);

    TbMonitorItem selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorItem record);

    int updateByPrimaryKey(TbMonitorItem record);
}