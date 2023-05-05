package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnLogInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnLogServiceImpl extends ServiceImpl<TbWarnLogMapper, TbWarnLog> implements ITbWarnLogService {
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final IotService iotService;
    private final RedisService redisService;

    @Override
    public PageUtil.Page<WtWarnLogInfo> queryByPage(QueryWtWarnLogPageParam param) {
        // 实时记录是测点报警状态最新一条报警数据，并不是指当天的；历史记录是除了最新报警状态的报警数据其他触发报警状态的报警数据
        Integer queryType = param.getQueryType();
        Integer pageSize = param.getPageSize() == 0 ? 1 : param.getPageSize();
        List<WtWarnLogInfo> wtWarnLogInfos;
        Long totalCount = 0L;
        if (queryType == 1) {
            wtWarnLogInfos = this.baseMapper.queryCurrentRecords(param);
            totalCount = this.baseMapper.queryCurrentRecordCount(param);
        } else if (queryType == 2) {
            IPage<WtWarnLogInfo> page = this.baseMapper.queryHistoryRecords(
                    new Page<>(param.getCurrentPage(), pageSize), param);
            wtWarnLogInfos = page.getRecords();
            totalCount = page.getTotal();
        } else {
            wtWarnLogInfos = new ArrayList<>();
        }
        //使用deviceToken查询设备信息填充deviceTypeName(设备类型名)
        TransferUtil.applyDeviceBaseItem(wtWarnLogInfos,
                () -> QueryDeviceBaseInfoParam.builder()
                        .deviceTokens(wtWarnLogInfos.stream().map(WtWarnLogInfo::getDeviceToken).collect(Collectors.toSet()))
                        .companyID(param.getCompanyID()).build(),
                WtWarnLogInfo::getDeviceToken,
                WtWarnLogInfo::setDeviceTypeName);
        return new PageUtil.Page<>(totalCount / pageSize + 1, wtWarnLogInfos, totalCount);
    }

    @Override
    public WtWarnDetailInfo queryDetail(QueryWtWarnDetailParam param) {
        WtWarnDetailInfo detailInfo = this.baseMapper.queryWarnDetail(param.getWarnID());

        //从redis中获取regionArea信息填充regionArea(区域名)
        Optional.ofNullable(detailInfo.getRegionArea()).filter(JSONUtil::isTypeJSON).ifPresent(e -> {
            Map<String, Object> regionInfo = JSONUtil.toBean(e, Dict.class);
            List<RegionArea> regionAreas = redisService.multiGet(RedisKeys.REGION_AREA_KEY, regionInfo.values(), RegionArea.class);
            detailInfo.setRegionArea(regionAreas.stream().map(RegionArea::getName).distinct().collect(Collectors.joining(StrUtil.EMPTY)));
        });

        //使用deviceToken查询设备信息填充deviceTypeName(设备类型名)
        Optional.ofNullable(detailInfo.getDeviceToken()).filter(e -> !e.isBlank())
                .ifPresent(deviceToken -> TransferUtil.applyDeviceBase(List.of(detailInfo),
                        () -> QueryDeviceBaseInfoParam.builder()
                                .deviceTokens(Set.of(deviceToken))
                                .companyID(param.getCompanyID()).build(),
                        WtWarnDetailInfo::getDeviceToken,
                        (e, device) -> e.setDeviceTypeName(device.getProductName())));
        return FieldShowUtil.dealFieldShow(detailInfo);
    }

    @Override
    public PageUtil.Page<WtTerminalWarnLog> queryByPage(QueryWtTerminalWarnLogPageParam param) {
        //按条件查询所有报警记录，再通过 deviceToken 反查 UniqueToken, 进而反查传感器、项目、监测类型、检测项、监测点
        List<WtWarnLogInfo> wtWarnLogInfos = baseMapper.queryTerminalRecords(param, param.getQueryType() == 1);
        Set<String> deviceTokens = wtWarnLogInfos.stream()
                .map(WtWarnLogInfo::getDeviceToken).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        TransferUtil.applyDeviceBase(wtWarnLogInfos,
                () -> QueryDeviceBaseInfoParam.builder().deviceTokens(deviceTokens).companyID(param.getCompanyID()).build(),
                WtWarnLogInfo::getDeviceToken,
                (e, device) -> {
                    e.setDeviceTypeName(device.getProductName());
                    e.setUniqueToken(device.getUniqueToken());
                });
        Set<String> uniqueTokenSet = wtWarnLogInfos.stream().map(WtWarnLogInfo::getUniqueToken)
                .filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        if (!uniqueTokenSet.isEmpty()) {
            Map<String, WtTerminalWarnLog> map = baseMapper.queryTerminalRecordsByUniqueToken(param, uniqueTokenSet)
                    .stream().collect(Collectors.toMap(WtTerminalWarnLog::getUniqueToken, e -> e));
            List<WtTerminalWarnLog> result = wtWarnLogInfos.stream().filter(e -> map.containsKey(e.getUniqueToken())).map(item -> {
                        WtTerminalWarnLog log = new WtTerminalWarnLog();
                        BeanUtil.copyProperties(item, log);
                        log.setProjectList(map.get(item.getUniqueToken()).getProjectList());
                        return log;
                    })
                    .filter(e -> StrUtil.isEmpty(param.getQueryCode())
                            || (StrUtil.equalsAny(param.getQueryCode(), e.getDeviceToken(), e.getWarnName(), e.getWarnContent())
                            || e.getProjectList().stream().anyMatch(p -> param.getQueryCode().equals(p.getProjectName()))
                            || e.getProjectList().stream().flatMap(p -> p.getMonitorPointList().stream()).anyMatch(m -> param.getQueryCode().equals(m.getMonitorPointName())))
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
                    TransferUtil.applyDeviceBase(List.of(base),
                            () -> QueryDeviceBaseInfoParam.builder().deviceTokens(Set.of(deviceToken)).companyID(param.getCompanyID()).build(),
                            WtTerminalWarnDetailInfo::getDeviceToken,
                            (e, device) -> {
                                e.setDeviceTypeName(device.getProductName());
                                e.setUniqueToken(device.getUniqueToken());
                            });
                    List<WtTerminalWarnLog> otherInfo = baseMapper.queryTerminalRecordsByUniqueToken(new QueryWtTerminalWarnLogPageParam(), List.of(base.getUniqueToken()));
                    Set<WtTerminalWarnLog.Project> projects = otherInfo.stream().map(WtTerminalWarnLog::getProjectList).findFirst().orElse(Set.of());
                    //从redis中获取regionArea信息填充regionArea(区域名)
                    Set<Object> areaSet = projects.stream().map(WtTerminalWarnLog.Project::getRegionArea).filter(JSONUtil::isTypeJSON)
                            .flatMap(e -> JSONUtil.toBean(e, Dict.class).values().stream()).collect(Collectors.toSet());
                    Map<Object, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areaSet, RegionArea.class)
                            .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
                    projects.forEach(e -> {
                        if (JSONUtil.isTypeJSON(e.getRegionArea())) {
                            Map<String, Object> regionInfo = JSONUtil.toBean(e.getRegionArea(), Dict.class);
                            e.setRegionArea(regionInfo.values().stream().filter(areaMap::containsKey).map(areaMap::get).distinct().collect(Collectors.joining()));
                        }
                    });
                    base.setProjectList(projects);
                });
        return base;
    }

}
