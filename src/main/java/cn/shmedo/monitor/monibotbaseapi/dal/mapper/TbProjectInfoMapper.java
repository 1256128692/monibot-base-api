package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectWithRaiseCropsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.ProjectWithIrrigationInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryProjectBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ConfigBaseResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.ProjectVideoInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface TbProjectInfoMapper extends BaseMapper<TbProjectInfo> {
    void updateCompanyID(Integer projectID, Integer companyID, Integer userID, Date date);

    void updateExpiryDate(Integer projectID, Date newExpiryDate, Integer userID, Date date);

    void updatePathByID(String path, Integer projectID);

    List<Integer> getProjectIDByProperty(@Param("list") List<QueryProjectListRequest.Property> propertyEntity,
                                         @Param("projectIDList") Collection<Integer> projectIDList);

    IPage<ProjectInfo> getProjectPage(IPage<ProjectInfo> page,
                                      @Param("pa") QueryProjectListRequest pa);

    int countByNameExcludeID(String projectName, Integer projectID);

    int countByProjectIDList(List<Integer> idList, Integer companyID);

    List<Integer> countComany(List idList);

    int deleteProjectInfoList(List idList);

    List<TbProjectInfo> selectListByCompanyIDAndProjectIDList(@Param("companyID") Integer companyID,
                                                              @Param("projectIDList") List<Integer> projectIDList);

    List<QueryProjectBaseInfoResponse> selectListByCompanyIDAndMonitorItemName(Integer companyID, String monitorItemName, List<String> platformTypeSet);

    List<ConfigBaseResponse> selectMonitorPointRelateSensorConfig(@Param("monitorPointID") Integer monitorPointID,
                                                                  @Param("monitorType") Integer monitorType,
                                                                  @Param("groupName") String group,
                                                                  @Param("keyName") String key);

    List<ConfigBaseResponse> selectMonitorGroupRelatePointConfig(@Param("monitorGroupID") Integer monitorGroupID,
                                                                 @Param("monitorType") Integer monitorType,
                                                                 @Param("groupName") String group,
                                                                 @Param("keyName") String key);

    void updateLevel(Byte level, List<Integer> pidList, Integer userID, Date date);

    List<ProjectInfo> getProjectList(QueryProjectListRequest pa);

    void updateLevel2Unallocated();

    List<TbProjectInfo> selectAll();

    void updateLevel2Unallocatedwhennorealtion();

    List<ProjectVideoInfo> selectListByIDs(List<Integer> projectIDs);

    List<ProjectWithIrrigationInfo> queryProjectWithRaiseCrops(QueryProjectWithRaiseCropsParam pa);

    List<Integer> selectPlatformListByProjectID(Integer projectID);
}