package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorItemMapper extends BaseMapper<TbMonitorItem> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorItem record);

    int insertSelective(TbMonitorItem record);

    TbMonitorItem selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorItem record);

    int updateByPrimaryKey(TbMonitorItem record);

    List<MonitorItemBaseInfo> selectListByMonitorClassAndProID(List<Integer> monitorClassIDList, Integer projectID);

    List<MonitorItemBaseInfo> selectListByCondition(Integer companyID, List<Integer> proIDList, Integer queryType);

    void updateByCondition(Integer projectID, Integer monitorClass, List<Integer> monitorItemIDList);

    void updateMonitorClassToNull(Integer projectID, Integer monitorClass);
}