package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.CompanyPlatformParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.AddWarnLogBindWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.AddWarnWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.FillDealOpinionParam;

import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 14:20
 */
public interface IWarnLogService {
    void addWarnWorkFlowTask(Integer userID, AddWarnWorkFlowTaskParam param);

    void fillDealOpinion(Integer userID, FillDealOpinionParam param);

    Map<String, Object> queryUnreadWarnLatest(CompanyPlatformParam param, String accessToken);

    void addWarnLogBindWorkFlowTask(Integer userID, AddWarnLogBindWorkFlowTaskParam param);
}
