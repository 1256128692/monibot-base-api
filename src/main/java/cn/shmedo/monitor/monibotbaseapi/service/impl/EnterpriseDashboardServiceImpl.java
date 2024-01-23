package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.IndustryDistributionRes;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.QueryProductServicesRes;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.service.EnterpriseDashboardService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2024/1/18 16:32
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.impl
 * @ClassName: EnterpriseDashboardServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class EnterpriseDashboardServiceImpl implements EnterpriseDashboardService {
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private RedisService monitorRedisService;
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.IOT_REDIS_SERVICE)
    private RedisService iotRedisService;

    @Override
    public List<IndustryDistributionRes> queryIndustryDistribution(QueryIndustryDistributionParam param) {
        Map<String, ProjectInfoCache> projectInfoCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_KEY, ProjectInfoCache.class);
        if (CollectionUtil.isEmpty(projectInfoCacheMap))
            return Collections.emptyList();

        List<IndustryDistributionRes> distributionResList = Lists.newArrayList();
        List<IndustryDistributionRes> finalDistributionResList = distributionResList;
        // 按照行业类型分组
        Map<String, List<ProjectInfoCache>> groupMap = projectInfoCacheMap.values().stream().collect(Collectors.groupingBy(ProjectInfoCache::getProjectMainTypeName));
        // 处理空数据
        Arrays.stream(PlatformType.values())
                .filter(p -> !PlatformType.MDNET.getType().equals(p.getType()))
                .forEach(p -> Optional.of(p)
                        .filter(t -> !groupMap.containsKey(t.getTypeStr()))
                        .map(t -> new IndustryDistributionRes(t.getType(), t.getTypeStr(), 0, 0))
                        .map(finalDistributionResList::add));
        // 组装返回结果
        Date previousYear = TimeUtil.previousYear();
        groupMap.forEach((k, v) -> {
            long inThePastYearCount = v.stream().filter(p -> p.getCreateTime().after(previousYear)).count();
            IndustryDistributionRes distributionRes = new IndustryDistributionRes(PlatformType.getByTypeStr(k).getType(), k, v.size(), inThePastYearCount);
            finalDistributionResList.add(distributionRes);
        });
        // 条形图按照数量大小排列，数量大的放前面
        distributionResList = distributionResList.stream()
                .sorted(Comparator.comparing(IndustryDistributionRes::getProjectCount).reversed()).collect(Collectors.toList());
        return distributionResList;
    }

    @Override
    public List<QueryProductServicesRes> queryProductServices(QueryProductServicesParam param) {
        Map<String, List<QueryProductServicesRes.Product>> productMap = new HashMap<>();
        // 项目信息
        Map<String, ProjectInfoCache> projectInfoCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_KEY, ProjectInfoCache.class);
        // 项目类型
        Map<String, TbProjectType> projectTypeCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_TYPE_KEY, TbProjectType.class);
        // 监测类型
        Map<String, MonitorTypeCacheData> monitorTypeMap = iotRedisService.getAll(RedisKeys.MONITOR_TYPE_KEY, MonitorTypeCacheData.class);
        // 条件过滤工程项目
        Set<Integer> projectIDSet = projectInfoCacheMap.values().stream().filter(v -> {
            if (Objects.nonNull(param.getProjectMainType())) {
                PlatformType platformType = PlatformType.getPlatformType(param.getProjectMainType());
                List<Byte> projectTypeSet = projectTypeCacheMap.values().stream()
                        .filter(m -> platformType.getTypeStr().equals(m.getMainType())).map(TbProjectType::getID).toList();
                return projectTypeSet.contains(v.getProjectType());
            } else {
                return true;
            }
        }).map(ProjectInfoCache::getID).collect(Collectors.toSet());

        Optional.of(projectIDSet).ifPresent(set -> set.forEach(projectID -> {
            // key-工程项目分组
            String key = RedisKeys.MONITOR_TYPE_DEVICE_COUNT + ":" + projectID;
            if (monitorRedisService.hasKey(key)) {
                // key-MonitorType；value-统计数据
                Map<String, String> map = monitorRedisService.getAll(key);
                // 合并不同项目下具有相同的监测类型
                map.forEach((k, v) -> productMap.computeIfAbsent(k, (m) -> new ArrayList<>()).addAll(JSONUtil.toList(v, QueryProductServicesRes.Product.class)));
            }
        }));

        // 处理返回数据
        List<QueryProductServicesRes> resList = new ArrayList<>();
        productMap.forEach((k, v) -> {
            QueryProductServicesRes res = new QueryProductServicesRes();
            // 处理不同项目下具有相同的监测类型
            int deviceCount = v.stream().mapToInt(QueryProductServicesRes.Product::getDeviceCount).sum();
            Map<Integer, QueryProductServicesRes.Product> countMap = v.stream().collect(Collectors.groupingBy(QueryProductServicesRes.Product::getProductID,
                    Collectors.collectingAndThen(Collectors.toList(), li -> {
                        li.get(0).setDeviceCount(li.stream().mapToInt(QueryProductServicesRes.Product::getDeviceCount).sum());
                        return li.get(0);
                    })));
            res.setMonitorType(Integer.parseInt(k))
                    .setMonitorTypeName(monitorTypeMap.get(k).getTypeName())
                    .setDeviceCount(deviceCount)
                    .setProductList(new ArrayList<>(countMap.values()));
            resList.add(res);
        });
        // 条形图按照数量的倒序排列
        return resList.stream().sorted(Comparator.comparing(QueryProductServicesRes::getDeviceCount).reversed()).collect(Collectors.toList());
    }

}
