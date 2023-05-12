package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.Device4Web;
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
    public PageUtil.Page<Device4Web> queryWtDevicePageList(QueryWtDevicePageListParam pa) {
        List<Integer> projectIDList = CollectionUtils.isEmpty(pa.getProjectIDList())
                ? pa.getProjectInfos().stream().map(TbProjectInfo::getID).toList()
                : pa.getProjectIDList();
        if (StringUtils.isNotBlank(pa.getAreaCode())) {
            projectIDList =
                    tbProjectInfoMapper.selectList(
                            new QueryWrapper<TbProjectInfo>().lambda()
                                    .in(TbProjectInfo::getID, projectIDList).like(TbProjectInfo::getLocation, pa.getAreaCode())
                    ).stream().map(TbProjectInfo::getID).toList();
        }
        if (CollectionUtils.isEmpty(projectIDList)) {
            return PageUtil.Page.empty();
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
            return PageUtil.Page.empty();
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
            return PageUtil.Page.empty();
        }
        if (pa.getRuleID() != null) {
            boolean flag = pa.getSelect() == null || pa.getSelect();
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(pa.getRuleID());
            if (tbWarnRule != null && tbWarnRule.getProductID() != null && StringUtils.isNotBlank(tbWarnRule.getDeviceCSV())) {
                Integer productID = tbWarnRule.getProductID();
                List<Integer> deviceIDList = tbWarnRule.getDeviceCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).map(Integer::valueOf).toList();
                allData = allData.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getProductID().equals(productID)) {
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
            return PageUtil.Page.empty();
        }
        Collection<String> uniqueTokens = allData.stream().map(SimpleDeviceV5::getUniqueToken).collect(Collectors.toSet());

        List<SensorWithMore> sensorWithMores = tbSensorMapper.querySensorWithMoreBy(uniqueTokens, pa.getCompanyID(), projectIDList, null);
        Map<String, List<SensorWithMore>> map = sensorWithMores.stream().collect(Collectors.groupingBy(SensorWithMore::getUniqueToken));
        Map<Integer, TbProjectInfo> projectInfoMap = tbProjectInfoMapper.selectBatchIds(projectIDList).stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        if (pa.getMonitorItemID() != null) {
            allData = allData.stream().filter(
                    item -> {
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> item2.getMonitorItemID().equals(pa.getMonitorItemID())
                            )) {
                                return true;
                            }
                        }
                        return false;
                    }
            ).toList();
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
                        } else if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> item2.getMonitorPointName().contains(pa.getQueryCode())
                            )) {
                                return true;
                            }
                        } else if (
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
                    .build();
            device4Web.setProjectList(
                    item.getProjectIDList().stream().map(
                            projectID -> {
                                TbProjectInfo tbProjectInfo = projectInfoMap.get(projectID);
                                if (tbProjectInfo == null) {
                                    return null;
                                }
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
        return new PageUtil.Page<>(page.totalPage(), collect, page.totalCount());
    }


    @Override
    public PageUtil.Page<WtVideoPageInfo> queryWtVideoPageList(QueryWtVideoPageParam param) {

        Long totalCount = 0L;
        Integer pageSize = param.getPageSize() == 0 ? 1 : param.getPageSize();

        List<WtVideoPageInfo> wtVideoList = monitorPointMapper.selectVideoPointListByCondition(param.getProjectIDList(),
                param.getOnlineInt(), param.getStatus(), param.getAreaCode(), param.getMonitorItemID(), MonitorType.VIDEO.getKey(),
                param.getVideoType());
        if (CollectionUtil.isNullOrEmpty(wtVideoList)) {
            return PageUtil.Page.empty();
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
                    item.setOnline(!Boolean.parseBoolean(item.getStatus()));
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
            return PageUtil.Page.empty();
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
                return PageUtil.Page.empty();
            }
            totalCount = Long.valueOf(resultList.size());
        }

        // 过滤出规则引擎 所拥有的设备
        if (param.getRuleID() != null) {
            boolean flag = param.getSelect() == null || param.getSelect();
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(param.getRuleID());
            if (tbWarnRule != null && StringUtils.isNotBlank(tbWarnRule.getVideoType()) && StringUtils.isNotBlank(tbWarnRule.getVideoCSV())) {
                String videoType = tbWarnRule.getVideoType();
                List<String> videoSNList = tbWarnRule.getVideoCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getVideoCSV().split(",")).toList();
                resultList = resultList.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getVideoType().equals(videoType)) {
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
            return PageUtil.Page.empty();
        }

        totalCount = Long.valueOf(resultList.size());
        List<List<WtVideoPageInfo>> lists = CollectionUtil.seperatorList(resultList, param.getPageSize());
        return new PageUtil.Page<WtVideoPageInfo>(totalCount / pageSize + 1, lists.get(param.getCurrentPage() - 1), totalCount);

    }

    @Override
    public List<Device4Web> exportWtDevice(ExportWtDeviceParam pa) {
        List<Integer> projectIDList = CollectionUtils.isEmpty(pa.getProjectIDList())
                ? pa.getProjectInfos().stream().map(TbProjectInfo::getID).toList()
                : pa.getProjectIDList();
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
            if (tbWarnRule != null && tbWarnRule.getProductID() != null && StringUtils.isNotBlank(tbWarnRule.getDeviceCSV())) {
                Integer productID = tbWarnRule.getProductID();
                List<Integer> deviceIDList = tbWarnRule.getDeviceCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).map(Integer::valueOf).toList();
                allData = allData.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getProductID().equals(productID)) {
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
        Map<Integer, TbProjectInfo> projectInfoMap = tbProjectInfoMapper.selectBatchIds(projectIDList).stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        if (pa.getMonitorItemID() != null) {
            allData = allData.stream().filter(
                    item -> {
                        if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> item2.getMonitorItemID().equals(pa.getMonitorItemID())
                            )) {
                                return true;
                            }
                        }
                        return false;
                    }
            ).toList();
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
                        } else if (map.containsKey(item.getUniqueToken())) {
                            if (map.get(item.getUniqueToken()).stream().anyMatch(
                                    item2 -> item2.getMonitorPointName().contains(pa.getQueryCode())
                            )) {
                                return true;
                            }
                        } else if (
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
                    .build();
            device4Web.setProjectList(
                    item.getProjectIDList().stream().map(
                            projectID -> {
                                TbProjectInfo tbProjectInfo = projectInfoMap.get(projectID);
                                if (tbProjectInfo == null) {
                                    return null;
                                }
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
                param.getOnlineInt(), param.getStatus(), param.getAreaCode(), param.getMonitorItemID(), MonitorType.VIDEO.getKey(),
                param.getVideoType());
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
                    item.setOnline(!Boolean.parseBoolean(item.getStatus()));
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
            if (tbWarnRule != null && StringUtils.isNotBlank(tbWarnRule.getVideoType()) && StringUtils.isNotBlank(tbWarnRule.getVideoCSV())) {
                String videoType = tbWarnRule.getVideoType();
                List<String> videoSNList = tbWarnRule.getVideoCSV().equals("all") ? null : Arrays.stream(tbWarnRule.getVideoCSV().split(",")).toList();
                resultList = resultList.stream().filter(
                        e -> {

                            if (flag) {
                                if (!e.getVideoType().equals(videoType)) {
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
                null, null, null, null, MonitorType.VIDEO.getKey(), null);
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

}