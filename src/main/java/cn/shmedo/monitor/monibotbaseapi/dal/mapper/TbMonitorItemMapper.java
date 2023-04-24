package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItem4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.GroupMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorItemWithPoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collection;
import java.util.Date;
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

    void updateProjectIDBatch(List<Integer> monitorItemIDList, Integer projectID, Integer userID, Date now);

    IPage<MonitorItem4Web> queryPage(Page<MonitorItem4Web> page,Integer companyID, Integer projectID, Byte createType, String monitorItemName, Integer monitorType, List<Integer> idList, Boolean companyItem);

    List<MonitorItemV1> queryMonitorItemV1By(Integer projectID, String monitorItemName, Integer monitorType);

    List<MonitorItemWithPoint> queryMonitorItemWithPointBy(Integer projectID, List<Integer> monitorItemIDList);

    void insertBatch(Collection<TbMonitorItem> collection);

    List<GroupMonitorItem> queryMonitorItemByGroupIDs(List<Integer> groupIDList);
}