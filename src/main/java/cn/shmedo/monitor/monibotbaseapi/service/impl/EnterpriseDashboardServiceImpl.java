package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProvinceProjectDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.service.EnterpriseDashboardService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
@RequiredArgsConstructor
public class EnterpriseDashboardServiceImpl implements EnterpriseDashboardService, InitializingBean {
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private RedisService monitorRedisService;
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.IOT_REDIS_SERVICE)
    private RedisService iotRedisService;
    Map<Long, RegionArea> provincialCapitalMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 行政区划，省会映射 key:provinceCode
        provincialCapitalMap = monitorRedisService.getAll(RedisKeys.REGION_AREA_KEY, RegionArea.class).values().stream()
                .filter(r -> r.getLevel() == 1 && Objects.nonNull(r.getParentCode()) &&
                        r.getAreaCode().equals(Long.valueOf(String.valueOf(r.getParentCode()).substring(0, 3) + "100")))
                .collect(Collectors.toMap(RegionArea::getParentCode, Function.identity()));
    }

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
    public List<ProductServicesRes> queryProductServices(QueryProductServicesParam param) {
        Map<String, List<ProductServicesRes.Product>> productMap = new HashMap<>();
        // 监测类型
        Map<String, MonitorTypeCacheData> monitorTypeMap = iotRedisService.getAll(RedisKeys.MONITOR_TYPE_KEY, MonitorTypeCacheData.class);

        Set<Integer> projectIDSet = filterIndustry(param.getProjectMainType(), null)
                .stream().map(ProjectInfoCache::getID).collect(Collectors.toSet());
        projectIDSet.forEach(projectID -> {
            // key-工程项目分组
            String key = RedisKeys.MONITOR_TYPE_DEVICE_COUNT + ":" + projectID;
            if (monitorRedisService.hasKey(key)) {
                // key-MonitorType；value-统计数据
                Map<String, String> map = monitorRedisService.getAll(key);
                // 合并不同项目下具有相同的监测类型
                map.forEach((k, v) -> productMap.computeIfAbsent(k, (m) -> new ArrayList<>()).addAll(JSONUtil.toList(v, ProductServicesRes.Product.class)));
            }
        });

        // 处理返回数据
        List<ProductServicesRes> resList = new ArrayList<>();
        productMap.forEach((k, v) -> {
            // 处理不同项目下具有相同的监测类型
            int deviceCount = v.stream().mapToInt(ProductServicesRes.Product::getDeviceCount).sum();
            Map<Integer, ProductServicesRes.Product> countMap = v.stream().collect(Collectors.groupingBy(ProductServicesRes.Product::getProductID,
                    Collectors.collectingAndThen(Collectors.toList(), li -> {
                        li.get(0).setDeviceCount(li.stream().mapToInt(ProductServicesRes.Product::getDeviceCount).sum());
                        return li.get(0);
                    })));
            ProductServicesRes res = new ProductServicesRes().setMonitorType(Integer.parseInt(k))
                    .setMonitorTypeName(monitorTypeMap.get(k).getTypeName())
                    .setDeviceCount(deviceCount)
                    .setProductList(new ArrayList<>(countMap.values()));
            resList.add(res);
        });
        // 条形图按照数量的倒序排列
        return resList.stream().sorted(Comparator.comparing(ProductServicesRes::getDeviceCount).reversed()).collect(Collectors.toList());
    }

    @Override
    public ResourceOverviewRes queryResourceOverview(QueryProductServicesParam param) {
        // 服务客户、覆盖区域、管理工程
        Set<ProjectInfoCache> projectInfoCacheSet = filterIndustry(param.getProjectMainType(), null);
        long companyCount = projectInfoCacheSet.stream().map(ProjectInfoCache::getCompanyID).distinct().count();
        long areaCount = projectInfoCacheSet.stream().map(p -> p.getLocationInfo().getCity()).distinct().count();
        long projectCount = projectInfoCacheSet.stream().map(ProjectInfoCache::getID).distinct().count();
        return new ResourceOverviewRes()
                .setCompanyCount((int) companyCount)
                .setAreaCount((int) areaCount)
                .setProjectCount((int) projectCount);
    }

    @Override
    public List<ProvinceProjectRes> queryProvinceProject(QueryProductServicesParam param) {
        // 条件过滤
        Set<ProjectInfoCache> projectInfoCacheSet = filterIndustry(param.getProjectMainType(), null);
        Set<ProjectInfoCache.LocationInfo> locationInfoSet = projectInfoCacheSet.stream().map(ProjectInfoCache::getLocationInfo).collect(Collectors.toSet());
        Map<Integer, List<ProjectInfoCache.LocationInfo>> provinceMap = locationInfoSet.stream().collect(Collectors.groupingBy(ProjectInfoCache.LocationInfo::getProvince));
        Map<Integer, List<ProjectInfoCache.LocationInfo>> cityMap = locationInfoSet.stream().collect(Collectors.groupingBy(ProjectInfoCache.LocationInfo::getCity));
        Map<Integer, Map<Integer, List<ProjectInfoCache>>> map = projectInfoCacheSet.stream()
                .collect(Collectors.groupingBy(province -> province.getLocationInfo().getProvince(),
                        Collectors.groupingBy(city -> city.getLocationInfo().getCity())));

        ArrayList<ProvinceProjectRes> resList = new ArrayList<>();
        map.forEach((provinceCode, provinceValue) -> {
            ArrayList<ProvinceProjectRes.City> cityList = new ArrayList<>();
            provinceValue.forEach((cityCode, cityValue) -> {
                ProvinceProjectRes.City city = new ProvinceProjectRes.City()
                        .setCityCode(cityCode)
                        .setCityName(cityMap.get(cityCode).get(0).getCityName())
                        .setProjectCount(cityValue.size());
                cityList.add(city);
            });
            String provinceName = provinceMap.get(provinceCode).get(0).getProvinceName();
            RegionArea provincialCapital = provincialCapitalMap.get(Long.valueOf(provinceCode));
            ProvinceProjectRes res = new ProvinceProjectRes()
                    .setProvinceCode(provinceCode)
                    .setProvinceName(provinceName.replace("省", "").replace("市", ""))
                    .setProvincialCapital(provincialCapital.getShortName())
                    .setProjectCount(provinceValue.values().stream().flatMap(Collection::stream).toList().size())
                    .setCityList(cityList)
                    .setLongitude(provincialCapital.getLng())
                    .setLatitude(provincialCapital.getLat());
            resList.add(res);
        });
        return resList;
    }

    @Override
    public ProvinceProjectDetailRes queryProvinceProjectDetail(QueryProvinceProjectDetailParam param) {
        Set<ProjectInfoCache> projectInfoCacheSet = filterIndustry(param.getProjectMainType(), param.getProvinceCode());
        if(CollectionUtil.isEmpty(projectInfoCacheSet))
            return null;
        Map<Integer, List<ProjectInfoCache>> map = projectInfoCacheSet.stream()
                .collect(Collectors.groupingBy(province -> province.getLocationInfo().getProvince()));
        List<ProjectInfoCache> projectInfoCacheList = map.get(param.getProvinceCode());
        List<ProvinceProjectDetailRes.Project> projectList = CustomizeBeanUtil.copyListProperties(projectInfoCacheList, ProvinceProjectDetailRes.Project::new);
        String provinceName = projectInfoCacheList.get(0).getLocationInfo().getProvinceName();
        return new ProvinceProjectDetailRes()
                .setProvinceCode(param.getProvinceCode())
                .setProvinceName(provinceName.replace("省", "").replace("市", ""))
                .setProjectCount(projectList.size())
                .setProjectList(projectList);
    }

    /**
     * 根据行业类型，或省份code码获取项目列表
     *
     * @param projectMainType 行业类型
     * @param provinceCode    省份code码
     * @return 项目列表
     */
    private Set<ProjectInfoCache> filterIndustry(Byte projectMainType, Integer provinceCode) {
        // 项目信息
        Map<String, ProjectInfoCache> projectInfoCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_KEY, ProjectInfoCache.class);
        // 项目类型
        Map<String, TbProjectType> projectTypeCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_TYPE_KEY, TbProjectType.class);
        // 条件过滤工程项目
        return projectInfoCacheMap.values().stream()
                .filter(v -> {
                    if (Objects.nonNull(projectMainType)) {
                        PlatformType platformType = PlatformType.getPlatformType(projectMainType);
                        List<Byte> projectTypeSet = projectTypeCacheMap.values().stream()
                                .filter(m -> platformType.getTypeStr().equals(m.getMainType())).map(TbProjectType::getID).toList();
                        return projectTypeSet.contains(v.getProjectType());
                    } else {
                        return true;
                    }
                })
                .filter(v -> Objects.isNull(provinceCode) || v.getLocationInfo().getProvince().equals(provinceCode)).collect(Collectors.toSet());
    }

}
