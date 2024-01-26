package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataDeviceWarnType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DeviceWarnDeviceType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLogDealStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLogDealType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.StartWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.CompanyPlatformParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.AddWarnWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.FillDealOpinionParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnLatestInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceWarnLatestInfo;
import cn.shmedo.monitor.monibotbaseapi.service.IWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.WorkFlowTemplateService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final TbWarnNotifyRelationMapper tbWarnNotifyRelationMapper;
    private final TbVideoDeviceMapper tbVideoDeviceMapper;

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

    @Override
    public Map<String, Object> queryUnreadWarnLatest(CompanyPlatformParam param) {
        Map<String, Object> result = new HashMap<>();
        //TODO 获取到最新未读设备/数据报警通知ID
        List<Integer> notifyIDList = List.of(1, 2);
        Map<DataDeviceWarnType, List<TbWarnNotifyRelation>> typeMap = tbWarnNotifyRelationMapper.selectList(
                        new LambdaQueryWrapper<TbWarnNotifyRelation>().in(TbWarnNotifyRelation::getNotifyID, notifyIDList))
                .stream().collect(Collectors.groupingBy(u -> DataDeviceWarnType.fromCode(u.getType())));
        typeMap.forEach((k, v) -> {
            TbWarnNotifyRelation tbWarnNotifyRelation = v.stream().findAny().orElseThrow();
            Integer warnLogID = tbWarnNotifyRelation.getWarnLogID();
            Integer notifyID = tbWarnNotifyRelation.getNotifyID();
            switch (k) {
                case DATA -> {
                    DataWarnLatestInfo info = tbDataWarnLogMapper.selectDataWarnBaseInfoByID(warnLogID);
                    info.setNotifyID(notifyID);
                    result.put("dataWarn", info);
                }
                case DEVICE -> {
                    DeviceWarnLatestInfo info = new DeviceWarnLatestInfo();
                    TbDeviceWarnLog tbDeviceWarnLog = tbDeviceWarnLogMapper.selectById(warnLogID);
                    info.setWarnLogID(tbDeviceWarnLog.getId());
                    info.setWarnName("设备离线");
                    info.setWarnTime(tbDeviceWarnLog.getWarnTime());
                    info.setDeviceToken(tbDeviceWarnLog.getDeviceSerial());
                    info.setNotifyID(notifyID);
                    fillDeviceInfo(info, param.getCompanyID());
                    result.put("deviceWarn", info);
                }
            }
        });
        return result;
    }

    private void fillDeviceInfo(DeviceWarnLatestInfo info, Integer companyID) {
        List<DeviceWarnLatestInfo> infoList = List.of(info);
        Set<String> deviceTokens = Set.of(info.getDeviceToken());
        Map<String, TbVideoDevice> deviceTokenMap = tbVideoDeviceMapper.selectList(new LambdaQueryWrapper<TbVideoDevice>()
                .in(TbVideoDevice::getDeviceToken, deviceTokens)).stream().collect(Collectors
                .toMap(TbVideoDevice::getDeviceSerial, Function.identity()));
        TransferUtil.applyDeviceBase(infoList,
                () -> QueryDeviceBaseInfoParam.builder().deviceTokens(deviceTokens).companyID(companyID).build(),
                DeviceWarnLatestInfo::getDeviceToken,
                (e, device) -> {
                    String deviceToken = e.getDeviceToken();
                    if (deviceTokenMap.containsKey(e.getDeviceToken())) {
                        e.setDeviceType(DeviceWarnDeviceType.VIDEO_DEVICE.getCode());
                        Optional.of(deviceToken).map(deviceTokenMap::get).map(TbVideoDevice::getDeviceType).ifPresent(e::setDeviceModel);
                    } else {
                        e.setDeviceType(DeviceWarnDeviceType.IOT_DEVICE.getCode());
                        e.setDeviceModel(device.getProductName());
                    }
                });
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
