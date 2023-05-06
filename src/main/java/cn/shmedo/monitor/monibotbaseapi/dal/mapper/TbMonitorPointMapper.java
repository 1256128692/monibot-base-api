package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.IDNameAlias;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointAndItemInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.GroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPoint4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.WtVideoPageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface TbMonitorPointMapper extends BasicMapper<TbMonitorPoint>{
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorPoint record);

    int insertSelective(TbMonitorPoint record);

    TbMonitorPoint selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorPoint record);

    int updateByPrimaryKey(TbMonitorPoint record);

    List<TbMonitorPoint> selectMonitorTypeAndProIDByProIDList(List<Integer> proIDList);

    Integer selectMonitorTypeCount(List<Integer> monitorPointIDs);

    List<MonitorPointAndItemInfo> selectListByCondition(List<Integer> projectIDList, Integer monitorType, Integer monitorItemID, Integer monitorClassType);

    IPage<MonitorPoint4Web> queryPage(Page<MonitorPoint4Web> page, Integer projectID, Integer monitorType, String monitorItemName, Integer sensorID, String pointName);

    List<IDNameAlias> querySimpleBy(Integer projectID, Integer groupID, List<Integer> monitorItemIDList);

    void insertBatch(List<TbMonitorPoint> list);

    void updateBatch(List<TbMonitorPoint> list, Boolean selectUpdate);

    List<GroupPoint> queryGroupPointByGroupIDs(List<Integer> allGroupIDList);

    List<VideoMonitorPointLiveInfo> selectListByIDList(List<Integer> monitorPointIDList);

    List<WtVideoPageInfo> selectVideoPointListByCondition(List<Integer> projectIDList, Boolean online, String areaCode,
                                                          Integer monitorItemID, Integer monitorType);
}