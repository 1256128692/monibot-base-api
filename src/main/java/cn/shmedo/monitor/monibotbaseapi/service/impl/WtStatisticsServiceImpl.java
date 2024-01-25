package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnBaseConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats.WarnPointStats;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevelStyle;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryReservoirWarnStatsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse;
import cn.shmedo.monitor.monibotbaseapi.service.WtStatisticsService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys.WARN_POINT_STATS;
import static cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.ReservoirWarnStatsResponse.*;

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

    @Override
    public ReservoirWarnStatsResponse queryWarnStats(QueryReservoirWarnStatsParam param) {
        WarnTag warnTag = WarnTag.TYPE1;
        WarnLevelStyle style = WarnLevelStyle.COLOR;
        if (param.getPlatform() != null) {
            TbWarnBaseConfig config = warnBaseConfigMapper.selectOne(Wrappers.<TbWarnBaseConfig>lambdaQuery()
                    .eq(TbWarnBaseConfig::getPlatform, param.getPlatform())
                    .eq(TbWarnBaseConfig::getCompanyID, param.getCompanyID()));

            if (config != null) {
                warnTag = WarnTag.fromCode(config.getWarnTag());
                style = WarnLevelStyle.fromCode(config.getWarnLevelStyle());
            }
        }
        String[] split = style.getDesc().split(StrUtil.COMMA);
        Dict dict = new Dict(split[0] + warnTag.getDesc(), split[1] + warnTag.getDesc(),
                split[2] + warnTag.getDesc(), split[3] + warnTag.getDesc(), "离线" + warnTag.getDesc());

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

}