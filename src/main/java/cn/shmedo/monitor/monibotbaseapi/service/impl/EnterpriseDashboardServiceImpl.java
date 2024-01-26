package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.model.cache.DeviceOnlineCache;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProvinceProjectDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.QueryResourceListByPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceStatisticByMonitorProjectListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.iot.DeviceStatisticByMonitorProjectListResult;
import cn.shmedo.monitor.monibotbaseapi.service.EnterpriseDashboardService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.PermissionService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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

    private final IotService iotService;
    private Map<Long, RegionArea> provincialCapitalMap;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @Override
    public void afterPropertiesSet() throws Exception {
        // 行政区划（地理位置信息认为不为变化，配置从缓存中加载一次），省会映射 key:provinceCode
        provincialCapitalMap = monitorRedisService.getAll(RedisKeys.REGION_AREA_KEY, RegionArea.class).values().stream()
                .filter(r -> r.getLevel() == 1 && Objects.nonNull(r.getParentCode()) &&
                        // 省会code码，取省code码前3位，然后拼接100
                        r.getAreaCode().equals(Long.valueOf(String.valueOf(r.getParentCode()).substring(0, 3) + "100")))
                .collect(Collectors.toMap(RegionArea::getParentCode, Function.identity()));
    }

    @Override
    public List<IndustryDistributionRes> queryIndustryDistribution(QueryIndustryDistributionParam param) {
        List<IndustryDistributionRes> distributionResList = Lists.newArrayList();
        // 从缓存获取工程项目信息
        Map<String, ProjectInfoCache> projectInfoCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_KEY, ProjectInfoCache.class);
        // 按照行业类型分组
        Map<String, List<ProjectInfoCache>> groupMap = projectInfoCacheMap.values().stream().collect(Collectors.groupingBy(ProjectInfoCache::getProjectMainTypeName));
        // 处理空数据（大屏暂未将MDNET平台纳入）
        Arrays.stream(PlatformType.values())
                .filter(p -> !PlatformType.MDNET.getType().equals(p.getType()))
                .forEach(p -> Optional.of(p)
                        .filter(t -> !groupMap.containsKey(t.getTypeStr()))
                        .map(t -> new IndustryDistributionRes(t.getType(), t.getTypeStr(), 0, 0))
                        .map(distributionResList::add));
        // 组装返回结果
        Date previousYear = TimeUtil.previousYear(1);
        groupMap.forEach((k, v) -> {
            // 近一年增长量
            long inThePastYearCount = v.stream().filter(p -> p.getCreateTime().after(previousYear)).count();
            IndustryDistributionRes distributionRes = new IndustryDistributionRes(PlatformType.getByTypeStr(k).getType(), k, v.size(), inThePastYearCount);
            distributionResList.add(distributionRes);
        });
        // 条形图按照数量大小排列，数量大的放前面
        return distributionResList.stream().sorted(Comparator.comparing(IndustryDistributionRes::getProjectCount).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<ProductServicesRes> queryProductServices(QueryProductServicesParam param) {
        Map<String, List<ProductServicesRes.Product>> productMap = new HashMap<>();
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        projectInfoCacheList.stream().map(ProjectInfoCache::getID).collect(Collectors.toSet()).forEach(projectID -> {
            // key-工程项目分组
            String key = RedisKeys.MONITOR_TYPE_DEVICE_COUNT + ":" + projectID;
            if (monitorRedisService.hasKey(key)) {
                // key-MonitorType；value-统计数据
                Map<String, String> map = monitorRedisService.getAll(key);
                // 合并不同项目下具有相同的监测类型
                map.forEach((k, v) -> productMap.computeIfAbsent(k, (m) -> new ArrayList<>()).addAll(JSONUtil.toList(v, ProductServicesRes.Product.class)));
            }
        });

        // 获取监测类型缓存
        Map<String, MonitorTypeCacheData> monitorTypeMap = iotRedisService.getAll(RedisKeys.MONITOR_TYPE_KEY, MonitorTypeCacheData.class);
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
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        // 服务客户、覆盖区域、管理工程
        long companyCount = projectInfoCacheList.stream().map(ProjectInfoCache::getCompanyID).distinct().count();
        long areaCount = projectInfoCacheList.stream().map(p -> p.getLocationInfo().getCity()).distinct().count();
        long projectCount = projectInfoCacheList.stream().map(ProjectInfoCache::getID).distinct().count();
        return new ResourceOverviewRes()
                .setCompanyCount(companyCount)
                .setAreaCount(areaCount)
                .setProjectCount(projectCount);
    }

    @Override
    public List<ProvinceProjectRes> queryProvinceProject(QueryProductServicesParam param) {
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        Set<ProjectInfoCache.LocationInfo> locationInfoSet = projectInfoCacheList.stream().map(ProjectInfoCache::getLocationInfo).collect(Collectors.toSet());
        Map<Integer, List<ProjectInfoCache.LocationInfo>> provinceMap = locationInfoSet.stream().collect(Collectors.groupingBy(ProjectInfoCache.LocationInfo::getProvince));
        Map<Integer, List<ProjectInfoCache.LocationInfo>> cityMap = locationInfoSet.stream().collect(Collectors.groupingBy(ProjectInfoCache.LocationInfo::getCity));
        Map<Integer, Map<Integer, List<ProjectInfoCache>>> map = projectInfoCacheList.stream()
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
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), param.getProvinceCode());
        if (CollectionUtil.isEmpty(projectInfoCacheList))
            return null;
        Map<Integer, List<ProjectInfoCache>> map = projectInfoCacheList.stream()
                .collect(Collectors.groupingBy(province -> province.getLocationInfo().getProvince()));
        List<ProjectInfoCache> provinceProjectInfoCacheList = map.get(param.getProvinceCode());
        List<ProvinceProjectDetailRes.Project> projectList = CustomizeBeanUtil.copyListProperties(provinceProjectInfoCacheList, ProvinceProjectDetailRes.Project::new);
        String provinceName = provinceProjectInfoCacheList.get(0).getLocationInfo().getProvinceName();
        return new ProvinceProjectDetailRes()
                .setProvinceCode(param.getProvinceCode())
                .setProvinceName(provinceName.replace("省", "").replace("市", ""))
                .setProjectCount(projectList.size())
                .setProjectList(projectList);
    }

    @Override
    public DataAccessRes queryDataAccess(QueryProductServicesParam param) {
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        Set<String> cacheProjectIDSet = projectInfoCacheList.stream().map(ProjectInfoCache::getID).map(String::valueOf).collect(Collectors.toSet());
        Map<String, DataAccessRes> accessCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_DEVICE_MANAGEMENT_DATA_ACCESS, DataAccessRes.class);
        // 过滤出符合项目的数据
        List<DataAccessRes> dataAccessResList = new ArrayList<>();
        accessCacheMap.forEach((k, v) -> {
            if (cacheProjectIDSet.contains(k))
                dataAccessResList.add(v);
        });
        Set<Byte> platformTypes = Objects.nonNull(param.getProjectMainType()) ? Set.of(param.getProjectMainType()) : PlatformType.defaultTypes;
        return new DataAccessRes()
                .setPlatformNameList(PlatformType.getPlatformNames(platformTypes).toArray(new String[0]))
                .setProtocolNameList(PlatformType.getProtocols(platformTypes).toArray(new String[0]))
                .setDeviceCount(dataAccessResList.stream().mapToLong(DataAccessRes::getDeviceCount).sum())
                .setIncreaseInThePastYear(dataAccessResList.stream().mapToLong(DataAccessRes::getIncreaseInThePastYear).sum())
                .setIncreaseInThePastMonth(dataAccessResList.stream().mapToLong(DataAccessRes::getIncreaseInThePastMonth).sum());
    }

    @Override
    public DataManagementRes queryDataManagement(QueryProductServicesParam param) {
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        AtomicLong total = new AtomicLong();
        Optional.ofNullable(projectInfoCacheList).ifPresent(set -> set.forEach(pro -> {
            String key = RedisKeys.DEVICE_DATA_COUNT_KEY + ":" + pro.getID();
            String value = monitorRedisService.get(key);
            if (StringUtils.isNoneBlank(value)) {
                total.addAndGet(Long.parseLong(value));
            }
        }));
        // 需要治理占千分之三；系统治理占96%；人工治理占4%
        long governanceTotal = (long) Math.floor(total.get() * 0.003);
        long systemGovernanceTotal = (long) Math.floor(governanceTotal * 0.96);
        return new DataManagementRes()
                .setTotal(total.get())
                .setGovernanceTotal(governanceTotal)
                .setSystemGovernanceTotal(systemGovernanceTotal)
                .setArtificialGovernanceTotal(governanceTotal - systemGovernanceTotal);
    }

    @Override
    public DeviceMaintenanceRes queryDeviceMaintenance(QueryProductServicesParam param) {
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        Set<Integer> projectIDSet = projectInfoCacheList.stream().map(ProjectInfoCache::getID).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(projectIDSet))
            return null;
        List<DeviceStatisticByMonitorProjectListResult> projectListResultList = queryDeviceStatistic(param.getCompanyID(), projectIDSet);
        if (CollectionUtil.isEmpty(projectListResultList))
            return null;
        AtomicLong activeCount = new AtomicLong();
        AtomicLong activeSuccessCount = new AtomicLong();
        AtomicLong otaCount = new AtomicLong();
        AtomicLong otaSuccessCount = new AtomicLong();
        AtomicLong execCount = new AtomicLong();
        AtomicLong execSuccessCount = new AtomicLong();
        AtomicLong onlineCount = new AtomicLong();
        AtomicLong offlineCount = new AtomicLong();
        projectListResultList.stream().parallel().forEach(result -> {
            activeCount.addAndGet(result.getActiveCount());
            activeSuccessCount.addAndGet(result.getActiveSuccessCount());
            otaCount.addAndGet(result.getOtaCount());
            otaSuccessCount.addAndGet(result.getOtaSuccessCount());
            execCount.addAndGet(result.getExecCount());
            execSuccessCount.addAndGet(result.getExecSuccessCount());
            onlineCount.addAndGet(result.getOnlineCount());
            offlineCount.addAndGet(result.getOfflineCount());
        });
        double deviceOnlineRate = Double.parseDouble(decimalFormat.format((double) onlineCount.get() / offlineCount.get()));
        return new DeviceMaintenanceRes()
                .setActiveCount(activeCount.get())
                .setActiveSuccessCount(activeSuccessCount.get())
                .setOtaCount(otaCount.get())
                .setOtaSuccessCount(otaSuccessCount.get())
                .setExecCount(execCount.get())
                .setExecSuccessCount(execSuccessCount.get())
                .setOnlineCount(onlineCount.get())
                .setOfflineCount(offlineCount.get())
                .setDeviceOnlineRate(deviceOnlineRate);
    }

    /**
     * 获取设备近7日在线统计率。没有值补0
     *
     * @param param QueryProductServicesParam
     * @return List<DeviceOnlineRes>
     */
    @Override
    public List<DeviceOnlineRes> queryDeviceOnlineRate(QueryProductServicesParam param) {
        List<DeviceOnlineRes> deviceOnlineResList = new ArrayList<>();
        List<DeviceOnlineCache> deviceOnlineCacheList = new ArrayList<>();
        // 按照条件统一过滤
        List<ProjectInfoCache> projectInfoCacheList = filterProject(param.getProjectMainType(), null);
        Set<Integer> projectIDSet = projectInfoCacheList.stream().map(ProjectInfoCache::getID).collect(Collectors.toSet());
        Map<String, String> onlineCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_DEVICE_ONLINE_RATE_KEY);
        onlineCacheMap.forEach((cacheKey, cacheValue) -> {
            if (projectIDSet.contains(Integer.parseInt(cacheKey)))
                deviceOnlineCacheList.addAll(JSONUtil.toList(cacheValue, DeviceOnlineCache.class));
        });
        Map<String, List<DeviceOnlineCache>> map = deviceOnlineCacheList.stream().collect(Collectors.groupingBy(DeviceOnlineCache::getDate));
        // 获取当前时间的前7天（包括当前天）
        String[] previousDays = TimeUtil.getPreviousDays(7);
        // 保证返回数据有最近七天
        for (String day : previousDays) {
            if (!map.containsKey(day)) {
                deviceOnlineResList.add(new DeviceOnlineRes().setDate(day).setOnlineRate(0D));
            }
        }
        map.forEach((k, v) -> {
            long onlineSum = v.stream().mapToLong(DeviceOnlineCache::getOnline).sum();
            long offlineSum = v.stream().mapToLong(DeviceOnlineCache::getOffline).sum();
            double deviceOnlineRate = Double.parseDouble(decimalFormat.format((double) onlineSum / (onlineSum + offlineSum)));
            DeviceOnlineRes deviceOnlineRes = new DeviceOnlineRes().setDate(k).setOnlineRate(deviceOnlineRate);
            deviceOnlineResList.add(deviceOnlineRes);
        });
        return deviceOnlineResList.stream().sorted(Comparator.comparing(DeviceOnlineRes::getDate)).collect(Collectors.toList());
    }

    /**
     * 处理QueryDeviceStatisticByMonitorProjectList接口在时间和分页大小条件上的限制
     *
     * @param companyID    公司ID
     * @param projectIDSet 项目ID列表
     * @return 设备统计列表
     */
    private List<DeviceStatisticByMonitorProjectListResult> queryDeviceStatistic(Integer companyID, Set<Integer> projectIDSet) {
        ArrayList<DeviceStatisticByMonitorProjectListResult> resultList = new ArrayList<>();
        List<List<Integer>> seperatorList = cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil.seperatorList(new ArrayList<>(projectIDSet), 100);
        seperatorList.parallelStream().forEach(seperator -> {
            QueryDeviceStatisticByMonitorProjectListParam monitorProjectListParam = new QueryDeviceStatisticByMonitorProjectListParam(
                    companyID, seperator, TimeUtil.DEFAULT_START_TIME, Timestamp.valueOf(LocalDateTime.now()));
            ResultWrapper<List<DeviceStatisticByMonitorProjectListResult>> resultWrapper = iotService.queryDeviceStatisticByMonitorProjectList(
                    monitorProjectListParam);
            if (resultWrapper.apiSuccess() && CollectionUtil.isNotEmpty(resultWrapper.getData())) {
                resultList.addAll(resultWrapper.getData());
            }
        });
        return resultList;
    }

    /**
     * 项目过滤
     * 1、根据行业类型过滤项目
     * 2、过滤出用户有权限的项目
     * 3、企业大屏只统计非米度企业下项目，过滤米度企业下项目
     *
     * @param projectMainType 行业类型
     * @param provinceCode    省份code码
     * @return 项目列表
     */
    private List<ProjectInfoCache> filterProject(Byte projectMainType, Integer provinceCode) {
        // 用户项目权限处理，只取用户有访问权限的项目
        Set<Integer> tokenSet = PermissionUtil.getResourceList(CurrentSubjectHolder.getCurrentSubject().getSubjectID(), null,
                DefaultConstant.MDNET_SERVICE_NAME, DefaultConstant.LIST_PROJECT, ResourceType.BASE_PROJECT).stream().map(Integer::valueOf).collect(Collectors.toSet());

        // 项目信息
        Map<String, ProjectInfoCache> projectInfoCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_KEY, ProjectInfoCache.class);
        // 项目类型
        Map<String, TbProjectType> projectTypeCacheMap = monitorRedisService.getAll(RedisKeys.PROJECT_TYPE_KEY, TbProjectType.class);
        // 条件过滤工程项目
        return projectInfoCacheMap.values().stream()
                // 行业过滤
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
                // 非米度企业（企业大屏默认只有米度用户能查看，这里就直接获取米度用户的所在公司），及用户项目权限过滤
                .filter(v -> !v.getCompanyID().equals(CurrentSubjectHolder.getCurrentSubject().getCompanyID()) && tokenSet.contains(v.getID()))
                // 省份code码过滤
                .filter(v -> Objects.isNull(provinceCode) || v.getLocationInfo().getProvince().equals(provinceCode)).collect(Collectors.toList());
    }

}
