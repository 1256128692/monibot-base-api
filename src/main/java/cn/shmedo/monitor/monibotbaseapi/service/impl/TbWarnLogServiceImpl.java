package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWorkOrderMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.engineField.FieldShowUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnLogServiceImpl extends ServiceImpl<TbWarnLogMapper, TbWarnLog> implements ITbWarnLogService {
    private final RedisService redisService;

    private final TbWorkOrderMapper workOrderMapper;
    private final TbProjectInfoMapper projectMapper;
    private static final String METEOROLOGICAL_OBSERVATORY = "气象台";

    @Override
    public PageUtil.Page<WtWarnLogInfo> queryByPage(QueryWtWarnLogPageParam param) {
        // 实时记录是测点报警状态最新一条报警数据，并不是指当天的；历史记录是除了最新报警状态的报警数据其他触发报警状态的报警数据
        IPage<WtWarnLogInfo> page;
        switch (WarnType.formCode(param.getWarnType())) {
            case MONITOR: //在线监测报警
                page = this.baseMapper.queryMonitorWarnPage(new Page<>(param.getCurrentPage(), param.getPageSize()), param);
                return new PageUtil.Page<>(page.getPages(), page.getRecords(), page.getTotal());
            case CAMERA: //视频监控报警
                page = this.baseMapper.queryCameraWarnPage(new Page<>(param.getCurrentPage(), param.getPageSize()), param);
                //使用deviceToken查询设备信息填充deviceTypeName(设备类型名)
                List<WtWarnLogInfo> data = page.getRecords();
                TransferUtil.INSTANCE.applyProductName(data,
                        () -> QueryDeviceBaseInfoParam.builder()
                                .deviceTokens(data.stream().map(WtWarnLogInfo::getDeviceToken).collect(Collectors.toSet()))
                                .companyID(param.getCompanyID()).build(),
                        WtWarnLogInfo::getDeviceToken,
                        WtWarnLogInfo::setDeviceTypeName);
                return new PageUtil.Page<>(page.getPages(), data, page.getTotal());
        }
        return PageUtil.Page.empty();
    }

    @Override
    public WtWarnDetailInfo queryDetail(QueryWtWarnDetailParam param) {
        switch (WarnType.formCode(param.getWarnType())) {
            case MONITOR:   //在线监测报警
                return FieldShowUtil.dealFieldShow(this.baseMapper.queryMonitorDetail(param.getWarnID()));
            case CAMERA:    //视频监控报警
                WtWarnDetailInfo cameraWarn = this.baseMapper.queryCameraDetail(param.getWarnID());
                //从redis中获取regionArea信息填充regionArea(区域名)
                Optional.ofNullable(cameraWarn.getRegionArea()).filter(JSONUtil::isTypeJSON).ifPresent(e -> {
                    Map<String, Object> regionInfo = JSONUtil.toBean(e, Dict.class);
                    RegionArea regionArea = redisService.get(RedisKeys.REGION_AREA_KEY,
                            (String) CollUtil.getLast(regionInfo.values()), RegionArea.class);
                    cameraWarn.setRegionArea(regionArea.getName());
                });
                //使用deviceToken查询设备信息填充deviceTypeName(设备类型名)
                Optional.ofNullable(cameraWarn.getDeviceToken()).filter(e -> !e.isBlank())
                        .ifPresent(deviceToken -> TransferUtil.INSTANCE.applyProductName(List.of(cameraWarn),
                                () -> QueryDeviceBaseInfoParam.builder()
                                        .deviceTokens(Set.of(deviceToken))
                                        .companyID(param.getCompanyID()).build(),
                                WtWarnDetailInfo::getDeviceToken,
                                WtWarnDetailInfo::setDeviceTypeName));
                return FieldShowUtil.dealFieldShow(cameraWarn);
            case DANGEROUS:
            case FLOOD:
                return baseMapper.queryDetailByID(param.getWarnID());
            case RAINSTORM:
                WtWarnDetailInfo detail = baseMapper.queryDetailByID(param.getWarnID());
                Optional.ofNullable(projectMapper.selectById(detail.getProjectID())).ifPresent(e -> {
                    Integer city = JsonUtil.get(e.getLocation(), "city", Integer.class);
                    if (city != null && redisService.hasKey(RedisKeys.REGION_AREA_KEY, city.toString())) {
                        RegionArea area = redisService.get(RedisKeys.REGION_AREA_KEY, city.toString(), RegionArea.class);
                        detail.setWarnAreaCode(city);
                        detail.setWarnArea(area.getName());
                        detail.setWarnAreaLocation(area.getLng() + StrUtil.COMMA + area.getLat());
                        detail.setIssueUnit(area.getName() + METEOROLOGICAL_OBSERVATORY);
                    }
                });
                return detail;
        }
        return null;
    }

    @Override
    public PageUtil.Page<WtTerminalWarnLog> queryTerminalWarnPage(QueryWtTerminalWarnLogPageParam param) {
        //按条件查询所有报警记录，再通过 deviceToken 反查 UniqueToken, 进而反查传感器、项目、监测类型、检测项、监测点
        List<WtWarnLogInfo> wtWarnLogInfos = baseMapper.queryTerminalWarnList(param);
        Set<String> deviceTokens = wtWarnLogInfos.stream()
                .map(WtWarnLogInfo::getDeviceToken).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        TransferUtil.INSTANCE.applyDeviceBase(wtWarnLogInfos,
                () -> QueryDeviceBaseInfoParam.builder().deviceTokens(deviceTokens).companyID(param.getCompanyID()).build(),
                WtWarnLogInfo::getDeviceToken,
                (e, device) -> {
                    e.setDeviceTypeName(device.getProductName());
                    e.setUniqueToken(device.getUniqueToken());
                });
        Set<String> uniqueTokenSet = wtWarnLogInfos.stream().map(WtWarnLogInfo::getUniqueToken)
                .filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        if (!uniqueTokenSet.isEmpty()) {
            Map<String, WtTerminalWarnLog> map = baseMapper.queryTerminalWarnListByUniqueToken(param, uniqueTokenSet)
                    .stream().collect(Collectors.toMap(WtTerminalWarnLog::getUniqueToken, e -> e));
            List<WtTerminalWarnLog> result = wtWarnLogInfos.stream().filter(e -> map.containsKey(e.getUniqueToken())).map(item -> {
                        WtTerminalWarnLog log = new WtTerminalWarnLog();
                        BeanUtil.copyProperties(item, log);
                        log.setProjectList(map.get(item.getUniqueToken()).getProjectList());
                        return log;
                    })
                    .filter(e -> StrUtil.isEmpty(param.getQueryCode())
                            || (StrUtil.contains(e.getDeviceToken(), param.getQueryCode()))
                            || (StrUtil.contains(e.getWarnName(), param.getQueryCode()))
                            || e.getProjectList().stream().flatMap(p -> p.getMonitorPointList().stream()).anyMatch(m -> m.getMonitorPointName().contains(param.getQueryCode()))
                            || e.getProjectList().stream().anyMatch(i -> i.getProjectName().contains(param.getQueryCode()))
                    ).toList();
            return PageUtil.page(result, param.getPageSize(), param.getCurrentPage());
        }
        return PageUtil.Page.empty();
    }

    @Override
    public WtTerminalWarnDetailInfo queryTerminalWarnDetail(QueryWtWarnDetailParam param) {
        WtTerminalWarnDetailInfo base = this.baseMapper.queryTerminalWarnDetail(param.getWarnID());
        //使用deviceToken查询设备信息填充deviceTypeName(设备类型名)
        Optional.ofNullable(base.getDeviceToken()).filter(e -> !e.isBlank())
                .ifPresent(deviceToken -> {
                    TransferUtil.INSTANCE.applyDeviceBase(List.of(base),
                            () -> QueryDeviceBaseInfoParam.builder().deviceTokens(Set.of(deviceToken)).companyID(param.getCompanyID()).build(),
                            WtTerminalWarnDetailInfo::getDeviceToken,
                            (e, device) -> {
                                e.setDeviceTypeName(device.getProductName());
                                e.setUniqueToken(device.getUniqueToken());
                            });
                    // Nesting a {@code Optional.of().filter()} that for device exists but not belongs to this company
                    // which cause the RPC querying cannot get the device info
                    Optional.of(base).filter(u -> Objects.nonNull(u.getUniqueToken())).ifPresent(u -> {
                        List<WtTerminalWarnLog> otherInfo = baseMapper.queryTerminalWarnListByUniqueToken(
                                new QueryWtTerminalWarnLogPageParam(), List.of(u.getUniqueToken()));
                        Set<WtTerminalWarnLog.Project> projects = otherInfo.stream().map(WtTerminalWarnLog::getProjectList).findFirst().orElse(Set.of());
                        //从redis中获取regionArea信息填充regionArea(区域名)
                        Set<Object> areaSet = projects.stream().map(WtTerminalWarnLog.Project::getRegionArea).filter(JSONUtil::isTypeJSON)
                                .map(e -> CollUtil.getLast(JSONUtil.parseObj(e).values())).collect(Collectors.toSet());
                        Map<Object, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areaSet, RegionArea.class)
                                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
                        projects.forEach(e -> {
                            if (JSONUtil.isTypeJSON(e.getRegionArea())) {
                                Map<String, Object> regionInfo = JSONUtil.toBean(e.getRegionArea(), Dict.class);
                                Object last = CollUtil.getLast(regionInfo.values());
                                e.setRegionArea(areaMap.getOrDefault(last, null));
                            }
                        });
                        u.setProjectList(projects);
                    });
                });
        return base;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWarnLogBindWarnOrder(AddWarnLogBindWarnOrderParam param) {
        if (workOrderMapper.insertByCondition(param) == 1) {
            this.baseMapper.updateByIdAndWorkOrderID(param.getWarnID(), param.getID());
        }
    }

    @Override
    public WtWarnListResult queryBaseList(QueryWtWarnListParam pa) {
        List<WtWarnLogBase> list = baseMapper.queryBaseList(pa);
        WtWarnListResult result = new WtWarnListResult();

        Set<Object> cityCodes = list.stream().filter(e -> e.getWarnType().equals(WarnType.RAINSTORM) &&
                        e.getProjectID() != null && e.getProjectID() != -1)
                .map(e -> JsonUtil.getStr(e.getProjectArea(), "city"))
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (!cityCodes.isEmpty()) {
            Map<Long, RegionArea> cityCodeMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, cityCodes, RegionArea.class)
                    .stream().collect(Collectors.toMap(RegionArea::getAreaCode, e -> e));

            result.setList(list.stream().map(e -> {
                WtWarnListResult.Item item = WtWarnListResult.Item.of(e);
                if (WarnType.RAINSTORM.equals(e.getWarnType())) {
                    Long city = JsonUtil.get(e.getProjectArea(), "city", Long.class);
                    if (city !=null && cityCodeMap.containsKey(city)) {
                        RegionArea area = cityCodeMap.get(city);
                        item.setWarnAreaCode(city);
                        item.setWarnArea(area.getName());
                        item.setWarnAreaLocation(area.getLng() + StrUtil.COMMA + area.getLat());
                        item.setIssueUnit(area.getName() + METEOROLOGICAL_OBSERVATORY);
                    }
                }
                return item;
            }).toList());
        } else {
            result.setList(list);
        }

        result.setStatistic(list.stream()
                .collect(Collectors.groupingBy(WtWarnLogBase::getWarnLevel, Collectors.counting())));
        return result;
    }

}
