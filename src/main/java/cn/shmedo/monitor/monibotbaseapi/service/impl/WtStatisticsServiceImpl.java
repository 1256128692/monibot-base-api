package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnBaseConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.DeviceOnlineStats;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorWithIot;
import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.WarnPointStats;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevelStyle;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryDeviceOnlineStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceInfoByUniqueTokensParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.DeviceOnlineStatsResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse;
import cn.shmedo.monitor.monibotbaseapi.service.WtStatisticsService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.*;
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
    private final IotService iotService;

    @Override
    public ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param) {
        WarnTag warnTag = WarnTag.TYPE1;
        WarnLevelStyle style = WarnLevelStyle.COLOR;
        int levelNum = 4;
        if (param.getPlatform() != null) {
            TbWarnBaseConfig config = warnBaseConfigMapper.selectOne(Wrappers.<TbWarnBaseConfig>lambdaQuery()
                    .eq(TbWarnBaseConfig::getPlatform, param.getPlatform())
                    .eq(TbWarnBaseConfig::getCompanyID, param.getCompanyID()));

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

        List<WarnPointStats> data = param.getProjects().stream()
                .flatMap(e -> monitorRedisService.getAll(WARN_POINT_STATS + e, WarnPointStats.class)
                        .values().stream()).toList();

        Item overview = Item.from(data);

        if (data.isEmpty()) {
            return new ReservoirWarnStatsResponse(dict, overview, List.of());
        }

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
                                WarnPointStats cacheObj = new WarnPointStats(level1, level2, level3, level4,
                                        offline, typeGroup.getKey());
                                return Tuples.of(entry.getKey(), typeGroup.getKey(), cacheObj);
                            });
                }).collect(Collectors.groupingBy(t -> WARN_POINT_STATS + t.getT1(), Collectors.toMap(Tuple3::getT2, Tuple3::getT3)));
        cacheMap.forEach(monitorRedisService::putAll);
        cacheMap.clear();
    }

    @Override
    public DeviceOnlineStatsResponse queryDeviceOnlineStats(QueryDeviceOnlineStatsParam param) {
        List<DeviceOnlineStats> data = param.getProjects().stream()
                .flatMap(e -> monitorRedisService.getAll(DEVICE_ONLINE_STATS + e, DeviceOnlineStats.class)
                        .values().stream()).toList();

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
        if (list.isEmpty()) {
            return;
        }

        List<String> uniqueTokens = list.stream().map(SensorWithIot::getIotUniqueToken).distinct().toList();
        Map<String, Boolean> onlineMap = Optional.ofNullable(iotService
                        .queryDeviceInfoByUniqueTokens(new QueryDeviceInfoByUniqueTokensParam(uniqueTokens)).getData())
                .orElse(List.of())
                .stream().collect(Collectors.toMap(DeviceInfo::getUniqueToken, DeviceInfo::getOnline));

        Map<String, Map<Integer, DeviceOnlineStats>> cacheMap = list.stream().collect(Collectors.groupingBy(SensorWithIot::getProjectID))
                .entrySet().stream().flatMap(entry -> {
                    //项目 监测类型  设备
                    return entry.getValue().stream()
                            .collect(Collectors.groupingBy(SensorWithIot::getMonitorType))
                            .entrySet().stream().map(typeGroup -> {
                                Map<Boolean, Long> collect = typeGroup.getValue().stream()
                                        .map(SensorWithIot::getIotUniqueToken)
                                        .map(e -> onlineMap.getOrDefault(e, Boolean.FALSE))
                                        .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

                                DeviceOnlineStats cacheObj = new DeviceOnlineStats(collect.getOrDefault(Boolean.TRUE, 0L),
                                        collect.getOrDefault(Boolean.TRUE, 0L),
                                        collect.values().stream().mapToLong(e -> e).count(), typeGroup.getKey());
                                return Tuples.of(entry.getKey(), typeGroup.getKey(), cacheObj);
                            });
                }).collect(Collectors.groupingBy(t -> DEVICE_ONLINE_STATS + t.getT1(), Collectors.toMap(Tuple3::getT2, Tuple3::getT3)));

        cacheMap.forEach(monitorRedisService::putAll);
        cacheMap.clear();
    }

}