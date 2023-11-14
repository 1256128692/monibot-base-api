package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryDmDataPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryDmDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryStDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.DmThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.DmThematicAnalysisPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.StThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicMonitorPointInfo;
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
}
