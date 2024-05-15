package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryThematicGroupPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryWetLineConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.IDNameAlias;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointAndItemInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.GroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorPointBaseInfoV2;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.MonitorPointDataInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.MonitorItemCountStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicGroupPointListInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.WetLineConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.WtVideoPageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TbMonitorPointMapper extends BasicMapper<TbMonitorPoint> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorPoint record);

    int insertSelective(TbMonitorPoint record);

    TbMonitorPoint selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorPoint record);

    int updateByPrimaryKey(TbMonitorPoint record);

    List<TbMonitorPoint> selectMonitorTypeAndProIDByProIDList(List<Integer> proIDList);

    Integer selectMonitorTypeCount(List<Integer> monitorPointIDs);

    List<MonitorPointAndItemInfo> selectListByCondition(List<Integer> projectIDList, Integer monitorType, Integer monitorItemID, Integer monitorClassType, String monitorItemName);

    IPage<MonitorPoint4Web> queryPage(Page<MonitorPoint4Web> page, Integer projectID, Integer monitorType, Integer monitorItemID, String queryCode);

    List<IDNameAlias> querySimpleBy(Integer projectID, Integer groupID, List<Integer> monitorItemIDList);

    void insertBatch(List<TbMonitorPoint> list);

    void updateBatch(List<TbMonitorPoint> list, Boolean selectUpdate);

    List<GroupPoint> queryGroupPointByGroupIDs(List<Integer> allGroupIDList);

    List<VideoMonitorPointLiveInfo> selectListByIDList(List<Integer> monitorPointIDList);

    List<WtVideoPageInfo> selectVideoPointListByCondition(List<Integer> projectIDList, Integer onlineInt,
                                                          Integer status, String areaCode,
                                                          Integer monitorItemID, String monitorItemName, Integer monitorType, String videoType, Integer monitorPointID);

    List<MonitorPointWithSensor> selectListByProjectIDsAndMonitorItemName(List<Integer> projectIDList, String monitorItemName);

    List<MonitorPointWithItemBaseInfo> selectPointWithItemBaseInfo(@Param("projectIDList") List<Integer> projectIDList,
                                                                   @Param("monitorTypeList") List<Integer> monitorTypeList);

    List<TbMonitorPoint> selectListByProjectIDAndMonitorClass(Integer projectID, Integer monitorClass);

    List<MonitorPointBaseInfoV1> selectPointListByMonitorItemIDList(List<Integer> monitorItemIDList);

    List<TbMonitorPoint> selectPointListByIDList(List<Integer> monitorPointIDList);

    List<ThematicGroupPointListInfo> selectThematicGroupPointList(@Param("param") QueryThematicGroupPointListParam param);

    List<Integer> selectItemIDsByIDs(List<Integer> monitorPointIDList);

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> selectMonitorTypeExValuesByPointIDList(List<Integer> monitorPointIDList);

    List<MonitorPointDataInfo> selectMonitorPointDataInfoListByIDList(List<Integer> monitorPointIDList);

    WetLineConfigInfo selectWetLineConfig(@Param("param") QueryWetLineConfigParam param);

    List<MonitorPointBaseInfoV2> selectListByEigenValueIDListAndMonitorItemIDList(@Param("eigenValueIDList") List<Integer> eigenValueIDList,
                                                              @Param("monitorItemIDList") List<Integer> monitorItemIDList);

    List<MonitorPointSimple> listByProjectType(@Param("projectIDList") Collection<Integer> projectIDList,
                                               @Param("monitorTypeList") Collection<Integer> monitorTypeList);

    List<MonitorPointSimple> listByProjectTypeAndMonitorType(Integer projectType, Integer monitorType, Integer companyID);

    List<MonitorPoint4Web> queryList(Integer projectID, Integer monitorType, Integer monitorItemID, String queryCode);

    List<MonitorItemCountStatisticsInfo> selectItemCountByProjectID(Integer projectID, List<Integer> monitorIDList);

    List<MonitorPointInfo> selectMonitorPoints(Integer projectID, Collection<Integer> monitorPointIDList);
}