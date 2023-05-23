package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.QueryMonitorGroupItemNameListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.QueryProjectGroupInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.Group4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.ProjectGroupPlainInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.SimpleMonitorInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TbMonitorGroupMapper extends BasicMapper<TbMonitorGroup>{
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorGroup record);

    int insertSelective(TbMonitorGroup record);

    TbMonitorGroup selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorGroup record);

    int updateByPrimaryKey(TbMonitorGroup record);

    void updateImg(String path, Integer groupID, Integer userID, Date date);

    IPage<Group4Web> queryPage(Page<Group4Web> page, Integer projectID, String groupName, String secondaryGroupName, Integer monitorItemID, Boolean parented);

    List<Group4Web> queryGroup4WebByParentIDs(List<Integer> parentIDList);

    Integer selectCountByName(String name, boolean flag, Integer projectID);

    List<Group4Web> queryList(Integer projectID, String groupName, String secondaryGroupName, Integer monitorItemID, Boolean parented);

    List<SimpleMonitorInfo> queryMonitorGroupItemNameList(QueryMonitorGroupItemNameListParam pa);

    List<ProjectGroupPlainInfo> queryProjectGroupInfoList(@Param("param") QueryProjectGroupInfoParam param);
}