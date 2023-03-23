package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorTypeMapper extends BaseMapper<TbMonitorType> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorType record);

    int insertSelective(TbMonitorType record);

    TbMonitorType selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorType record);

    int updateByPrimaryKey(TbMonitorType record);

    List<TbMonitorType> selectAll();

    List<MonitorTypeBaseInfo> selectMonitorBaseInfo(Integer companyID);
}