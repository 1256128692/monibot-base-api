package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeTemplateCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.ProjectConditionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateDeviceCountStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.OtherDeviceCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DeviceAssetsStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectStatisticsService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectStatisticsServiceImpl implements ProjectStatisticsService {

    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final IotService iotService;

    private final TbVideoDeviceMapper videoDeviceMapper;

    private final TbOtherDeviceMapper tbOtherDeviceMapper;

    private final RedisService redisService;
    @Override
    public Object updateDeviceCountStatistics(UpdateDeviceCountStatisticsParam pa) {

        // 我要查询全部工程的统计
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectAll();

        if (CollectionUtils.isEmpty(tbProjectInfos)) {
            return true;
        }

        List<DeviceAssetsStatisticsInfo> voList = new LinkedList<>();

        Map<Integer, List<TbProjectInfo>> projectsByCompanyID = tbProjectInfos.stream()
                .collect(Collectors.groupingBy(TbProjectInfo::getCompanyID));


        // 统计智能设备
        for (Map.Entry<Integer, List<TbProjectInfo>> entry : projectsByCompanyID.entrySet()) {
            Integer companyID = entry.getKey();
            List<Integer> projectIDs = entry.getValue().stream().map(TbProjectInfo::getID).collect(Collectors.toList());
            List<String> sendAddressList = projectIDs.stream().map(String::valueOf).toList();

            QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                    .companyID(companyID)
                    .sendType(SendType.MDMBASE.toInt())
                    .sendAddressList(sendAddressList)
                    .sendEnable(true)
                    .deviceToken(null)
                    .online(null)
                    .productID(null)
                    .build();

            ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);

            // 在这里处理查询结果
            if (result.apiSuccess()) {
                // 遍历全部工程
                for (Integer projectID : projectIDs) {
                    DeviceAssetsStatisticsInfo vo = new DeviceAssetsStatisticsInfo();
                    vo.setProjectID(projectID);

                    // 获取当前工程下的设备列表
                    List<SimpleDeviceV5> devices = result.getData().stream().filter(r -> r.getSendAddressList()
                            .contains(projectID.toString())).toList();

                    // 统计设备数量、设备在线数量、设备离线数量
                    if (CollectionUtils.isNotEmpty(devices)) {
                        vo.setIntelligenceCount(devices.size());
                        vo.setIntelligenceOnlineCount((int) devices.stream().filter(device ->
                                Boolean.TRUE.equals(device.getOnlineStatus())).count());
                        vo.setIntelligenceOfflineCount((int) devices.stream().filter(device ->
                                Boolean.FALSE.equals(device.getOnlineStatus()) || device.getOnlineStatus() == null).count());
                        vo.setVideoCount(0);
                        vo.setVideoOnlineCount(0);
                        vo.setVideoOfflineCount(0);
                        vo.setOtherCount(0);
                    } else {
                        vo.setIntelligenceCount(0);
                        vo.setIntelligenceOnlineCount(0);
                        vo.setIntelligenceOfflineCount(0);
                        vo.setVideoCount(0);
                        vo.setVideoOnlineCount(0);
                        vo.setVideoOfflineCount(0);
                        vo.setOtherCount(0);
                    }

                    // 将统计信息添加到voList中
                    voList.add(vo);
                }

            }
        }


        // 统计视频设备
        List<DeviceAssetsStatisticsInfo> videoDeviceOnlineInfoList  = videoDeviceMapper.queryOnlineCountByProjectIDList(
                tbProjectInfos.stream().map(TbProjectInfo::getID).collect(Collectors.toList()));

        if (CollectionUtils.isNotEmpty(videoDeviceOnlineInfoList)) {
            videoDeviceOnlineInfoList.forEach(v -> {
                DeviceAssetsStatisticsInfo deviceAssetsStatisticsInfo = voList.stream().filter(projectInfo -> projectInfo.getProjectID().equals(v.getProjectID())).findFirst().orElse(null);
                if (ObjectUtil.isNotNull(deviceAssetsStatisticsInfo)) {
                    deviceAssetsStatisticsInfo.setVideoCount(v.getVideoCount());
                    deviceAssetsStatisticsInfo.setVideoOnlineCount(v.getVideoOnlineCount());
                    deviceAssetsStatisticsInfo.setVideoOfflineCount(v.getVideoOfflineCount());
                }
            });
        }

        // 统计其它设备
        List<OtherDeviceCountInfo> otherDeviceCountInfoList = tbOtherDeviceMapper.queryCountByProjectIDList(
                tbProjectInfos.stream().map(TbProjectInfo::getID).collect(Collectors.toList())
        );
        if (CollectionUtils.isNotEmpty(otherDeviceCountInfoList)) {
            otherDeviceCountInfoList.forEach(v -> {
                DeviceAssetsStatisticsInfo deviceAssetsStatisticsInfo = voList.stream().filter(projectInfo -> projectInfo.getProjectID().equals(v.getProjectID())).findFirst().orElse(null);
                if (ObjectUtil.isNotNull(deviceAssetsStatisticsInfo)) {
                    deviceAssetsStatisticsInfo.setOtherCount(v.getOtherCount());
                }
            });
        }


        voList.forEach(v -> {
            redisService.put(RedisKeys.DEVICE_ASSET_KEY, v.getProjectID().toString(), v);
        });


        return true;
    }

    @Override
    public Object queryDeviceCountStatistics(ProjectConditionParam pa) {

        DeviceAssetsStatisticsInfo vo = redisService.get(RedisKeys.DEVICE_ASSET_KEY,
                pa.getProjectID().toString(), DeviceAssetsStatisticsInfo.class);
        if (ObjectUtil.isNull(vo)) {
            return null;
        }

        // 对属性进行非空校验
        vo.setVideoRate(calculateRate(vo.getVideoCount(), vo.getVideoOnlineCount()));
        vo.setIntelligenceRate(calculateRate(vo.getIntelligenceCount(), vo.getIntelligenceOnlineCount()));
        vo.setProjectTotalCount(vo.getIntelligenceCount() + vo.getVideoCount() + vo.getOtherCount());

        return vo;
    }

    /**
     * 计算比率，并保留两位小数
     * @param totalCount
     * @param onlineCount
     * @return
     */
    private Double calculateRate(Integer totalCount, Integer onlineCount) {
        if (ObjectUtil.isNull(totalCount) || ObjectUtil.isNull(onlineCount) || totalCount == 0) {
            return 0.0;
        }

        double rate = (double) onlineCount / totalCount;
        return Math.round(rate * 100.0) / 100.0;
    }

}
