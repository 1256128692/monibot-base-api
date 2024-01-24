package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.AddWarnWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.FillDealOpinionParam;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 14:20
 */
public interface IWarnLogService {
    void addWarnWorkFlowTask(Integer userID,AddWarnWorkFlowTaskParam param);

    void fillDealOpinion(Integer userID, FillDealOpinionParam param);
}
