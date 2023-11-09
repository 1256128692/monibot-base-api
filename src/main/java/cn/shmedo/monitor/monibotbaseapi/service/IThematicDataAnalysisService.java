package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.Date;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:59
 */
public interface IThematicDataAnalysisService {
    @Deprecated
    StThematicAnalysisInfo queryStGroupRealData(QueryStDataParam param);

    @Deprecated
    DmThematicAnalysisInfo queryDmAnalysisData(QueryDmDataParam param);

    @Deprecated
    PageUtil.Page<DmThematicAnalysisPageInfo> queryDmAnalysisDataPage(QueryDmDataPageParam param);

    @Deprecated
    List<Date> queryDmPageDataList(QueryDmDataParam param);

    List<ThematicMonitorPointInfo> queryThematicMonitorPointByProjectID(Integer projectID);

    List<ThematicGroupPointListInfo> queryThematicGroupPointList(QueryThematicGroupPointListParam param);

    List<ThematicQueryTransverseInfo> queryTransverseList(QueryTransverseListParam param);

    PageUtil.Page<ThematicQueryTransverseInfo> queryTransversePage(QueryTransversePageParam param);
}
