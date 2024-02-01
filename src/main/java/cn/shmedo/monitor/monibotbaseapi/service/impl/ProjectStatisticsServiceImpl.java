package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceInfoByUniqueTokensParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.WarnInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.OtherDeviceCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DataCountStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.DeviceAssetsStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.MonitorItemCountStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoV4;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorWithDataSourceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectStatisticsService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectStatisticsServiceImpl implements ProjectStatisticsService {

    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final IotService iotService;

    private final TbVideoDeviceMapper videoDeviceMapper;

    private final TbOtherDeviceMapper tbOtherDeviceMapper;

    private final TbMonitorPointMapper tbMonitorPointMapper;

    private final TbMonitorItemMapper tbMonitorItemMapper;

    private final TbSensorMapper tbSensorMapper;

    private final TbSensorDataSourceMapper tbSensorDataSourceMapper;

    private final TbUserFollowMonitorPointMapper tbUserFollowMonitorPointMapper;

    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    private final SensorDataDao sensorDataDao;

    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private final RedisService redisService;
    @Override
    public Boolean updateDeviceCountStatistics(UpdateDeviceCountStatisticsParam pa) {

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
    public DeviceAssetsStatisticsInfo queryDeviceCountStatistics(ProjectConditionParam pa) {

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

    @Override
    public DataCountStatisticsInfo queryDataCountStatistics(ProjectConditionParam pa) {
        DataCountStatisticsInfo vo = new DataCountStatisticsInfo();

        vo.setDataCount(Integer.valueOf(redisService.get(RedisKeys.DEVICE_DATA_COUNT_KEY + ":" + pa.getProjectID().toString())));

        // 查询含有传感器的监测点
        List<SensorBaseInfoV4> sensorBaseInfoV4List = tbSensorMapper.selectListByCondition(pa.getProjectID(), null, null);
        vo.setMonitorPointTotalCount(sensorBaseInfoV4List.stream().map(SensorBaseInfoV4::getMonitorPointID).distinct().count());

        // 查询含有监测点(且携带传感器) 的监测项目数量
        if (CollectionUtils.isNotEmpty(sensorBaseInfoV4List)) {
            List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectPointListByIDList(
                    sensorBaseInfoV4List.stream().map(SensorBaseInfoV4::getMonitorPointID).distinct().collect(Collectors.toList()));
            vo.setMonitorItemCount(tbMonitorPoints.stream().map(TbMonitorPoint::getMonitorItemID).distinct().count());
        } else {
            vo.setMonitorItemCount(0L);
        }

        return vo;
    }

    @Override
    public List<MonitorItemCountStatisticsInfo> queryMonitorItemCountStatistics(ProjectConditionParam pa) {

        // 查询含有传感器的监测点
        List<SensorBaseInfoV4> sensorBaseInfoV4List = tbSensorMapper.selectListByCondition(pa.getProjectID(), null, null);

        // 查询含有监测点(且携带传感器) 的监测项目数量
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectPointListByIDList(
                sensorBaseInfoV4List.stream().map(SensorBaseInfoV4::getMonitorPointID).distinct().collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(tbMonitorPoints)){
            return Collections.emptyList();
        }

        return tbMonitorPointMapper.selectItemCountByProjectID(pa.getProjectID(), tbMonitorPoints.stream().map(TbMonitorPoint::getID).collect(Collectors.toList()));
    }

    @Override
    public WarnInfo queryDistinctWarnTypeMonitorPointCount(ProjectConditionParam pa) {

        List<TbSensor> tbSensorList = tbSensorMapper.selectList(new QueryWrapper<TbSensor>().lambda()
                .eq(TbSensor::getProjectID, pa.getProjectID()));

        return WarnInfo.toBuliderNewVo(tbSensorList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserCollectionMonitorPoint(AddUserCollectionMonitorPointParam pa) {

        tbUserFollowMonitorPointMapper.insertBatch(pa.getUserFollowMonitorPointList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserCollectionMonitorPoint(DeleteUserCollectionMonitorPointParam pa) {

        tbUserFollowMonitorPointMapper.deleteBatch(pa.getMonitorPointIDList(), pa.getUserID());

    }

    @Override
    public Object querySingleProjectMonitorPointInfoList(QuerySingleProjectMonitorPointInfoListParam pa) {

        // 先查询所有传感器,然后按监测点进行分组,按照条件(监测项目ID列表,监测点名称,收藏的监测点ID列表)
        List<SensorBaseInfoV4> sensorBaseInfoV4List = tbSensorMapper.selectListByCondition(pa.getProjectID(),
                pa.getMonitorItemIDList(),
                pa.getMonitorPointName());

        if (CollectionUtils.isEmpty(sensorBaseInfoV4List)) {
            return Collections.emptyList();
        }

        // 分组完之后进行遍历,每个监测点根据传感器去判断自己的监测点状态,然后按照条件(监测点状态进行筛选)
        Map<Integer, List<SensorBaseInfoV4>> monitorPointInfoList = sensorBaseInfoV4List.stream()
                .collect(Collectors.groupingBy(SensorBaseInfoV4::getMonitorPointID));

        // 存储结果的列表
        List<SensorBaseInfoV4> resultList = new ArrayList<>();

        // 遍历分组后的数据
        List<SensorBaseInfoV4> finalResultList = resultList;
        monitorPointInfoList.forEach((monitorPointID, sensors) -> {

            sensors.forEach(sensor -> {
                // 监测点是否收藏
                if (pa.getMonitorPointIDList().contains(sensor.getMonitorPointID())) {
                    sensor.setMonitorPointCollection(true);
                } else {
                    sensor.setMonitorPointCollection(false);
                }
                if (sensor.getDeviceOnlineStatus() == null) {
                    // 为null默认离线
                    sensor.setDeviceOnlineStatus(0);
                }
            });

            // 只取一个传感器,取的规则按照当前点的传感器最高预警的一个即可
            SensorBaseInfoV4 minStatusSensor = sensors.stream()
                    .filter(sensor -> sensor.getDataWarnStatus() > 0)
                    .min(Comparator.comparingInt(SensorBaseInfoV4::getDataWarnStatus))
                    .orElse(null);

            // 将找到的传感器添加到结果列表中
            if (minStatusSensor != null) {
                finalResultList.add(minStatusSensor);
            } else {
                SensorBaseInfoV4 maxStatusSensor = sensors.stream()
                        .max(Comparator.comparingInt(SensorBaseInfoV4::getDataWarnStatus))
                        .orElse(null);
                finalResultList.add(maxStatusSensor);
            }


        });

        if (pa.getMonitorPointCollection() != null && pa.getMonitorPointCollection()) {
            resultList = resultList.stream().filter(r -> r.getMonitorPointCollection().equals(pa.getMonitorPointCollection())).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(pa.getMonitorStatusList())) {
            resultList = resultList.stream().filter(r -> pa.getMonitorStatusList().contains(r.getDataWarnStatus())).collect(Collectors.toList());
        }

        // 最后根据监测类型再去分组这些监测点,然后遍历监测类型,去查该类型下监测点的传感器的最新数据,封装打包
        Map<Integer, List<SensorBaseInfoV4>> monitorTypeList = resultList.stream()
                .collect(Collectors.groupingBy(SensorBaseInfoV4::getMonitorType));

        monitorTypeList.forEach((monitorType, sensors) -> {

            List<FieldBaseInfo> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByType(monitorType);

            List<FieldSelectInfo> fieldList = getFieldSelectInfoListFromModleTypeFieldList(monitorTypeFields);
            List<Integer> sensorIDList = sensors.stream().map(SensorBaseInfoV4::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> maps = sensorDataDao.querySensorNewData(sensorIDList, fieldList, false, monitorType);

            sensors.forEach(s -> {
                s.setMonitorTypeFields(monitorTypeFields);
                if (CollectionUtils.isNotEmpty(maps)) {
                    s.setSensorData(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID())).findFirst().orElse(null));
                    if (ObjectUtil.isNotNull(s.getSensorData())) {
                        s.setDataTime(DateUtil.parse((String) s.getSensorData().get(DbConstant.TIME_FIELD)));
                    }
                }
                // 墒情单独处理
//                if (monitorType == 9) {
//                    if (StringUtils.isNotBlank(s.getConfigFieldValue())) {
//                        s.getSensorData().put(DbConstant.SHANGQING_DEEP, JSONUtil.parseObj(s.getConfigFieldValue()).getByPath("$.埋深"));
//                    }
//                }
            });

        });

        // 按照时间倒序排序
        resultList.sort(Comparator.comparing(SensorBaseInfoV4::getDataTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return resultList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSensorOnlineStatusByIot(UpdateDeviceCountStatisticsParam pa) {

        List<SensorWithDataSourceInfo> sensorWithDataSourceInfoList = tbSensorMapper.selectListFromIotSerivce();

        if (CollectionUtils.isEmpty(sensorWithDataSourceInfoList)) {
            return true;
        }

        sensorWithDataSourceInfoList.forEach(s -> {
            if (StringUtils.isNotEmpty(s.getDataSourceToken()) && s.getDataSourceToken().contains("@")) {
                String[] parts = s.getDataSourceToken().split("@");
                s.setUniqueToken(parts[0]);
            }
        });

        List<String> uniqueTokenList = sensorWithDataSourceInfoList.stream()
                .map(SensorWithDataSourceInfo::getUniqueToken)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(uniqueTokenList)) {
            return true;
        }

        ResultWrapper<List<DeviceInfo>> result = iotService.queryDeviceInfoByUniqueTokens(
                new QueryDeviceInfoByUniqueTokensParam(uniqueTokenList));

        if (result.apiSuccess()) {
            if (CollectionUtils.isNotEmpty(result.getData())) {
                sensorWithDataSourceInfoList.forEach(s -> {
                    DeviceInfo deviceInfo = result.getData().stream().filter(r -> r.getUniqueToken().equals(s.getUniqueToken())).findFirst().orElse(null);
                    if (deviceInfo != null && deviceInfo.getOnline() != null) {
                        if (deviceInfo.getOnline()) {
                            s.setOnlineStatus(1);
                        } else {
                            s.setOnlineStatus(0);
                        }
                    }
                });

                tbSensorMapper.updateBatch(sensorWithDataSourceInfoList);
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public Object querySingleProjectMonitorPointNewDataPage(QuerySingleProjectMonitorPointNewDataPageParam pa) {
        // 先查询所有传感器,然后按监测点进行分组,按照条件(监测项目ID列表,监测点名称,收藏的监测点ID列表)
        List<SensorBaseInfoV4> sensorBaseInfoV4List = tbSensorMapper.selectListByCondition(pa.getProjectID(),
                pa.getMonitorItemIDList(),
                pa.getMonitorPointName());

        if (CollectionUtils.isEmpty(sensorBaseInfoV4List)) {
            return Collections.emptyList();
        }

        // 分组完之后进行遍历,每个监测点根据传感器去判断自己的监测点状态,然后按照条件(监测点状态进行筛选)
        Map<Integer, List<SensorBaseInfoV4>> monitorPointInfoList = sensorBaseInfoV4List.stream()
                .collect(Collectors.groupingBy(SensorBaseInfoV4::getMonitorPointID));

        // 存储结果的列表
        List<SensorBaseInfoV4> resultList = new ArrayList<>();

        // 遍历分组后的数据
        List<SensorBaseInfoV4> finalResultList = resultList;
        monitorPointInfoList.forEach((monitorPointID, sensors) -> {

            sensors.forEach(sensor -> {
                // 监测点是否收藏
                if (pa.getMonitorPointIDList().contains(sensor.getMonitorPointID())) {
                    sensor.setMonitorPointCollection(true);
                } else {
                    sensor.setMonitorPointCollection(false);
                }
                if (sensor.getDeviceOnlineStatus() == null) {
                    // 为null默认离线
                    sensor.setDeviceOnlineStatus(0);
                }
            });

            // 只取一个传感器,取的规则按照当前点的传感器最高预警的一个即可,数字越小,级别越高
            SensorBaseInfoV4 minStatusSensor = sensors.stream()
                    .filter(sensor -> sensor.getDataWarnStatus() > 0)
                    .min(Comparator.comparingInt(SensorBaseInfoV4::getDataWarnStatus))
                    .orElse(null);

            // 将找到的传感器添加到结果列表中
            if (minStatusSensor != null) {
                finalResultList.add(minStatusSensor);
            } else {
                SensorBaseInfoV4 maxStatusSensor = sensors.stream()
                        .max(Comparator.comparingInt(SensorBaseInfoV4::getDataWarnStatus))
                        .orElse(null);
                finalResultList.add(maxStatusSensor);
            }
        });

        if (pa.getMonitorPointCollection() != null && pa.getMonitorPointCollection()) {
            resultList = resultList.stream().filter(r -> r.getMonitorPointCollection().equals(pa.getMonitorPointCollection())).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(pa.getMonitorStatusList())) {
            resultList = resultList.stream().filter(r -> pa.getMonitorStatusList().contains(r.getDataWarnStatus())).collect(Collectors.toList());
        }

        // 最后根据监测类型再去分组这些监测点,然后遍历监测类型,去查该类型下监测点的传感器的最新数据,封装打包
        Map<Integer, List<SensorBaseInfoV4>> monitorTypeList = resultList.stream()
                .collect(Collectors.groupingBy(SensorBaseInfoV4::getMonitorType));

        monitorTypeList.forEach((monitorType, sensors) -> {

            List<FieldBaseInfo> monitorTypeFields = tbMonitorTypeFieldMapper.selectListByType(monitorType);

            List<FieldSelectInfo> fieldList = getFieldSelectInfoListFromModleTypeFieldList(monitorTypeFields);
            List<Integer> sensorIDList = sensors.stream().map(SensorBaseInfoV4::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> maps = sensorDataDao.querySensorNewData(sensorIDList, fieldList, false, monitorType);

            sensors.forEach(s -> {
                s.setMonitorTypeFields(monitorTypeFields);
                if (CollectionUtils.isNotEmpty(maps)) {
                    s.setSensorData(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID())).findFirst().orElse(null));
                    if (ObjectUtil.isNotNull(s.getSensorData())) {
                        s.setDataTime(DateUtil.parse((String) s.getSensorData().get(DbConstant.TIME_FIELD)));
                    }
                }
            });

        });

        // 按照时间倒序排序
        resultList.sort(Comparator.comparing(SensorBaseInfoV4::getDataTime, Comparator.nullsLast(Comparator.reverseOrder())));

        // 列表分页
        return PageUtil.page(resultList, pa.getPageSize(), pa.getCurrentPage());
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



    /**
     * @param list 监测点子类型字段列表
     * @return 统一格式的子类型字段列表
     */
    public List<FieldSelectInfo> getFieldSelectInfoListFromModleTypeFieldList(List<FieldBaseInfo> list) {
        List<FieldSelectInfo> fieldSelectInfos = new ArrayList<>();
        list.forEach(modelField -> {
            if (modelField.getFieldClass().equals(FieldClass.EXTEND_CONFIG.getCode())) {
                return;
            }
            FieldSelectInfo fieldSelectInfo = new FieldSelectInfo();
            fieldSelectInfo.setFieldToken(modelField.getFieldToken());
            fieldSelectInfo.setFieldName(modelField.getFieldName());
//            fieldSelectInfo.setFieldOrder(modelField.getDisplayOrder());
//            fieldSelectInfo.setFieldType(FieldType.valueOfString(modelField.getFieldDataType()));
//            fieldSelectInfo.setFieldStatisticsType(modelField.getFieldClass().toString());
//            fieldSelectInfo.setFieldJsonPath(null);
//            fieldSelectInfo.setFieldExValue(modelField.getFieldUnitID().toString());
            fieldSelectInfos.add(fieldSelectInfo);
        });
        return fieldSelectInfos;
    }
}
