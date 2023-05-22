package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryDmDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryStDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.DmThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.StThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicMonitorPointInfo;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:59
 */
public interface IThematicDataAnalysisService {
    StThematicAnalysisInfo queryStGroupRealData(QueryStDataParam param);

    DmThematicAnalysisInfo queryDmAnalysisData(QueryDmDataParam param);

    List<ThematicMonitorPointInfo> queryThematicMonitorPointByProjectID(Integer projectID);
}
