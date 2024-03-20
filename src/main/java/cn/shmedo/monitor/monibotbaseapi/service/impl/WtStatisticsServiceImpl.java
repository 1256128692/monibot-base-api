package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.DeviceOnlineStats;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.cache.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorWithIot;
import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.CacheIntelDeviceStatItem;
import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.CacheReservoirDetail;
import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.CacheTypePointStatItem;
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

import static cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys.*;
import static cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse.Item;
import static cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse.MonitorTypeRecord;

/**
 * @author Chengfs on 2024/1/25
 */
@Service
@RequiredArgsConstructor
public class WtStatisticsServiceImpl implements WtStatisticsService {

    private static final String STR_RESERVOIR_SCALE = "工程规模";
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
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final TbVideoDeviceMapper tbVideoDeviceMapper;
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

        List<MonitorTypeRecord> monitorType = data.stream().collect(Collectors.groupingBy(WarnPointStats::getMonitorType))
                .entrySet().stream().map(item -> {
                    String typeName = monitorTypeMap.get(item.getKey());
                    return new MonitorTypeRecord(item.getKey(), typeName, Item.from(item.getValue()));
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
                    .stream().filter(e -> e.getUniqueToken() != null && e.getOnline() != null)
                    .collect(Collectors.toMap(DeviceInfo::getUniqueToken, DeviceInfo::getOnline));
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
    public List<ReservoirNewSensorDataResponse> queryReservoirNewSensorData(ReservoirNewSensorDataParam pa) {

        List<TbProjectInfo> tbProjectInfoList = projectInfoMapper.selectList(Wrappers.<TbProjectInfo>lambdaQuery()
                .in(TbProjectInfo::getID, pa.getProjectIDList()));
        if (CollectionUtil.isNullOrEmpty(tbProjectInfoList)) {
            return Collections.emptyList();
        }

        List<TbProjectProperty> tbProjectProperties = projectPropertyMapper.selectList(Wrappers.<TbProjectProperty>lambdaQuery()
                .eq(TbProjectProperty::getSubjectType, 0)
                .in(TbProjectProperty::getProjectID, pa.getProjectIDList())
        );


        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        List<SensorBaseInfoV4> sensorBaseInfoV4List = sensorMapper.selectListByProjectIDList(pa.getProjectIDList());
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
                                s.getSensorData().put("projectID", s.getProjectID());
                                s.setDataTime(DateUtil.parse((String) s.getSensorData().get(DbConstant.TIME_FIELD)));
                            }
                            resultList.add(s.getSensorData());
                        }
                    });

                });

            }
        }

        List<ReservoirNewSensorDataResponse> proList = new LinkedList<>();

        tbProjectInfoList.forEach(projectInfo -> {
            if (StringUtils.isNotBlank(projectInfo.getLocation())) {
                if (JSONUtil.isTypeJSON(projectInfo.getLocation())) {
                    JSONObject json = JSONUtil.parseObj(projectInfo.getLocation());
                    projectInfo.setLocation(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
                }
            }
            Collection<Object> areas = List.of(projectInfo.getLocation());
            Map<String, String> areaMap = monitorRedisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                    .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));

            proList.add(ReservoirNewSensorDataResponse.toNewVo(projectInfo,
                    tbProjectProperties.stream().filter(o -> Integer.valueOf(Math.toIntExact(o.getProjectID())).equals(projectInfo.getID())).collect(Collectors.toList()),
                    resultList.stream().filter(Objects::nonNull)
                            .filter(r -> Integer.valueOf(r.get("projectID").toString()).equals(projectInfo.getID())).collect(Collectors.toList()),
                    areaMap.get(projectInfo.getLocation())));
        });

        return proList;
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
        List<ReservoirWarnStatsByProjectResponse.Project> projects = projectMap.entrySet().stream().map(project -> {
                    String projectName = project.getValue();
                    List<WarnPointStats> list = dataMap.getOrDefault(project.getKey(), List.of());
                    return new ReservoirWarnStatsByProjectResponse.Project(project.getKey(), projectName,
                            ReservoirWarnStatsByProjectResponse.Item.from(list));
                })
                .sorted(Comparator.comparingInt(ReservoirWarnStatsByProjectResponse.Project::warnCount).reversed())
                .limit(5)
                .toList();
        return new ReservoirWarnStatsByProjectResponse(dict, overview, projects);
    }

    @Override
    public ReservoirProjectStatisticsResult reservoirProjectStatistics(Integer companyID, Collection<Integer> havePermissionProjectList) {
        if (ObjectUtil.isEmpty(havePermissionProjectList)) {
            return ReservoirProjectStatisticsResult.builder().build();
        }
        List<TbProjectInfo> tbProjectInfos = projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, companyID)
                        .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
                        .in(TbProjectInfo::getID, havePermissionProjectList)
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
    public ReservoirMonitorStatisticsResult reservoirMonitorStatistics(Integer companyID, Collection<Integer> havePermissionProjectList) {
        if (ObjectUtil.isEmpty(havePermissionProjectList)) {
            return ReservoirMonitorStatisticsResult.builder().build();
        }

        List<Integer> pIDList = projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, companyID)
                        .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
                        .select(TbProjectInfo::getID)
        ).stream().map(TbProjectInfo::getID).toList();
        if (pIDList.isEmpty()) {
            return ReservoirMonitorStatisticsResult.builder().build();
        }
        Map<Integer, MonitorTypeCacheData> allType = monitorRedisService.getAll(MONITOR_TYPE_KEY,
                Integer.class, MonitorTypeCacheData.class);
        // 使用缓存
        List<ReservoirMonitorStatisticsResult.TypePointItem> itemList =
                monitorRedisService.getAll(TYPE_POINT_STATS, List.class)
                        .entrySet().stream().filter(e -> pIDList.contains(Integer.valueOf(e.getKey())))
                        .flatMap(e -> JSONUtil.toList(e.getValue().toString(), CacheTypePointStatItem.class).stream()).toList().stream()
                        .collect(Collectors.groupingBy(CacheTypePointStatItem::getMonitorType, Collectors.collectingAndThen(
                                Collectors.toList(), list -> list.stream().mapToInt(CacheTypePointStatItem::getCount).sum()
                        ))).entrySet().stream().map(
                                e -> ReservoirMonitorStatisticsResult.TypePointItem.builder()
                                        .monitorType(e.getKey())
                                        .typeName(allType.getOrDefault(e.getKey(), new MonitorTypeCacheData()).getTypeName())
                                        .count(e.getValue())
                                        .build()
                        ).toList();
        return ReservoirMonitorStatisticsResult.builder()
                .monitorTypeStatisticsList(itemList)
                .monitorPointCount(itemList.stream().mapToInt(ReservoirMonitorStatisticsResult.TypePointItem::getCount).sum())
                .build();
    }

    @Override
    public ReservoirDetail reservoirProjectDetail(TbProjectInfo tbProjectInfo) {
        CacheReservoirDetail cacheReservoirDetail = monitorRedisService.get(RESERVOIR_DETAIL, tbProjectInfo.getID().toString(), CacheReservoirDetail.class);
        return BeanUtil.copyProperties(cacheReservoirDetail, ReservoirDetail.class);
    }

    @Override
    public List<PointWithProjectInfo> reservoirVideoMonitorPoint(Integer companyID, Collection<Integer> havePermissionProjectList) {
        if (ObjectUtil.isEmpty(havePermissionProjectList)) {
            return List.of();
        }
        List<TbProjectInfo> projectInfoList = projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, companyID)
                        .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
                        .in(TbProjectInfo::getID, havePermissionProjectList)
                        .select(TbProjectInfo::getID, TbProjectInfo::getProjectName, TbProjectInfo::getShortName)
        );
        if (projectInfoList.isEmpty()) {
            return List.of();
        }
        List<Integer> pidList = projectInfoList.stream().map(TbProjectInfo::getID).toList();
        Map<Integer, TbProjectInfo> pMap = projectInfoList.stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        List<Integer> pointIDList = monitorRedisService.getAll(VIDEO_POINT_ID, Integer.class, List.class)
                .entrySet().stream().filter(e -> pidList.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(list -> list.stream()).map(e -> Integer.valueOf(e.toString())).toList();
        if (ObjectUtil.isEmpty(pointIDList)) {
            return List.of();
        }
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectList(
                new LambdaQueryWrapper<TbMonitorPoint>()
                        .in(TbMonitorPoint::getID, pointIDList)

        );
        return tbMonitorPoints.stream().map(e ->
                PointWithProjectInfo.builder()
                        .projectID(e.getProjectID())
                        .projectName(pMap.get(e.getProjectID()).getProjectName())
                        .shortName(pMap.get(e.getProjectID()).getShortName())
                        .monitorPointID(e.getID())
                        .monitorPointName(e.getName())
                        .build()
        ).sorted(Comparator.comparingInt(PointWithProjectInfo::getMonitorPointID).reversed()).toList();
    }

    @Override
    public ReservoirDeviceStatisticsResult reservoirDeviceStatistics(Integer companyID, Collection<Integer> havePermissionProjectList) {
        ReservoirDeviceStatisticsResult result = ReservoirDeviceStatisticsResult.builder()
                .iotDeviceCount(0)
                .videoDeviceCount(0)
                .build();
        if (ObjectUtil.isEmpty(havePermissionProjectList)) {
            return result;
        }
        List<Integer> pidList = new ArrayList<>(projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getCompanyID, companyID)
                        .in(TbProjectInfo::getID, havePermissionProjectList)
        ).stream().map(TbProjectInfo::getID).toList());

        String str = monitorRedisService.get(INTEL_DEVICE_STATS, companyID.toString());
        if (StrUtil.isBlank(str)) {
            return result;
        }
        JSONObject entries = JSONUtil.parseObj(str);
        entries.getByPath(Integer.toString(11), CacheIntelDeviceStatItem.class);
        pidList.add(-1);
        Set<String> iotTokenSet = new HashSet<>();
        pidList.forEach(
                pid -> {
                    CacheIntelDeviceStatItem item = entries.getByPath(pid.toString(), CacheIntelDeviceStatItem.class);
                    if (item != null) {
                        if (item.getIotDeviceTokens() != null) {
                            iotTokenSet.addAll(item.getIotDeviceTokens());
                        }
                        if (item.getVideoDeviceTokens() != null) {
                            result.setVideoDeviceCount(result.getVideoDeviceCount() + item.getVideoDeviceTokens().size());
                        }
                    }
                }
        );
        result.setIotDeviceCount(iotTokenSet.size());
        return result;
    }

    @Override
    public void cacheTypePointStatistics() {
        Map<String, List<CacheTypePointStatItem>> cacheMap = tbMonitorPointMapper.selectList(
                        Wrappers.<TbMonitorPoint>lambdaQuery()
                                .select(TbMonitorPoint::getProjectID, TbMonitorPoint::getMonitorType)
                ).stream().collect(Collectors.groupingBy(TbMonitorPoint::getProjectID))
                .entrySet().stream().map(entry -> {
                    List<CacheTypePointStatItem> collect = entry.getValue().stream().collect(Collectors.groupingBy(
                            TbMonitorPoint::getMonitorType,
                            Collectors.counting()
                    )).entrySet().stream().map(e -> CacheTypePointStatItem.builder()
                            .monitorType(e.getKey())
                            .count(e.getValue().intValue())
                            .build()
                    ).toList();
                    return new Tuple<>(entry.getKey(), collect);
                }).collect(Collectors.toMap(
                        e -> e.getItem1().toString(),
                        Tuple::getItem2
                ));
        monitorRedisService.putAll(TYPE_POINT_STATS, cacheMap);
    }

    @Override
    public void cacheVideoPointIDStatistics() {
        List<Integer> list = sensorMapper.selectList(
                Wrappers.<TbSensor>lambdaQuery().isNotNull(
                        TbSensor::getMonitorPointID
                )
        ).stream().map(TbSensor::getMonitorPointID).distinct().toList();
        Map<String, List<Integer>> collect = tbMonitorPointMapper.selectList(
                Wrappers.<TbMonitorPoint>lambdaQuery()
                        .eq(TbMonitorPoint::getMonitorType, cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType.VIDEO.getKey())
                        .in(!list.isEmpty(), TbMonitorPoint::getID, list)
                        .select(TbMonitorPoint::getProjectID, TbMonitorPoint::getID)
        ).stream().collect(Collectors.groupingBy(e -> e.getProjectID().toString(), Collectors.collectingAndThen(
                Collectors.toList(), pointList -> pointList.stream().map(TbMonitorPoint::getID).toList()
        )));
        monitorRedisService.putAll(VIDEO_POINT_ID, collect);
        collect.forEach(monitorRedisService::putAll);
    }

    @Override
    public void cachedIntelDeviceStatistics() {
        List<TbProjectInfo> projectInfoList = projectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>()
                        .select(TbProjectInfo::getID, TbProjectInfo::getCompanyID)
        );
        // <companyID, <PID, List>>， 没有项目ID的为-1
        Map<Integer, Map<Integer, List<String>>> vieocountMap = tbVideoDeviceMapper.selectAllList().stream()
                .collect(Collectors.groupingBy(TbVideoDevice::getCompanyID,
                        Collectors.groupingBy(e -> e.getProjectID() == null ? -1 : e.getProjectID(), Collectors.mapping(TbVideoDevice::getDeviceToken, Collectors.toList()))));
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(null)
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(null)
                .sendEnable(true)
                .deviceToken(null)
                .online(null)
                .productID(null)
                .build();
        Map<String, Map<Integer, List<String>>> iotcountMap = new HashMap<>();
        projectInfoList.stream().collect(Collectors.groupingBy(
                TbProjectInfo::getCompanyID
        )).forEach((companyID, list) -> {
            request.setCompanyID(companyID);
            request.setSendAddressList(list.stream().map(e -> e.getID().toString()).toList());
            ResultWrapper<List<SimpleDeviceV5>> resultWrapper = iotService.queryDeviceSimpleBySenderAddress(request);
            if (!resultWrapper.apiSuccess()) {
                throw new CustomBaseException(resultWrapper.getCode(), resultWrapper.getMsg());
            }
            if (ObjectUtil.isNotEmpty(resultWrapper.getData())) {
                Map<Integer, List<String>> integerListHashMap = new HashMap<>();
                resultWrapper.getData().forEach(
                        e -> {
                            e.getSendAddressList().forEach(
                                    ee -> {
                                        if (integerListHashMap.containsKey(Integer.valueOf(ee))) {
                                            integerListHashMap.get(Integer.valueOf(ee)).add(e.getDeviceToken());
                                        } else {
                                            List<String> list1 = new ArrayList<>();
                                            list1.add(e.getDeviceToken());
                                            integerListHashMap.put(Integer.valueOf(ee), list1);

                                        }
                                    }
                            );
                        }
                );
                iotcountMap.put(companyID.toString(), integerListHashMap);
            }
        });
        Map<String, Map<String, CacheIntelDeviceStatItem>> collect = new HashMap<>();
        projectInfoList.stream().map(TbProjectInfo::getCompanyID).distinct().forEach(companyID -> {
            if (vieocountMap.containsKey(companyID))
                vieocountMap.get(companyID).forEach((pid, tokens) -> {
                    CacheIntelDeviceStatItem item = collect.computeIfAbsent(companyID.toString(), k -> new HashMap<>()).getOrDefault(pid.toString(), CacheIntelDeviceStatItem.builder().build());
                    item.setVideoDeviceTokens(tokens);
                    collect.get(companyID.toString()).put(pid.toString(), item);
                });
            if (iotcountMap.containsKey(companyID.toString()))
                iotcountMap.get(companyID.toString()).forEach((pid, tokens) -> {
                    CacheIntelDeviceStatItem item = collect.computeIfAbsent(companyID.toString(), k -> new HashMap<>()).getOrDefault(pid.toString(), CacheIntelDeviceStatItem.builder().build());
                    item.setIotDeviceTokens(tokens);
                    collect.get(companyID.toString()).put(pid.toString(), item);
                });
        });
        monitorRedisService.putAll(INTEL_DEVICE_STATS, collect);
    }

    @Override
    public void cachedReservoirDetail() {
        List<TbProjectInfo> tbProjectInfos = projectInfoMapper.selectList(
                Wrappers.<TbProjectInfo>lambdaQuery()
                        .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
                        .select(TbProjectInfo::getID, TbProjectInfo::getProjectName, TbProjectInfo::getShortName, TbProjectInfo::getCompanyID)
        );
        List<PropertyDto> propertyDtos = projectPropertyMapper.queryPropertyByProjectID(
                tbProjectInfos.stream().map(TbProjectInfo::getID).toList(), null,
                PropertySubjectType.Project.getType()
        ).stream().filter(e -> ObjectUtil.isNotEmpty(e.getValue())).toList();
        Map<Tuple<Integer, String>, String> map1 = propertyDtos.stream().collect(Collectors.toMap(
                e -> new Tuple<Integer, String>(e.getProjectID(), e.getName())
                , PropertyDto::getValue
        ));
        Map<String, CacheReservoirDetail> collect = tbProjectInfos.stream()
                .collect(Collectors.groupingBy(TbProjectInfo::getCompanyID))
                .entrySet().stream()
                .flatMap(
                        entry -> {
                            // 远程调用获取别的属性
                            ResultWrapper<List<QueryReservoirResponsibleListResponseItem>> wrapper = wtReportService.queryReservoirResponsibleList(
                                    QueryReservoirResponsibleListRequest.builder()
                                            .companyID(entry.getKey())
                                            .build()
                            );
                            if (!wrapper.apiSuccess()) {
                                throw new CustomBaseException(wrapper.getCode(), wrapper.getMsg());
                            }
                            Map<Tuple<Integer, Integer>, String> map2 = ObjectUtil.isEmpty(wrapper) ? Map.of() :
                                    wrapper.getData().stream().collect(Collectors.toMap(
                                            e1 -> new Tuple<>(e1.getProjectID(), e1.getType())
                                            , QueryReservoirResponsibleListResponseItem::getUser,
                                            (e1, e2) -> e1 + "," + e2
                                    ));
                            return entry.getValue().stream().map(e -> {
                                CacheReservoirDetail result = CacheReservoirDetail.builder()
                                        .projectID(e.getID())
                                        .projectName(e.getProjectName())
                                        .shortName(e.getShortName())
                                        .build();
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_SCALE)))
                                        .ifPresentOrElse(
                                                result::setReservoirScale
                                                , () -> result.setReservoirScale(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_CHECKFLOODWATER)))
                                        .ifPresentOrElse(
                                                s -> result.setCheckFloodWater(Double.valueOf(s))
                                                , () -> result.setCheckFloodWater(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_DESIGNFLOODWATER)))
                                        .ifPresentOrElse(
                                                s -> result.setDesignFloodWater(Double.valueOf(s))
                                                , () -> result.setDesignFloodWater(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_NORMALSTORAGEWATER)))
                                        .ifPresentOrElse(
                                                s -> result.setNormalStorageWater(Double.valueOf(s))
                                                , () -> result.setNormalStorageWater(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_PERIODLIMITWATER)))
                                        .ifPresentOrElse(
                                                s -> result.setPeriodLimitWater(Double.valueOf(s))
                                                , () -> result.setPeriodLimitWater(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_TOTALCAPACITY)))
                                        .ifPresentOrElse(
                                                s -> result.setTotalCapacity(Double.valueOf(s))
                                                , () -> result.setTotalCapacity(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_MANAGEUNIT)))
                                        .ifPresentOrElse(
                                                result::setManageUnit
                                                , () -> result.setManageUnit(null)
                                        );
                                Optional.ofNullable(map1.get(new Tuple<>(e.getID(), STR_RESERVOIR_PROPERTY_NAME_CONTACTSPHONE)))
                                        .ifPresentOrElse(
                                                result::setContactsPhone
                                                , () -> result.setContactsPhone(null)
                                        );
                                // 责任人
                                Optional.ofNullable(map2.get(new Tuple<>(e.getID(), STR_RESERVOIR_RESPONSIBLE_TYPE_ADMINISTRATIONDIRECTOR)))
                                        .ifPresentOrElse(
                                                result::setAdministrationDirector
                                                , () -> result.setAdministrationDirector(null)
                                        );
                                Optional.ofNullable(map2.get(new Tuple<>(e.getID(), STR_RESERVOIR_RESPONSIBLE_TYPE_MAINMANAGEMENTDIRECTOR)))
                                        .ifPresentOrElse(
                                                result::setMainManagementDirector
                                                , () -> result.setMainManagementDirector(null)
                                        );
                                Optional.ofNullable(map2.get(new Tuple<>(e.getID(), STR_RESERVOIR_RESPONSIBLE_TYPE_MANAGEMENTDIRECTOR)))
                                        .ifPresentOrElse(
                                                result::setManagementDirector
                                                , () -> result.setManagementDirector(null)
                                        );
                                Optional.ofNullable(map2.get(new Tuple<>(e.getID(), STR_RESERVOIR_RESPONSIBLE_TYPE_PATROLDIRECTOR)))
                                        .ifPresentOrElse(
                                                result::setPatrolDirector
                                                , () -> result.setPatrolDirector(null)
                                        );
                                Optional.ofNullable(map2.get(new Tuple<>(e.getID(), STR_RESERVOIR_RESPONSIBLE_TYPE_ECHNICALDIRECTOR)))
                                        .ifPresentOrElse(
                                                result::setTechnicalDirector
                                                , () -> result.setTechnicalDirector(null)
                                        );
                                return result;
                            });
                        }
                ).collect(Collectors.toMap(e -> e.getProjectID().toString(), Function.identity()));
        monitorRedisService.putAll(RESERVOIR_DETAIL, collect);
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
    private ApplicationRunner XX() {
        return args -> {
            cachedIntelDeviceStatistics();
        };
    }
}