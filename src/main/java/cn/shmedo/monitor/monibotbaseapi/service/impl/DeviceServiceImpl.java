package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.BatchUpdateVideoDeviceStatusParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDeviceWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.ProjectWithServiceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.service.IDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDeviceWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceServiceImpl implements IDeviceService {


    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;

    private final ITbDeviceWarnLogService tbDeviceWarnLogService;

    private final TbProjectInfoMapper projectInfoMapper;

    private final IotService iotService;

    @Override
    public Boolean batchHandlerIotDeviceStatusChange(BatchUpdateVideoDeviceStatusParam pa) {

        // 查询全部的projectID
        List<ProjectWithServiceInfo> tbProjectInfos = tbProjectInfoMapper.selectAllIncludeServiceID();

        // 查询全部的设备预警日志消息
        List<TbDeviceWarnLog> tbDeviceWarnLogs = tbDeviceWarnLogMapper.selectList(null);


        List<Integer> projectIDList = tbProjectInfos.stream().map(ProjectWithServiceInfo::getID).collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(projectIDList)) {
            return true;
        }

        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(pa.getCompanyID())
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(projectIDList.stream().map(String::valueOf).toList())
                .build();
        ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);

        if (result.apiSuccess()) {
            for (SimpleDeviceV5 device : result.getData()) {

                List<String> sendAddressList = device.getSendAddressList();

                if (!CollectionUtil.isNullOrEmpty(sendAddressList)) {
                    for (String serviceID : sendAddressList) {
                        if (!CollectionUtil.isNullOrEmpty(tbProjectInfos)) {
                            ProjectWithServiceInfo projectWithServiceInfo = tbProjectInfos.stream().filter(project -> serviceID.equals(String.valueOf(project.getID()))).findFirst().orElse(null);
                            if (projectWithServiceInfo != null) {
                                List<Integer> platformIDList = projectInfoMapper.selectPlatformListByProjectID(projectWithServiceInfo.getID());
                                if (!CollectionUtil.isNullOrEmpty(platformIDList)) {
                                    platformIDList.forEach(p -> {
                                        TbDeviceWarnLog tbDeviceWarnLog = tbDeviceWarnLogs.stream().filter(deviceWarn ->
                                                        deviceWarn.getDeviceSerial().equals(device.getDeviceToken())
                                                                && deviceWarn.getPlatform().equals(p)
                                                                && deviceWarn.getProjectID().equals(Integer.valueOf(serviceID))
                                                                && deviceWarn.getWarnEndTime() == null)
                                                .findFirst().orElse(null);
                                        tbDeviceWarnLogService.saveDeviceWarnLog(new SaveDeviceWarnParam(
                                                projectWithServiceInfo.getCompanyID(), p,
                                                projectWithServiceInfo.getID(), DateUtil.date(), device.getProductName(),
                                                device.getDeviceToken(), projectWithServiceInfo.getProjectName(), "IoT设备", tbDeviceWarnLog,
                                                device.getOnlineStatus()));
                                    });
                                }
                            }
                        }
                    }
                }
            }

        }

        return true;
    }
}
