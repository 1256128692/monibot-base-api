package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLogDealStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLogDealType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.StartWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.AddWarnWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.FillDealOpinionParam;
import cn.shmedo.monitor.monibotbaseapi.service.IWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.WorkFlowTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 14:20
 */
@Service
@RequiredArgsConstructor
public class WarnLogServiceImpl implements IWarnLogService {
    private final WorkFlowTemplateService workFlowTemplateService;
    private final TbDataWarnLogMapper tbDataWarnLogMapper;
    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWarnWorkFlowTask(Integer userID, AddWarnWorkFlowTaskParam param) {
        ResultWrapper<Integer> wrapper = workFlowTemplateService.startWorkFlowTask(new StartWorkFlowTaskParam(
                param.getCompanyID(), param.getProjectID(), param.getTemplateID(), param.getStartBpmnNodeID()));
        Integer data = wrapper.getData();
        Date current = new Date();
        switch (param.getDataDeviceWarnType()) {
            case DATA -> {
                TbDataWarnLog tbDataWarnLog = param.getTbDataWarnLog();
                tbDataWarnLog.setWorkOrderID(data);
                dealAndSaveDataWarnLog(tbDataWarnLog, WarnLogDealType.WORK_ORDER, userID, current);
            }
            case DEVICE -> {
                TbDeviceWarnLog tbDeviceWarnLog = param.getTbDeviceWarnLog();
                tbDeviceWarnLog.setWorkOrderID(data);
                dealAndSaveDeviceWarnLog(tbDeviceWarnLog, WarnLogDealType.WORK_ORDER, userID, current);
            }
        }
    }

    @Override
    public void fillDealOpinion(Integer userID, FillDealOpinionParam param) {
        Date current = new Date();
        switch (param.getDataDeviceWarnType()) {
            case DATA -> {
                TbDataWarnLog tbDataWarnLog = param.getTbDataWarnLog();
                tbDataWarnLog.setDealContent(param.getOpinion());
                dealAndSaveDataWarnLog(tbDataWarnLog, WarnLogDealType.DEAL_CONTENT, userID, current);
            }
            case DEVICE -> {
                TbDeviceWarnLog tbDeviceWarnLog = param.getTbDeviceWarnLog();
                tbDeviceWarnLog.setDealContent(param.getOpinion());
                dealAndSaveDeviceWarnLog(tbDeviceWarnLog, WarnLogDealType.DEAL_CONTENT, userID, current);
            }
        }
    }

    private void dealAndSaveDataWarnLog(TbDataWarnLog tbDataWarnLog, WarnLogDealType dealType, Integer userID, Date current) {
        tbDataWarnLog.setDealType(dealType.getCode());
        tbDataWarnLog.setDealUserID(userID);
        tbDataWarnLog.setDealTime(current);
        tbDataWarnLog.setDealStatus(WarnLogDealStatus.DEAL.getCode());
        tbDataWarnLogMapper.updateById(tbDataWarnLog);
    }

    private void dealAndSaveDeviceWarnLog(TbDeviceWarnLog tbDeviceWarnLog, WarnLogDealType dealType, Integer userID, Date current) {
        tbDeviceWarnLog.setDealType(dealType.getCode());
        tbDeviceWarnLog.setDealUserID(userID);
        tbDeviceWarnLog.setDealTime(current);
        tbDeviceWarnLog.setDealStatus(WarnLogDealStatus.DEAL.getCode());
        tbDeviceWarnLogMapper.updateById(tbDeviceWarnLog);
    }
}
