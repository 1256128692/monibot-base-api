package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryProjectBaseInfoResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper
public interface TbProjectInfoMapper extends BaseMapper<TbProjectInfo> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectInfo record);

    int insertSelective(TbProjectInfo record);

    TbProjectInfo selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectInfo record);

    int updateByPrimaryKey(TbProjectInfo record);

    void updateCompanyID(Integer projectID, Integer companyID, Integer userID, Date date);

    void updateExpiryDate(Integer projectID, Date newExpiryDate, Integer userID, Date date);

    void updatePathByID(String path, Integer projectID);

    List<Integer> getProjectIDByProperty(@Param("list") List<QueryProjectListRequest.Property> propertyEntity,
                                         @Param("size") Integer size);

    IPage<ProjectInfo> getProjectList(IPage<ProjectInfo> page,
                                      @Param("pa") QueryProjectListRequest pa);

    int countByNameExcludeID(String projectName, Integer projectID);

    int countByProjectIDList(List<Integer> idList, Integer companyID);

    List<Integer> countComany(List idList);

    int deleteProjectInfoList(List idList);

    List<TbProjectInfo> selectProjectInfoByCompanyID(Integer companyID);

    List<QueryProjectBaseInfoResponse> selectListByCompanyIDAndMonitorItemName(Integer companyID, String monitorItemName);
}