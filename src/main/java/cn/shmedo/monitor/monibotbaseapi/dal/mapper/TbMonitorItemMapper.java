package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointAllInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectLocationInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItem4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemNameFullInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.GroupMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorItemWithPoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

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

    List<MonitorItemBaseInfo> selectListByMonitorClassAndProID(List<Integer> monitorClassIDList, Integer projectID, Boolean enable);

    List<MonitorItemBaseInfo> selectListByCondition(Integer companyID, List<Integer> proIDList, Integer queryType);

    void updateByCondition(Integer projectID, Integer monitorClass, List<Integer> monitorItemIDList);

    void updateMonitorClassToNull(Integer projectID, Integer monitorClass);

    void updateProjectIDBatch(List<Integer> monitorItemIDList, Integer projectID, Integer userID, Date now);

    IPage<MonitorItem4Web> queryPage(Page<MonitorItem4Web> page,
                                     @Param("companyID") Integer companyID,
                                     @Param("projectID") Integer projectID,
                                     @Param("createType") Byte createType,
                                     @Param("queryCode") String queryCode,
                                     @Param("monitorType") Integer monitorType,
                                     @Param("idList") List<Integer> idList,
                                     @Param("companyItem") Boolean companyItem,
                                     @Param("monitorItemID") Integer monitorItemID,
                                     @Param("enable") Boolean enable);

    List<MonitorItemV1> queryMonitorItemV1By(Integer projectID, String monitorItemName, Integer monitorType, Boolean enable);

    List<MonitorItemWithPoint> queryMonitorItemWithPointBy(Integer projectID, List<Integer> monitorItemIDList, Boolean itemEnable);

    void insertBatch(Collection<TbMonitorItem> collection);

    List<GroupMonitorItem> queryMonitorItemByGroupIDs(List<Integer> groupIDList);

    List<TbMonitorItem> selectListByMonitorPointIDsAndProjectIDs(List<Integer> monitorPointIDList, List<Integer> projectIDList);

    List<MonitorItemNameFullInfo> queryMonitorItemNameFullInfo(@Param("param") QueryWorkOrderStatisticsParam param);

    List<TbMonitorItem> selectListBySensorIDsAndProjectIDs(List<Integer> sensorIDList, List<Integer> projectIDList);

    List<ProjectLocationInfo> getProjectLocation(@Param("companyID") Integer companyID, @Param("monitorType") Integer monitorType, @Param("monitorClassType") Integer monitorClassType);

    List<MonitorPointAllInfoV1> queryListByProjectIDAndMonitorItemID(Integer projectID, Integer monitorItemID, Boolean enable);

    List<MonitorItemBaseInfo> selectListByEventIDList(List<Integer> eventIDList);

    List<Integer> selectByMonitorType(@Param("companyID") Integer companyID, @Param("createType") Byte createType);
}