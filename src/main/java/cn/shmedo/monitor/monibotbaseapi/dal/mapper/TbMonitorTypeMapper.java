package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorType4web;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

    IPage<TbMonitorType4web> queryPage(Page<TbMonitorType4web> page,  Integer companyID, Byte createType, String fuzzyTypeName, List<Integer> typeList);
}