package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.IndustryDistributionRes;
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

    @Override
    public List<IndustryDistributionRes> queryIndustryDistribution(QueryIndustryDistributionParam param) {
        Map<String, ProjectInfoCache> infoCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_KEY, ProjectInfoCache.class);
        if (CollectionUtil.isEmpty(infoCacheMap))
            return Collections.emptyList();

        List<IndustryDistributionRes> distributionResList = Lists.newArrayList();
        List<IndustryDistributionRes> finalDistributionResList = distributionResList;
        // 按照行业类型分组并条件过滤
        Map<String, List<ProjectInfoCache>> groupMap = infoCacheMap.values().stream()
                .filter(p -> Objects.isNull(param.getProjectMainType()) ||
                        PlatformType.getPlatformType(param.getProjectMainType()).getTypeStr().equals(p.getProjectMainTypeName()))
                .collect(Collectors.groupingBy(ProjectInfoCache::getProjectMainTypeName));
        // 处理空数据（param.getProjectMainType()，为空，补全量；非空，补单条）
        Arrays.stream(PlatformType.values())
                .filter(p -> !PlatformType.MDNET.getType().equals(p.getType()))
                .forEach(p -> Optional.of(p)
                        .filter(t -> !groupMap.containsKey(t.getTypeStr()))
                        .filter(t -> Objects.isNull(param.getProjectMainType()) || param.getProjectMainType().equals(t.getType()))
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
    public Object queryProductServices(QueryProductServicesParam param) {

        // todo 条形图按照数量的倒序排列
        return null;
    }
}
