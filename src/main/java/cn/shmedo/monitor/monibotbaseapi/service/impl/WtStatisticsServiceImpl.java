package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.DeviceOnlineStats;
import cn.shmedo.monitor.monibotbaseapi.model.cache.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorWithIot;
import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.WarnPointStats;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryDeviceOnlineStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsByProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.ReservoirNewSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceInfoByUniqueTokensParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryReservoirResponsibleListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryReservoirResponsibleListResponseItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoV4;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.service.WtStatisticsService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.service.third.wt.WtReportService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys.DEVICE_ONLINE_STATS;
import static cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys.WARN_POINT_STATS;
import static cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse.Item;
import static cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse.MonitorType;

/**
 * @author Chengfs on 2024/1/25
 */
@Service
@RequiredArgsConstructor
public class WtStatisticsServiceImpl implements WtStatisticsService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RedisService monitorRedisService;
    private final TbSensorMapper sensorMapper;
    private final TbMonitorTypeMapper monitorTypeMapper;
    private final TbWarnBaseConfigMapper warnBaseConfigMapper;
    private final TbProjectInfoMapper projectInfoMapper;
    private final TbProjectPropertyMapper projectPropertyMapper;
    private final IotService iotService;
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;
    private final SensorDataDao sensorDataDao;
    private static final String STR_RESERVOIR_SCALE = "水库规模";
    private static final String STR_RESERVOIR_SCALE_ONE = "小(Ⅰ)型";
    private static final String STR_RESERVOIR_SCALE_TWO = "小(Ⅱ)型";
    private static final String STR_RESERVOIR_SCALE_THREE = "中型";
    private static final String STR_RESERVOIR_SCALE_FOUR = "大(Ⅰ)型";
    private static final String STR_RESERVOIR_SCALE_FIVE = "大(Ⅱ)型";


    private static final String STR_RESERVOIR_PROPERTY_NAME_CHECKFLOODWATER = "校核洪水位";
    private static final String STR_RESERVOIR_PROPERTY_NAME_DESIGNFLOODWATER = "设计洪水位";
    private static final String STR_RESERVOIR_PROPERTY_NAME_NORMALSTORAGEWATER = "正常蓄水位";
    private static final String STR_RESERVOIR_PROPERTY_NAME_PERIODLIMITWATER = "期限制水位";
    private static final String STR_RESERVOIR_PROPERTY_NAME_TOTALCAPACITY = "总库容";

    private static final String STR_RESERVOIR_PROPERTY_NAME_MANAGEUNIT = "主管部门";
    private static final String STR_RESERVOIR_PROPERTY_NAME_CONTACTSPHONE = "主管部门联系电话";

    private static final Integer STR_RESERVOIR_RESPONSIBLE_TYPE_ADMINISTRATIONDIRECTOR = 21;
    private static final Integer STR_RESERVOIR_RESPONSIBLE_TYPE_MAINMANAGEMENTDIRECTOR = 12;
    private static final Integer STR_RESERVOIR_RESPONSIBLE_TYPE_MANAGEMENTDIRECTOR = 13;
    private static final Integer STR_RESERVOIR_RESPONSIBLE_TYPE_PATROLDIRECTOR = 23;
    private static final Integer STR_RESERVOIR_RESPONSIBLE_TYPE_ECHNICALDIRECTOR = 22;
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final WtReportService wtReportService;

    @Override
    public ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param) {
        Map<String, Object> dict = getWarnDict(param.getCompanyID(), param.getPlatform());
        if (param.getProjects().isEmpty()) {
            return new ReservoirWarnStatsResponse(dict, Item.empty(), List.of());
        }

        List<WarnPointStats> data = param.getProjects().stream()
                .flatMap(e -> monitorRedisService.getAll(WARN_POINT_STATS + e, WarnPointStats.class)
                        .values().stream()).toList();
        if (data.isEmpty()) {
            return new ReservoirWarnStatsResponse(dict, Item.empty(), List.of());
        }

        Item overview = Item.from(data);
        Map<Integer, String> monitorTypeMap = monitorTypeMapper.selectList(Wrappers.<TbMonitorType>lambdaQuery()
                        .in(TbMonitorType::getMonitorType, data.stream().map(WarnPointStats::getMonitorType).distinct().toList())
                        .select(TbMonitorType::getMonitorType, TbMonitorType::getTypeName)).stream()
                .collect(Collectors.toMap(TbMonitorType::getMonitorType, TbMonitorType::getTypeName));

        List<MonitorType> monitorType = data.stream().collect(Collectors.groupingBy(WarnPointStats::getMonitorType))
                .entrySet().stream().map(item -> {
                    String typeName = monitorTypeMap.get(item.getKey());
                    return new MonitorType(item.getKey(), typeName, Item.from(item.getValue()));
                }).toList();

        return new ReservoirWarnStatsResponse(dict, overview, monitorType);
    }

    @Override
    public void cacheWarnStats() {
        Map<String, Map<Integer, WarnPointStats>> cacheMap = sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                        .isNotNull(TbSensor::getMonitorPointID)
                        .select(TbSensor::getProjectID, TbSensor::getMonitorType, TbSensor::getMonitorPointID,
                                TbSensor::getStatus, TbSensor::getOnlineStatus))
                .stream().collect(Collectors.groupingBy(TbSensor::getProjectID))
                .entrySet().stream().flatMap(entry -> {
                    //项目 监测类型 监测点
                    return entry.getValue().stream()
                            .collect(Collectors.groupingBy(TbSensor::getMonitorType))
                            .entrySet().stream().map(typeGroup -> {
                                Integer level1 = 0, level2 = 0, level3 = 0, level4 = 0, offline = 0;
                                for (List<TbSensor> item : typeGroup.getValue().stream()
                                        .collect(Collectors.groupingBy(TbSensor::getMonitorPointID)).values()) {
                                    //数据报警等级
                                    Integer level = item.stream()
                                            .map(e -> e.getStatus().intValue())
                                            .filter(e -> e > 0).min(Comparator.comparingInt(i -> i)).orElse(0);
                                    switch (level) {
                                        case 1 -> level1++;
                                        case 2 -> level2++;
                                        case 3 -> level3++;
                                        case 4 -> level4++;
                                    }

                                    //在线状态
                                    boolean isOnline = item.stream()
                                            .allMatch(e -> e.getOnlineStatus() != null && e.getOnlineStatus() == 1);
                                    if (!isOnline) {
                                        offline++;
                                    }
                                }
                                WarnPointStats cacheObj = new WarnPointStats(entry.getKey(), level1, level2, level3, level4,
                                        offline, typeGroup.getKey());
                                return Tuples.of(entry.getKey(), typeGroup.getKey(), cacheObj);
                            });
                }).collect(Collectors.groupingBy(t -> WARN_POINT_STATS + t.getT1(), Collectors.toMap(Tuple3::getT2, Tuple3::getT3)));

        monitorRedisService.transaction(operations -> {
            operations.delete(cacheMap.keySet());
            cacheMap.forEach((k, v) -> operations.opsForHash().putAll(k, RedisService.serializeMap(v)));
        });
        cacheMap.clear();
    }

    @Override
    public DeviceOnlineStatsResponse queryDeviceOnlineStats(QueryDeviceOnlineStatsParam param) {
        List<DeviceOnlineStats> data = param.getProjects().stream()
                .flatMap(e -> monitorRedisService.getAll(DEVICE_ONLINE_STATS + e, DeviceOnlineStats.class)
                        .values().stream())
                .filter(e -> e != null && e.getMonitorType() != null).toList();

        DeviceOnlineStatsResponse result = new DeviceOnlineStatsResponse();
        result.setCount(data.stream().mapToLong(DeviceOnlineStats::getCount).sum());
        result.setOnline(data.stream().mapToLong(DeviceOnlineStats::getOnline).sum());
        result.setOffline(data.stream().mapToLong(DeviceOnlineStats::getOffline).sum());

        Map<Integer, String> monitorTypeMap;
        if (!data.isEmpty()) {
            monitorTypeMap = monitorTypeMapper.selectList(Wrappers.<TbMonitorType>lambdaQuery()
                            .in(TbMonitorType::getMonitorType, data.stream().map(DeviceOnlineStats::getMonitorType).distinct().toList())
                            .select(TbMonitorType::getMonitorType, TbMonitorType::getTypeName)).stream()
                    .collect(Collectors.toMap(TbMonitorType::getMonitorType, TbMonitorType::getTypeName));
        } else {
            monitorTypeMap = Map.of();
        }

        List<DeviceOnlineStatsResponse.MonitorType> monitorType = data.stream()
                .collect(Collectors.groupingBy(DeviceOnlineStats::getMonitorType))
                .entrySet().stream().map(item -> {
                    String typeName = monitorTypeMap.get(item.getKey());
                    return new DeviceOnlineStatsResponse.MonitorType(item.getKey(), typeName,
                            item.getValue().stream().mapToLong(DeviceOnlineStats::getCount).sum(),
                            item.getValue().stream().mapToLong(DeviceOnlineStats::getOnline).sum(),
                            item.getValue().stream().mapToLong(DeviceOnlineStats::getOffline).sum());
                }).toList();
        result.setMonitorType(monitorType);
        return result;
    }

    @Override
    public void cacheDeviceOnlineStats() {
        List<SensorWithIot> list = sensorMapper.listSensorWithIot();
        Map<String, Boolean> onlineMap;
        if (list.isEmpty()) {
            onlineMap = Map.of();
        } else {
            List<String> uniqueTokens = list.stream().map(SensorWithIot::getIotUniqueToken).distinct().toList();
            onlineMap = Optional.ofNullable(iotService
                            .queryDeviceInfoByUniqueTokens(new QueryDeviceInfoByUniqueTokensParam(uniqueTokens)).getData())
                    .orElse(List.of())
                    .stream().collect(Collectors.toMap(DeviceInfo::getUniqueToken, DeviceInfo::getOnline));
        }

        Map<String, Map<Integer, DeviceOnlineStats>> cacheMap = list.stream()
                .collect(Collectors.groupingBy(SensorWithIot::getProjectID))
                .entrySet().stream().flatMap(entry -> {
                    //项目 监测类型  设备
                    return entry.getValue().stream()
                            .collect(Collectors.groupingBy(SensorWithIot::getMonitorType))
                            .entrySet().stream().map(typeGroup -> {
                                Map<Boolean, Long> collect = typeGroup.getValue().stream()
                                        .map(SensorWithIot::getIotUniqueToken)
                                        .map(e -> onlineMap.getOrDefault(e, Boolean.FALSE))
                                        .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

                                DeviceOnlineStats cacheObj = new DeviceOnlineStats(entry.getKey(),
                                        collect.values().stream().mapToLong(e -> e).count(),
                                        collect.getOrDefault(Boolean.TRUE, 0L),
                                        collect.getOrDefault(Boolean.FALSE, 0L),
                                        typeGroup.getKey());
                                return Tuples.of(entry.getKey(), typeGroup.getKey(), cacheObj);
                            });
                }).collect(Collectors.groupingBy(t -> DEVICE_ONLINE_STATS + t.getT1(), Collectors.toMap(Tuple3::getT2, Tuple3::getT3)));

        monitorRedisService.transaction(operations -> {
            operations.delete(cacheMap.keySet());
            cacheMap.forEach((k, v) -> operations.opsForHash().putAll(k, RedisService.serializeMap(v)));
        });
        cacheMap.clear();
    }

    @Override
    public ReservoirNewSensorDataResponse queryReservoirNewSensorData(ReservoirNewSensorDataParam pa) {

        TbProjectInfo tbProjectInfo = projectInfoMapper.selectOne(Wrappers.<TbProjectInfo>lambdaQuery()
                .eq(TbProjectInfo::getID, pa.getProjectID()));
        if (!ObjectUtil.isNotNull(tbProjectInfo)) {
            return null;
        }

        List<TbProjectProperty> tbProjectProperties = projectPropertyMapper.selectList(Wrappers.<TbProjectProperty>lambdaQuery()
                .eq(TbProjectProperty::getSubjectType, 0)
                .eq(TbProjectProperty::getProjectID, pa.getProjectID())
        );


        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        List<SensorBaseInfoV4> sensorBaseInfoV4List = sensorMapper.selectListByCondition(pa.getProjectID(), null, null);
        if (!CollectionUtil.isNullOrEmpty(sensorBaseInfoV4List)) {

            // 查询监测类型为2:水位, 31:降雨量的监测点
            List<SensorBaseInfoV4> includeMonitorTypeSensorList = sensorBaseInfoV4List.stream().filter(s -> s.getMonitorType().equals(2) || s.getMonitorType().equals(31)).collect(Collectors.toList());
            if (!CollectionUtil.isNullOrEmpty(includeMonitorTypeSensorList)) {
                // 根据监测类型再去分类,然后查询最新时间的数据
                // 最后根据监测类型再去分组这些监测点,然后遍历监测类型,去查该类型下监测点的传感器的最新数据,封装打包
                Map<Integer, List<SensorBaseInfoV4>> monitorTypeList = includeMonitorTypeSensorList.stream()
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
                            resultList.add(s.getSensorData());
                        }
                    });

                });

            }
        }

        if (StringUtils.isNotBlank(tbProjectInfo.getLocation())) {
            if (JSONUtil.isTypeJSON(tbProjectInfo.getLocation())) {
                JSONObject json = JSONUtil.parseObj(tbProjectInfo.getLocation());
                tbProjectInfo.setLocation(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
            }
        }
        Collection<Object> areas = List.of(tbProjectInfo.getLocation());
        Map<String, String> areaMap = monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));

        return ReservoirNewSensorDataResponse.toNewVo(tbProjectInfo,
                tbProjectProperties, resultList, areaMap.get(tbProjectInfo.getLocation()));
    }

    @Override
    public ReservoirWarnStatsByProjectResponse queryWarnStatsByProject(QueryReservoirWarnStatsByProjectParam param) {

        Map<String, Object> dict = getWarnDict(param.getCompanyID(), param.getPlatform());
        if (param.getProjects().isEmpty()) {
            return new ReservoirWarnStatsByProjectResponse(dict, ReservoirWarnStatsByProjectResponse.Item.empty(), List.of());
        }

        Map<Integer, List<WarnPointStats>> dataMap = param.getProjects().stream()
                .flatMap(e -> monitorRedisService.getAll(WARN_POINT_STATS + e, WarnPointStats.class)
                        .values().stream()).toList()
                .stream().collect(Collectors.groupingBy(WarnPointStats::getProjectID));

        Map<Integer, String> projectMap = monitorRedisService.multiGet(RedisKeys.PROJECT_KEY,
                        param.getProjects().stream().map(Object::toString).collect(Collectors.toList()), ProjectInfoCache.class)
                .stream().collect(Collectors.toMap(TbProjectInfo::getID, TbProjectInfo::getProjectName));

        ReservoirWarnStatsByProjectResponse.Item overview = ReservoirWarnStatsByProjectResponse.Item
                .from(dataMap.values().stream().flatMap(Collection::stream).toList());
        List<ReservoirWarnStatsByProjectResponse.Project> projects = projectMap.entrySet().stream()
                .map(project -> {
                    String projectName = project.getValue();
                    List<WarnPointStats> list = dataMap.getOrDefault(project.getKey(), List.of());
                    return new ReservoirWarnStatsByProjectResponse.Project(project.getKey(), projectName,
                            ReservoirWarnStatsByProjectResponse.Item.from(list));
                }).toList();
        return new ReservoirWarnStatsByProjectResponse(dict, overview, projects);
    }

    @Override
    public ReservoirProjectStatisticsResult reservoirProjectStatistics(Integer companyID) {
        List<TbProjectInfo> tbProjectInfos = projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, companyID)
                        .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
        );
        List<PropertyDto> propertyDtos = projectPropertyMapper.queryPropertyByProjectID(
                tbProjectInfos.stream().map(TbProjectInfo::getID).toList(), null,
                PropertySubjectType.Project.getType()
        );
        Map<Integer, String> map = propertyDtos.stream()
                .filter(e -> e.getName().equals(STR_RESERVOIR_SCALE))
                .collect(Collectors.toMap(PropertyDto::getProjectID, PropertyDto::getValue));
        Map<String, List<TbProjectInfo>> locationMap = tbProjectInfos.stream().collect(Collectors.groupingBy(
                e -> {
                    if (JSONUtil.isTypeJSON(e.getLocation())) {
                        JSONObject json = JSONUtil.parseObj(e.getLocation());
                        return json.isEmpty() ? null : CollUtil.getLast(json.values()).toString();
                    } else {
                        return null;
                    }
                }
        ));
        Map<String, RegionArea> areaMap = monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY,
                        locationMap.keySet().stream().filter(Objects::nonNull).collect(Collectors.toSet()), RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), Function.identity()));
        ReservoirProjectStatisticsResult result = ReservoirProjectStatisticsResult.builder().reservoirCount(tbProjectInfos.size())
                .build();
        result.setTypeOneCount((int) map.values().stream().filter(e -> e.equals(STR_RESERVOIR_SCALE_ONE)).count());
        result.setTypeTwoCount((int) map.values().stream().filter(e -> e.equals(STR_RESERVOIR_SCALE_TWO)).count());
        result.setTypeThreeCount((int) map.values().stream().filter(e -> e.equals(STR_RESERVOIR_SCALE_THREE)).count());
        result.setTypeFourCount((int) map.values().stream().filter(e -> e.equals(STR_RESERVOIR_SCALE_FOUR)).count());
        result.setTypeFiveCount((int) map.values().stream().filter(e -> e.equals(STR_RESERVOIR_SCALE_FIVE)).count());
        result.setAreaStatisticsList(
                areaMap.entrySet().stream().map(e ->
                        {
                            ReservoirProjectStatisticsResult.AreaStatItem obj = ReservoirProjectStatisticsResult.AreaStatItem.builder()
                                    .areaCode(e.getKey())
                                    .areaName(e.getValue().getName())
                                    .build();
                            obj.setReservoirCount(locationMap.getOrDefault(e.getKey(), List.of()).size());
                            obj.setTypeOneCount((int) locationMap.getOrDefault(e.getKey(), List.of()).stream()
                                    .map(ee -> map.get(ee.getID())).filter(Objects::nonNull).filter(i -> i.equals(STR_RESERVOIR_SCALE_ONE)).count());
                            obj.setTypeTwoCount((int) locationMap.getOrDefault(e.getKey(), List.of()).stream()
                                    .map(ee -> map.get(ee.getID())).filter(Objects::nonNull).filter(i -> i.equals(STR_RESERVOIR_SCALE_TWO)).count());
                            obj.setTypeThreeCount((int) locationMap.getOrDefault(e.getKey(), List.of()).stream()
                                    .map(ee -> map.get(ee.getID())).filter(Objects::nonNull).filter(i -> i.equals(STR_RESERVOIR_SCALE_THREE)).count());
                            obj.setTypeFourCount((int) locationMap.getOrDefault(e.getKey(), List.of()).stream()
                                    .map(ee -> map.get(ee.getID())).filter(Objects::nonNull).filter(i -> i.equals(STR_RESERVOIR_SCALE_FOUR)).count());
                            obj.setTypeFiveCount((int) locationMap.getOrDefault(e.getKey(), List.of()).stream()
                                    .map(ee -> map.get(ee.getID())).filter(Objects::nonNull).filter(i -> i.equals(STR_RESERVOIR_SCALE_FIVE)).count());
                            return obj;
                        }
                ).toList()
        );
        return result;
    }

    @Override
    public ReservoirMonitorStatisticsResult reservoirMonitorStatistics(Integer companyID) {
        List<Integer> pIDList = projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, companyID)
                        .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
        ).stream().map(TbProjectInfo::getID).toList();
        Map<Integer, Long> typeCoutMap = tbMonitorPointMapper.selectList(
                new LambdaQueryWrapper<TbMonitorPoint>()
                        .in(TbMonitorPoint::getProjectID, pIDList)
        ).stream().collect(Collectors.groupingBy(
                TbMonitorPoint::getMonitorType,
                Collectors.counting()
        ));
        Map<Integer, String> typeANameMap = monitorTypeMapper.selectList(
                new LambdaQueryWrapper<TbMonitorType>()
                        .in(TbMonitorType::getMonitorType, typeCoutMap.keySet())
        ).stream().collect(Collectors.toMap(TbMonitorType::getMonitorType, TbMonitorType::getTypeName));
        ReservoirMonitorStatisticsResult result = ReservoirMonitorStatisticsResult.builder()
                .monitorPointCount((int) typeCoutMap.values().stream().mapToLong(e -> e).sum())
                .build();
        result.setMonitorTypeStatisticsList(
                typeCoutMap.entrySet().stream().map(e -> ReservoirMonitorStatisticsResult.TypeStatItem.builder()
                        .monitorType(e.getKey())
                        .monitorTypeName(typeANameMap.get(e.getKey()))
                        .count(e.getValue().intValue())
                        .build()).toList());
        return result;
    }

    @Override
    public ReservoirDetail reservoirProjectDetail(TbProjectInfo tbProjectInfo) {
        ReservoirDetail result = ReservoirDetail.builder()
                .projectID(tbProjectInfo.getID())
                .projectName(tbProjectInfo.getProjectName())
                .shortName(tbProjectInfo.getShortName())
                .build();
        // 基础属性
        List<PropertyDto> propertyDtos = projectPropertyMapper.queryPropertyByProjectID(
                List.of(tbProjectInfo.getID()), null,
                PropertySubjectType.Project.getType()
        ).stream().filter(e -> ObjectUtil.isNotEmpty(e.getValue())).toList();
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_SCALE)).findFirst().ifPresent(
                e -> result.setReservoirScale(e.getValue())
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_CHECKFLOODWATER)).findFirst().ifPresent(
                e -> result.setCheckFloodWater(Double.valueOf(e.getValue()))
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_DESIGNFLOODWATER)).findFirst().ifPresent(
                e -> result.setCheckFloodWater(Double.valueOf(e.getValue()))
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_NORMALSTORAGEWATER)).findFirst().ifPresent(
                e -> result.setNormalStorageWater(Double.valueOf(e.getValue()))
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_PERIODLIMITWATER)).findFirst().ifPresent(
                e -> result.setPeriodLimitWater(Double.valueOf(e.getValue()))
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_TOTALCAPACITY)).findFirst().ifPresent(
                e -> result.setTotalCapacity(Double.valueOf(e.getValue()))
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_MANAGEUNIT)).findFirst().ifPresent(
                e -> result.setManageUnit(e.getValue())
        );
        propertyDtos.stream().filter(e -> e.getName().equals(STR_RESERVOIR_PROPERTY_NAME_CONTACTSPHONE)).findFirst().ifPresent(
                e -> result.setContactsPhone(e.getValue())
        );
        // 远程调用获取别的属性
        ResultWrapper<List<QueryReservoirResponsibleListResponseItem>> wrapper = wtReportService.queryReservoirResponsibleList(
                QueryReservoirResponsibleListRequest.builder()
                        .companyID(tbProjectInfo.getCompanyID())
                        .projectID(tbProjectInfo.getID())
                        .build()
        );
        if (!wrapper.apiSuccess()) {
            throw new CustomBaseException(wrapper.getCode(), wrapper.getMsg());
        }
        if (ObjectUtil.isNotEmpty(wrapper.getData())) {
            wrapper.getData().stream().filter(e -> e.getType().equals(STR_RESERVOIR_RESPONSIBLE_TYPE_ADMINISTRATIONDIRECTOR)).findFirst()
                    .ifPresent(e -> result.setAdministrationDirector(e.getUser()));
            wrapper.getData().stream().filter(e -> e.getType().equals(STR_RESERVOIR_RESPONSIBLE_TYPE_MAINMANAGEMENTDIRECTOR)).findFirst()
                    .ifPresent(e -> result.setMainManagementDirector(e.getUser()));
            wrapper.getData().stream().filter(e -> e.getType().equals(STR_RESERVOIR_RESPONSIBLE_TYPE_MANAGEMENTDIRECTOR)).findFirst()
                    .ifPresent(e -> result.setManagementDirector(e.getUser()));
            wrapper.getData().stream().filter(e -> e.getType().equals(STR_RESERVOIR_RESPONSIBLE_TYPE_PATROLDIRECTOR)).findFirst()
                    .ifPresent(e -> result.setPatrolDirector(e.getUser()));
            wrapper.getData().stream().filter(e -> e.getType().equals(STR_RESERVOIR_RESPONSIBLE_TYPE_ECHNICALDIRECTOR)).findFirst()
                    .ifPresent(e -> result.setTechnicalDirector(e.getUser()));
        }
        return result;
    }

    @Override
    public List<PointWithProjectInfo> reservoirVideoMonitorPoint(Integer companyID, TbProjectInfo tbProjectInfo) {
        Map<Integer, TbProjectInfo> projectIDANameMap;
        if (tbProjectInfo != null) {
            projectIDANameMap = Map.of(tbProjectInfo.getID(), tbProjectInfo);
        } else {
            projectIDANameMap = projectInfoMapper.selectList(
                    new LambdaQueryWrapper<TbProjectInfo>()
                            .eq(TbProjectInfo::getCompanyID, companyID)
                            .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
            ).stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        }
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectList(
                new LambdaQueryWrapper<TbMonitorPoint>()
                        .in(TbMonitorPoint::getProjectID, projectIDANameMap.keySet())
                        .eq(TbMonitorPoint::getMonitorType, cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType.VIDEO.getKey())
        );
        return tbMonitorPoints.stream().map(e ->
                PointWithProjectInfo.builder()
                        .projectID(e.getProjectID())
                        .projectName(projectIDANameMap.get(e.getProjectID()).getProjectName())
                        .shortName(projectIDANameMap.get(e.getProjectID()).getShortName())
                        .monitorPointID(e.getID())
                        .monitorPointName(e.getName())
                        .build()
        ).sorted(Comparator.comparingInt(PointWithProjectInfo::getMonitorPointID).reversed()).toList();
    }

    @Override
    public ReservoirDeviceStatisticsResult reservoirDeviceStatistics(Integer companyID, Integer projectID) {
        List<Integer> pidList;
        if (projectID != null) {
            pidList = List.of(projectID);
        } else {
            pidList = projectInfoMapper.selectList(
                    new LambdaQueryWrapper<TbProjectInfo>()
                            .eq(TbProjectInfo::getCompanyID, companyID)
                            .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
            ).stream().map(TbProjectInfo::getID).toList();
        }
        ReservoirDeviceStatisticsResult result = new ReservoirDeviceStatisticsResult();
        result.setVideoDeviceCount(
                tbMonitorPointMapper.selectVideoPointListByCondition(pidList,
                        null, null, null, null, null, cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType.VIDEO.getKey(),
                        null, null).size()
        );
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(companyID)
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(pidList.stream().map(String::valueOf).toList())
                .sendEnable(true)
                .deviceToken(null)
                .online(null)
                .productID(null)
                .build();
        ResultWrapper<List<SimpleDeviceV5>> resultWrapper = iotService.queryDeviceSimpleBySenderAddress(request);
        if (!resultWrapper.apiSuccess()) {
            throw new CustomBaseException(resultWrapper.getCode(), resultWrapper.getMsg());
        }
        result.setIotDeviceCount(
                ObjectUtil.isEmpty(resultWrapper.getData()) ? 0 : resultWrapper.getData().size()
        );
        return result;
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
            fieldSelectInfos.add(fieldSelectInfo);
        });
        return fieldSelectInfos;
    }

    protected Map<String, Object> getWarnDict(Integer companyID, Integer platform) {
        WarnTag warnTag = WarnTag.TYPE1;
        WarnLevelStyle style = WarnLevelStyle.COLOR;
        int levelNum = 4;
        if (platform != null) {
            TbWarnBaseConfig config = warnBaseConfigMapper.selectOne(Wrappers.<TbWarnBaseConfig>lambdaQuery()
                    .eq(TbWarnBaseConfig::getPlatform, platform)
                    .eq(TbWarnBaseConfig::getCompanyID, companyID));

            if (config != null) {
                warnTag = WarnTag.fromCode(config.getWarnTag());
                style = WarnLevelStyle.fromCode(config.getWarnLevelStyle());
                levelNum = config.getWarnLevelType() == 1 ? 4 : 3;
            }
        }
        String[] split = style.getDesc().split(StrUtil.COMMA);
        final String tag = warnTag.getDesc();
        Map<String, Object> dict = new HashMap<>(4);
        dict.put("offline", "离线" + tag);
        IntStream.range(0, levelNum).forEach(i -> dict.put("level" + (i + 1), split[i] + tag));
        return dict;
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            cacheDeviceOnlineStats();
        };
    }
}