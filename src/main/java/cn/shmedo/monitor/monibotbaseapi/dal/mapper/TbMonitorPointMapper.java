package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointAndItemInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorPointMapper  extends BaseMapper<TbMonitorPoint> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorPoint record);

    int insertSelective(TbMonitorPoint record);

    TbMonitorPoint selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorPoint record);

    int updateByPrimaryKey(TbMonitorPoint record);

    List<TbMonitorPoint> selectMonitorTypeAndProIDByProIDList(List<Integer> proIDList);

    Integer selectMonitorTypeCount(List<Integer> monitorPointIDs);

    List<MonitorPointAndItemInfo> selectListByCondition(List<Integer> projectIDList, Integer monitorType, Integer monitorItemID);
}