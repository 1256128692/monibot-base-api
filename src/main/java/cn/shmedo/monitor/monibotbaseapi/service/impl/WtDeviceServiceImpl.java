package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceState;
import cn.shmedo.monitor.monibotbaseapi.model.enums.IntelDeviceType4Location;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceStateParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DeviceStateResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.Device4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.DeviceDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.WtVideoPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.SensorWithMore;
import cn.shmedo.monitor.monibotbaseapi.service.WtDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 水利设备服务
 *
 * @author Chengfs on 2023/4/28
 */
@Component
@AllArgsConstructor
public class WtDeviceServiceImpl implements WtDeviceService {

    private final IotService iotService;
    private final TbSensorMapper tbSensorMapper;
    private final TbWarnRuleMapper tbWarnRuleMapper;
    private final TbWarnLogMapper tbWarnLogMapper;
    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final TbMonitorPointMapper monitorPointMapper;

    private final RedisService redisService;

    private final TbDeviceIntelLocationMapper tbDeviceIntelLocationMapper;

    @Override
    public Collection<ProductSimple> productSimpleList(QueryProductSimpleParam param) {
        Collection<Integer> projectList = PermissionUtil.getHavePermissionProjectList(param.getCompanyID());
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(param.getCompanyID())
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(projectList.stream().map(String::valueOf).toList())
                .sendEnable(param.getIsEnable())
                .deviceToken(param.getDeviceToken())
                .build();
        ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);
        return result.apiSuccess() ?
                result.getData().stream().map(e -> ProductSimple.builder()
                        .productID(e.getProductID())
                        .productName(e.getProductName()).build()).collect(Collectors.toSet()) :
                Collections.emptyList();
    }

    @Override
    public PageUtil.PageWithMap<Device4Web> queryWtDevicePageList(QueryWtDevicePageListParam pa) {
        PageUtil.PageWithMap<Device4Web> empty = PageUtil.PageWithMap.empty();
        List<Integer> projectIDList =
                pa.getProjectInfos().stream().map(TbProjectInfo::getID).toList();
        if (StringUtils.isNotBlank(pa.getAreaCode())) {
            projectIDList =
                    tbProjectInfoMapper.selectList(
                            new QueryWrapper<TbProjectInfo>().lambda()
                                    .in(TbProjectInfo::getID, projectIDList).like(TbProjectInfo::getLocation, pa.getAreaCode())
                    ).stream().map(TbProjectInfo::getID).toList();
        }
        if (CollectionUtils.isEmpty(projectIDList)) {
            return empty;
        }
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(pa.getCompanyID())
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(projectIDList.stream().map(String::valueOf).toList())
                .sendEnable(true)
                .deviceToken(null)
                .online(pa.getOnline())
                .productID(pa.getProductID())
                .build();
        ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);
        if (!result.apiSuccess()) {
            throw new CustomBaseException(result.getCode(), result.getMsg());
        }
        List<SimpleDeviceV5> allData = result.getData();
        if (CollectionUtils.isEmpty(allData)) {
            return empty;
        }

        if (pa.getRuleID() != null) {
            boolean flag = pa.getSelect() == null || pa.getSelect();
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(pa.getRuleID());
            if (tbWarnRule != null && tbWarnRule.getProductID() != null) {
                Integer productID = tbWarnRule.getProductID();
                List<Integer> deviceIDList = StringUtils.isBlank(tbWarnRule.getDeviceCSV()) || tbWarnRule.getDeviceCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).map(Integer::valueOf).toList();
                allData = allData.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getProductID().equals(productID)) {
                                    return false;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getDeviceCSV())) {
                                    return false;
                                }
                                if (tbWarnRule.getDeviceCSV().equals("all")) {
                                    return true;
                                }
                                if (CollectionUtils.isEmpty(deviceIDList)) {
                                    return false;
                                } else if (deviceIDList.contains(e.getDeviceID())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                if (!e.getProductID().equals(productID)) {
                                    return true;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getDeviceCSV())) {
                                    return true;
                                }
                                if (tbWarnRule.getDeviceCSV().equals("all")) {
                                    return false;
                                }
                                if (CollectionUtils.isEmpty(deviceIDList)) {
                                    return true;
                                } else if (deviceIDList.contains(e.getDeviceID())) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                ).toList();
            }
        }
        if (CollectionUtils.isEmpty(allData)) {
            return empty;
        }
        Collection<String> uniqueTokens = allData.stream().map(SimpleDeviceV5::getUniqueToken).collect(Collectors.toSet());

        List<SensorWithMore> sensorWithMores = tbSensorMapper.querySensorWithMoreBy(uniqueTokens, pa.getCompanyID(), projectIDList, null);
        Map<String, List<SensorWithMore>> map = sensorWithMores.stream().collect(Collectors.groupingBy(SensorWithMore::getUniqueToken));
        Map<Integer, TbProjectInfo> projectInfoMap = tbProjectInfoMapper.selectAll().stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        if (pa.getMonitorItemID() != null) {
            allData = allData.stream().filter(
                    item -> {
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> item2.getMonitorItemID() != null && item2.getMonitorItemID().equals(pa.getMonitorItemID())
                            )) {
                                return true;
                            }
                        }
                        return false;
                    }
            ).toList();
        }
        if (CollectionUtils.isEmpty(allData)) {
            return empty;
        }
        if (StringUtils.isNotBlank(pa.getMonitorItemName())) {
            allData = allData.stream().filter(
                    item -> {
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> StringUtils.isNotBlank(item2.getMonitorItemName())
                                            && item2.getMonitorItemName().contains(pa.getMonitorItemName())
                                            || StringUtils.isNotBlank(item2.getMonitorItemAlias())
                                            && item2.getMonitorItemAlias().contains(pa.getMonitorItemName())
                            )) {
                                return true;
                            }
                        }
                        return false;
                    }
            ).toList();
        }
        if (CollectionUtils.isEmpty(allData)) {
            return empty;
        }
        if (pa.getMonitorPointID() != null) {
            allData = allData.stream().filter(item -> {
                if (map.containsKey(item.getUniqueToken())) {
                    return map.get(item.getUniqueToken()).stream().anyMatch(
                            item2 -> item2.getMonitorPointID() != null && item2.getMonitorPointID().equals(pa.getMonitorPointID())
                    );
                }
                return false;
            }).toList();
            if (CollectionUtils.isEmpty(allData)) {
                return empty;
            }
        }
        if (StringUtils.isNotBlank(pa.getQueryCode())) {
            // 支持模糊查询设备SN/工程名称/监测点名称
            allData.forEach(item -> {
                item.setProjectIDList(item.getSendAddressList().stream().map(Integer::parseInt).toList());
            });
            allData = allData.stream().filter(
                    item -> {
                        if (item.getDeviceToken().contains(pa.getQueryCode())) {
                            return true;
                        }
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> StringUtils.isNotBlank(item2.getMonitorPointName()) && item2.getMonitorPointName().contains(pa.getQueryCode())
                            )) {
                                return true;
                            }
                        }
                        if (
                                item.getProjectIDList().stream().anyMatch(
                                        pid -> projectInfoMap.containsKey(pid) && projectInfoMap.get(pid).getProjectName().contains(pa.getQueryCode())
                                )
                        ) {
                            return true;
                        }
                        return false;
                    }
            ).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(allData)) {
            return empty;
        }
        List<String> warnDeviceTokenList = tbWarnLogMapper.selectList(
                new QueryWrapper<TbWarnLog>().lambda()
                        .ge(TbWarnLog::getWarnTime, DateUtil.offsetDay(new Date(), -2))
                        .isNotNull(TbWarnLog::getDeviceToken)

        ).stream().map(TbWarnLog::getDeviceToken).distinct().toList();
        long warnBefore = allData.stream().filter(e -> warnDeviceTokenList.contains(e.getDeviceToken())).count();
        long normalBefore = allData.size() - warnBefore;
        if (pa.getStatus() != null) {
            // 过滤 0.正常 1.异常

            if (pa.getStatus() == 0) {
                allData = allData.stream().filter(e -> !warnDeviceTokenList.contains(e.getDeviceToken())).toList();
            } else {
                allData = allData.stream().filter(e -> warnDeviceTokenList.contains(e.getDeviceToken())).toList();
            }

        }
        if (CollectionUtils.isEmpty(allData)) {
            return new PageUtil.PageWithMap<>(0, Collections.emptyList(), 0, Map.of("warnBefore", warnBefore, "normalBefore", normalBefore));
        }
        allData = allData.stream().sorted(Comparator.comparingInt(SimpleDeviceV5::getDeviceID).reversed()).toList();
        // 计算在线数量
        long onlineCount = allData.stream().filter(e -> e.getOnlineStatus() != null && e.getOnlineStatus()).count();
        PageUtil.Page<SimpleDeviceV5> page = PageUtil.page(allData, pa.getPageSize(), pa.getCurrentPage());
        page.currentPageData().forEach(
                item -> {
                    item.setProjectIDList(
                            item.getSendAddressList().stream().map(Integer::valueOf).toList()
                    );
                }
        );
        List<Device4Web> collect = page.currentPageData().stream().map(item -> {
            Device4Web device4Web = Device4Web.builder().deviceSN(item.getDeviceToken())
                    .firewallVersion(item.getFirmwareVersion())
                    .productID(item.getProductID())
                    .productName(item.getProductName())
                    .online(item.getOnlineStatus())
                    .lastActiveTime(item.getLastActiveTime())
                    .createTime(item.getCreateTime())
                    .status(null)
                    .deviceID(item.getDeviceID())
                    .build();
            device4Web.setProjectList(
                    item.getProjectIDList().stream().filter(projectInfoMap::containsKey).map(
                            projectID -> {
                                TbProjectInfo tbProjectInfo = projectInfoMap.get(projectID);
                                return Device4Web.Porject.builder().projectID(tbProjectInfo.getID())
                                        .projectName(tbProjectInfo.getProjectName())
                                        .projectShortName(tbProjectInfo.getShortName())
                                        .location(tbProjectInfo.getLocation())
                                        .projectAddress(tbProjectInfo.getProjectAddress())
                                        .build();
                            }
                    ).toList()
            );
            if (map.containsKey(item.getUniqueToken())) {
                Map<Integer, List<Device4Web.MonitorPoint>> pidMap = map.get(item.getUniqueToken()).stream().collect(Collectors.groupingBy(SensorWithMore::getProjectID, Collectors.mapping(sensorWithMore -> Device4Web.MonitorPoint.builder()
                        .monitorPointID(sensorWithMore.getMonitorPointID())
                        .monitorPointName(sensorWithMore.getMonitorPointName())
                        .pointGpsLocation(sensorWithMore.getPointGpsLocation())
                        .pointImageLocation(sensorWithMore.getPointImageLocation())
                        .monitorItemID(sensorWithMore.getMonitorItemID())
                        .monitorItemName(sensorWithMore.getMonitorItemName())
                        .monitorItemAlias(sensorWithMore.getMonitorItemAlias())
                        .build(), Collectors.toList())));
                device4Web.getProjectList().forEach(
                        project -> {
                            if (pidMap.containsKey(project.getProjectID())) {
                                project.setMonitorPointList(pidMap.get(project.getProjectID()));
                            }
                        }
                );
            }
            return device4Web;
        }).toList();
        collect.forEach(
                item -> item.setStatus(
                        warnDeviceTokenList.contains(item.getDeviceSN()) ? 1 : 0
                )
        );
        return new PageUtil.PageWithMap<>(page.totalPage(), collect, page.totalCount(),
                Map.of("warnBefore", warnBefore, "normalBefore", normalBefore, "onlineCount", onlineCount));
    }


    @Override
    public PageUtil.PageWithMap<WtVideoPageInfo> queryWtVideoPageList(QueryWtVideoPageParam param) {


        Long totalCount = 0L;
        Integer pageSize = param.getPageSize() == 0 ? 1 : param.getPageSize();

        List<WtVideoPageInfo> wtVideoList = monitorPointMapper.selectVideoPointListByCondition(param.getProjectIDList(),
                param.getOnlineInt(), null, param.getAreaCode(), param.getMonitorItemID(), param.getMonitorItemName(), MonitorType.VIDEO.getKey(),
                param.getVideoType(), param.getMonitorPointID());

        if (CollectionUtil.isNullOrEmpty(wtVideoList)) {
            return PageUtil.PageWithMap.empty();
        }
        long normalBefore = wtVideoList.stream().filter(e -> e.getStatus() != null && e.getStatus() == 0).count();
        long warnBefore = wtVideoList.stream().filter(e -> e.getStatus() != null && e.getStatus() == 1).count();
        PageUtil.PageWithMap<WtVideoPageInfo> empty = PageUtil.PageWithMap.emptyWithMap(Map.of("warnBefore", warnBefore, "normalBefore", normalBefore));
        if (param.getStatus() != null) {
            wtVideoList = wtVideoList.stream().filter(e -> e.getStatus() != null && e.getStatus().equals(param.getStatus())).toList();
        }
        if (CollectionUtil.isNullOrEmpty(wtVideoList)) {
            return empty;
        }
        wtVideoList.forEach(item -> {
            String exValues = item.getExValues();
            if (StringUtils.isNotEmpty(exValues)) {
                Dict dict = JSONUtil.toBean(exValues, Dict.class);
                if (dict.get("seqNo") != null) {
                    item.setVideoSN(dict.get("seqNo").toString());
                }
                if (dict.get("videoType") != null) {
                    item.setVideoType(dict.get("videoType").toString());
                }
                if (item.getStatus() != null) {
                    item.setOnline(item.getStatus() == 0);
                }
            }
            if (StringUtils.isNotEmpty(item.getLocation())) {
                if (JSONUtil.isTypeJSON(item.getLocation())) {
                    JSONObject json = JSONUtil.parseObj(item.getLocation());
                    item.setLocationInfo(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
                }
            }
        });

        Collection<Object> areas = wtVideoList
                .stream().map(WtVideoPageInfo::getLocationInfo).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
        areas.clear();

        // 过滤出监测点下存在设备的数据
        List<WtVideoPageInfo> resultList = wtVideoList.stream()
                .peek(item -> item.setLocationInfo(areaMap.getOrDefault(item.getLocationInfo(), null)))
                .filter(pojo -> pojo.getVideoSN() != null && !pojo.getVideoSN().equals(""))
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(resultList)) {
            return empty;
        }
        totalCount = Long.valueOf(resultList.size());

        if (!StringUtil.isNullOrEmpty(param.getQueryCode())) {
            // 设备SN || 工程名称 || 监测点
            String queryCode = param.getQueryCode();
            resultList = resultList.stream()
                    .filter(info -> (info.getVideoSN() != null && !info.getVideoSN().isEmpty() && info.getVideoSN().contains(queryCode))
                            || (info.getMonitorPointName().contains(queryCode))
                            || (info.getProjectName().contains(queryCode)))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNullOrEmpty(resultList)) {
                return empty;
            }
            totalCount = Long.valueOf(resultList.size());
        }

        // 过滤出规则引擎 所拥有的设备
        if (param.getRuleID() != null) {
            boolean flag = param.getSelect() == null || param.getSelect();
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(param.getRuleID());
            if (tbWarnRule != null && StringUtils.isNotBlank(tbWarnRule.getVideoType())) {
                String videoType = tbWarnRule.getVideoType();
                List<String> videoSNList = StringUtils.isBlank(tbWarnRule.getVideoCSV()) || tbWarnRule.getVideoCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getVideoCSV().split(",")).toList();
                resultList = resultList.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getVideoType().equals(videoType)) {
                                    return false;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getVideoCSV())) {
                                    return false;
                                }
                                if (tbWarnRule.getVideoCSV().equals("all")) {
                                    return true;
                                }
                                if (CollectionUtils.isEmpty(videoSNList)) {
                                    return false;
                                } else if (videoSNList.contains(e.getVideoSN())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                if (!e.getVideoType().equals(videoType)) {
                                    return true;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getVideoCSV())) {
                                    return true;
                                }
                                if (tbWarnRule.getVideoCSV().equals("all")) {
                                    return false;
                                }
                                if (CollectionUtils.isEmpty(videoSNList)) {
                                    return true;
                                } else if (videoSNList.contains(e.getVideoSN())) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                ).toList();
            }
        }
        if (CollectionUtils.isEmpty(resultList)) {
            return empty;
        }
        // 统计在线数量
        long onlineCount = resultList.stream().filter(e -> e.getOnline() != null && e.getOnline()).count();
        totalCount = Long.valueOf(resultList.size());
        List<List<WtVideoPageInfo>> lists = CollectionUtil.seperatorList(resultList, param.getPageSize());
        return new PageUtil.PageWithMap<WtVideoPageInfo>(totalCount / pageSize + 1, lists.get(param.getCurrentPage() - 1), totalCount,
                Map.of("warnBefore", warnBefore, "normalBefore", normalBefore, "onlineCount", onlineCount));

    }

    @Override
    public List<Device4Web> exportWtDevice(ExportWtDeviceParam pa) {
        List<Integer> projectIDList =
                pa.getProjectInfos().stream().map(TbProjectInfo::getID).toList();
        if (StringUtils.isNotBlank(pa.getAreaCode())) {
            projectIDList =
                    tbProjectInfoMapper.selectList(
                            new QueryWrapper<TbProjectInfo>().lambda()
                                    .in(TbProjectInfo::getID, projectIDList).like(TbProjectInfo::getLocation, pa.getAreaCode())
                    ).stream().map(TbProjectInfo::getID).toList();
        }
        if (CollectionUtils.isEmpty(projectIDList)) {
            return List.of();
        }
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(pa.getCompanyID())
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(projectIDList.stream().map(String::valueOf).toList())
                .sendEnable(true)
                .deviceToken(null)
                .online(pa.getOnline())
                .productID(pa.getProductID())
                .build();
        ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);
        if (!result.apiSuccess()) {
            throw new CustomBaseException(result.getCode(), result.getMsg());
        }
        List<SimpleDeviceV5> allData = result.getData();
        if (CollectionUtils.isEmpty(allData)) {
            return List.of();
        }
        List<String> warnDeviceTokenList = tbWarnLogMapper.selectList(
                new QueryWrapper<TbWarnLog>().lambda()
                        .ge(TbWarnLog::getWarnTime, DateUtil.offsetDay(new Date(), -2))
                        .isNotNull(TbWarnLog::getDeviceToken)

        ).stream().map(TbWarnLog::getDeviceToken).distinct().toList();
        if (pa.getStatus() != null) {
            // 过滤 0.正常 1.异常

            if (pa.getStatus() == 0) {
                allData = allData.stream().filter(e -> !warnDeviceTokenList.contains(e.getDeviceToken())).toList();
            } else {
                allData = allData.stream().filter(e -> warnDeviceTokenList.contains(e.getDeviceToken())).toList();
            }

        }
        if (CollectionUtils.isEmpty(allData)) {
            return List.of();
        }
        if (pa.getRuleID() != null) {
            boolean flag = pa.getSelect() == null || pa.getSelect();
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(pa.getRuleID());
            if (tbWarnRule != null && tbWarnRule.getProductID() != null) {
                Integer productID = tbWarnRule.getProductID();
                List<Integer> deviceIDList = StringUtils.isBlank(tbWarnRule.getDeviceCSV()) || tbWarnRule.getDeviceCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).map(Integer::valueOf).toList();
                allData = allData.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getProductID().equals(productID)) {
                                    return false;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getDeviceCSV())) {
                                    return false;
                                }
                                if (tbWarnRule.getDeviceCSV().equals("all")) {
                                    return true;
                                }
                                if (CollectionUtils.isEmpty(deviceIDList)) {
                                    return false;
                                } else if (deviceIDList.contains(e.getDeviceID())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                if (!e.getProductID().equals(productID)) {
                                    return true;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getDeviceCSV())) {
                                    return true;
                                }
                                if (tbWarnRule.getDeviceCSV().equals("all")) {
                                    return false;
                                }
                                if (CollectionUtils.isEmpty(deviceIDList)) {
                                    return true;
                                } else if (deviceIDList.contains(e.getDeviceID())) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                ).toList();
            }
        }
        if (CollectionUtils.isEmpty(allData)) {
            return List.of();
        }
        Collection<String> uniqueTokens = allData.stream().map(SimpleDeviceV5::getUniqueToken).collect(Collectors.toSet());

        List<SensorWithMore> sensorWithMores = tbSensorMapper.querySensorWithMoreBy(uniqueTokens, pa.getCompanyID(), projectIDList, null);
        Map<String, List<SensorWithMore>> map = sensorWithMores.stream().collect(Collectors.groupingBy(SensorWithMore::getUniqueToken));
        Map<Integer, TbProjectInfo> projectInfoMap = tbProjectInfoMapper.selectAll().stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        if (pa.getMonitorItemID() != null) {
            allData = allData.stream().filter(
                    item -> {
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> item2.getMonitorItemID() != null && item2.getMonitorItemID().equals(pa.getMonitorItemID())
                            )) {
                                return true;
                            }
                        }
                        return false;
                    }
            ).toList();
        }
        if (CollectionUtils.isEmpty(allData)) {
            return List.of();
        }
        if (StringUtils.isNotBlank(pa.getMonitorItemName())) {
            allData = allData.stream().filter(
                    item -> {
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> StringUtils.isNotBlank(item2.getMonitorItemName())
                                            && item2.getMonitorItemName().contains(pa.getMonitorItemName())
                                            || StringUtils.isNotBlank(item2.getMonitorItemAlias())
                                            && item2.getMonitorItemAlias().contains(pa.getMonitorItemName())
                            )) {
                                return true;
                            }
                        }
                        return false;
                    }
            ).toList();
        }
        if (CollectionUtils.isEmpty(allData)) {
            return List.of();
        }
        if (StringUtils.isNotBlank(pa.getQueryCode())) {
            // 支持模糊查询设备SN/工程名称/监测点名称
            allData.forEach(item -> {
                item.setProjectIDList(item.getSendAddressList().stream().map(Integer::parseInt).toList());
            });
            allData = allData.stream().filter(
                    item -> {
                        if (item.getDeviceToken().contains(pa.getQueryCode())) {
                            return true;
                        }
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> StringUtils.isNotBlank(item2.getMonitorPointName()) && item2.getMonitorPointName().contains(pa.getQueryCode())
                            )) {
                                return true;
                            }
                        }
                        if (
                                item.getProjectIDList().stream().anyMatch(
                                        pid -> projectInfoMap.containsKey(pid) && projectInfoMap.get(pid).getProjectName().contains(pa.getQueryCode())
                                )
                        ) {
                            return true;
                        }
                        return false;
                    }
            ).collect(Collectors.toList());
        }
        allData = allData.stream().sorted(Comparator.comparingInt(SimpleDeviceV5::getDeviceID).reversed()).toList();
        allData.forEach(
                item -> {
                    item.setProjectIDList(
                            item.getSendAddressList().stream().map(Integer::valueOf).toList()
                    );
                }
        );
        List<Device4Web> collect = allData.stream().map(item -> {
            Device4Web device4Web = Device4Web.builder().deviceSN(item.getDeviceToken())
                    .firewallVersion(item.getFirmwareVersion())
                    .productID(item.getProductID())
                    .productName(item.getProductName())
                    .online(item.getOnlineStatus())
                    .lastActiveTime(item.getLastActiveTime())
                    .createTime(item.getCreateTime())
                    .status(null)
                    .deviceID(item.getDeviceID())
                    .build();
            device4Web.setProjectList(
                    item.getProjectIDList().stream().filter(projectInfoMap::containsKey).map(
                            projectID -> {
                                TbProjectInfo tbProjectInfo = projectInfoMap.get(projectID);
                                return Device4Web.Porject.builder().projectID(tbProjectInfo.getID())
                                        .projectName(tbProjectInfo.getProjectName())
                                        .projectShortName(tbProjectInfo.getShortName())
                                        .location(tbProjectInfo.getLocation())
                                        .projectAddress(tbProjectInfo.getProjectAddress())
                                        .build();
                            }
                    ).toList()
            );
            if (map.containsKey(item.getUniqueToken())) {
                Map<Integer, List<Device4Web.MonitorPoint>> pidMap = map.get(item.getUniqueToken()).stream().collect(Collectors.groupingBy(SensorWithMore::getProjectID, Collectors.mapping(sensorWithMore -> Device4Web.MonitorPoint.builder()
                        .monitorPointID(sensorWithMore.getMonitorPointID())
                        .monitorPointName(sensorWithMore.getMonitorPointName())
                        .pointGpsLocation(sensorWithMore.getPointGpsLocation())
                        .pointImageLocation(sensorWithMore.getPointImageLocation())
                        .monitorItemID(sensorWithMore.getMonitorItemID())
                        .monitorItemName(sensorWithMore.getMonitorItemName())
                        .monitorItemAlias(sensorWithMore.getMonitorItemAlias())
                        .build(), Collectors.toList())));
                device4Web.getProjectList().forEach(
                        project -> {
                            if (pidMap.containsKey(project.getProjectID())) {
                                project.setMonitorPointList(pidMap.get(project.getProjectID()));
                            }
                        }
                );
            }
            return device4Web;
        }).toList();
        collect.forEach(
                item -> item.setStatus(
                        warnDeviceTokenList.contains(item.getDeviceSN()) ? 1 : 0
                )
        );
        return collect;
    }

    @Override
    public List<WtVideoPageInfo> exportWtVideo(ExportWtVideoParam param) {


        List<WtVideoPageInfo> wtVideoList = monitorPointMapper.selectVideoPointListByCondition(param.getProjectIDList(),
                param.getOnlineInt(), param.getStatus(), param.getAreaCode(), param.getMonitorItemID(), param.getMonitorItemName(), MonitorType.VIDEO.getKey(),
                param.getVideoType(), null);
        if (CollectionUtil.isNullOrEmpty(wtVideoList)) {
            return List.of();
        }

        wtVideoList.forEach(item -> {
            String exValues = item.getExValues();
            if (StringUtils.isNotEmpty(exValues)) {
                Dict dict = JSONUtil.toBean(exValues, Dict.class);
                if (dict.get("seqNo") != null) {
                    item.setVideoSN(dict.get("seqNo").toString());
                }
                if (dict.get("videoType") != null) {
                    item.setVideoType(dict.get("videoType").toString());
                }
                if (item.getStatus() != null) {
                    item.setOnline(item.getStatus() == 0);
                }
            }
            if (StringUtils.isNotEmpty(item.getLocation())) {
                if (JSONUtil.isTypeJSON(item.getLocation())) {
                    JSONObject json = JSONUtil.parseObj(item.getLocation());
                    item.setLocationInfo(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
                }
            }
        });

        Collection<Object> areas = wtVideoList
                .stream().map(WtVideoPageInfo::getLocationInfo).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> areaMap = redisService.multiGet(RedisKeys.REGION_AREA_KEY, areas, RegionArea.class)
                .stream().collect(Collectors.toMap(e -> e.getAreaCode().toString(), RegionArea::getName));
        areas.clear();

        // 过滤出监测点下存在设备的数据
        List<WtVideoPageInfo> resultList = wtVideoList.stream()
                .peek(item -> item.setLocationInfo(areaMap.getOrDefault(item.getLocationInfo(), null)))
                .filter(pojo -> pojo.getVideoSN() != null && !pojo.getVideoSN().equals(""))
                .collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(resultList)) {
            return List.of();
        }

        if (!StringUtil.isNullOrEmpty(param.getQueryCode())) {
            // 设备SN || 工程名称 || 监测点
            String queryCode = param.getQueryCode();
            resultList = resultList.stream()
                    .filter(info -> (info.getVideoSN() != null && !info.getVideoSN().isEmpty() && info.getVideoSN().contains(queryCode))
                            || (info.getMonitorPointName().contains(queryCode))
                            || (info.getProjectName().contains(queryCode)))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNullOrEmpty(resultList)) {
                return List.of();
            }
        }

        // 过滤出规则引擎 所拥有的设备
        if (param.getRuleID() != null) {
            boolean flag = param.getSelect() == null || param.getSelect();
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(param.getRuleID());
            if (tbWarnRule != null && StringUtils.isNotBlank(tbWarnRule.getVideoType())) {
                String videoType = tbWarnRule.getVideoType();
                List<String> videoSNList = StringUtils.isBlank(tbWarnRule.getVideoCSV()) || tbWarnRule.getVideoCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getVideoCSV().split(",")).toList();
                resultList = resultList.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getVideoType().equals(videoType)) {
                                    return false;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getVideoCSV())) {
                                    return false;
                                }
                                if (tbWarnRule.getVideoCSV().equals("all")) {
                                    return true;
                                }
                                if (CollectionUtils.isEmpty(videoSNList)) {
                                    return false;
                                } else if (videoSNList.contains(e.getVideoSN())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                if (!e.getVideoType().equals(videoType)) {
                                    return true;
                                }
                                if (StringUtils.isBlank(tbWarnRule.getVideoCSV())) {
                                    return false;
                                }
                                if (tbWarnRule.getVideoCSV().equals("all")) {
                                    return false;
                                }
                                if (CollectionUtils.isEmpty(videoSNList)) {
                                    return true;
                                } else if (videoSNList.contains(e.getVideoSN())) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                ).toList();
            }
        }

        return resultList;
    }

    @Override
    public Object queryWtVideoTypeList(QueryWtVideoTypeParam param) {

        List<WtVideoPageInfo> wtVideoList = monitorPointMapper.selectVideoPointListByCondition(param.getProjectIDList(),
                null, null, null, null, null, MonitorType.VIDEO.getKey(), null, null);
        if (CollectionUtil.isNullOrEmpty(wtVideoList)) {
            return null;
        }
        Set<String> videoTypeList = new HashSet<>();
        wtVideoList.forEach(item -> {
            String exValues = item.getExValues();
            if (StringUtils.isNotEmpty(exValues)) {
                Dict dict = JSONUtil.toBean(exValues, Dict.class);
                if (dict.get("videoType") != null) {
                    videoTypeList.add(dict.get("videoType").toString());
                }
            }
        });

        return videoTypeList;
    }

    @Override
    public DeviceDetail deviceDetail(QueryDeviceDetailParam param) {
        DeviceDetail result = BeanUtil.copyProperties(param.getDevice(), DeviceDetail.class);
        result.setState(new DeviceState());

        //构建参数查询设备状态和扩展状态
        QueryDeviceStateParam stateParam = QueryDeviceStateParam.builder()
                .deviceID(param.getDevice().getDeviceID()).build();
        ResultWrapper<DeviceStateResponse> stateWrapper = iotService.queryDeviceState(stateParam);
        if (stateWrapper.apiSuccess() && stateWrapper.getData() != null) {
            BeanUtil.copyProperties(stateWrapper.getData(), result.getState(), CopyOptions.create().ignoreNullValue());
        }

        ResultWrapper<DeviceStateResponse> expandWrapper = iotService.queryDeviceExpandState(stateParam);
        if (expandWrapper.apiSuccess() && expandWrapper.getData() != null) {
            BeanUtil.copyProperties(expandWrapper.getData(), result.getState(), CopyOptions.create().ignoreNullValue());
        }

        //TODO 设备状态（正常/异常）设备有报警即异常，否则正常；临时默认为在线状态
        result.getState().setStatus(Boolean.TRUE.equals(result.getOnlineStatus()) ? 0 : 1);
        // 设置设备状态
        result.setLocation(
                tbDeviceIntelLocationMapper.selectOne(
                        new LambdaQueryWrapper<TbDeviceIntelLocation>()
                                .eq(TbDeviceIntelLocation::getDeviceToken, param.getDevice().getDeviceToken())
                                .eq(TbDeviceIntelLocation::getType, IntelDeviceType4Location.IOT.getType())
                )
        );
        return result;
    }

    @Override
    public void setIntelDeviceLocationInSys(SetIntelDeviceLocationInSysParam pa, Integer userID) {
        TbDeviceIntelLocation tbDeviceIntelLocation = tbDeviceIntelLocationMapper.selectOne(
                new LambdaQueryWrapper<TbDeviceIntelLocation>()
                        .eq(TbDeviceIntelLocation::getDeviceToken, pa.getDeviceToken())
                        .eq(TbDeviceIntelLocation::getType, pa.getType())
        );
        Date now = new Date();
        if (tbDeviceIntelLocation == null) {

            tbDeviceIntelLocationMapper.insert(
                    TbDeviceIntelLocation.builder()
                            .deviceToken(pa.getDeviceToken())
                            .address(pa.getAddress())
                            .locationJson(pa.getLocationJson())
                            .type(pa.getType())
                            .createTime(now)
                            .updateTime(now)
                            .createUserID(userID)
                            .updateUserID(userID)
                            .build()
            );
        } else {
            tbDeviceIntelLocation.setUpdateTime(now);
            tbDeviceIntelLocation.setAddress(pa.getAddress());
            tbDeviceIntelLocation.setLocationJson(pa.getLocationJson());
            tbDeviceIntelLocation.setUpdateUserID(userID);
            tbDeviceIntelLocationMapper.updateByTypeAndToken(tbDeviceIntelLocation);
        }
    }

}