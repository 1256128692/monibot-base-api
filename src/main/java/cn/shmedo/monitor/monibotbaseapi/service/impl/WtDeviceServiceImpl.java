package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnRuleMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryProductSimpleParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryWtDevicePageListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.Device4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.SensorWithMore;
import cn.shmedo.monitor.monibotbaseapi.service.WtDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
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
                                    .in(TbProjectInfo::getID, projectIDList).likeLeft(TbProjectInfo::getLocation, pa.getAreaCode())
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
        if (pa.getStatus() != null) {
            // 过滤 0.正常 1.异常
            List<String> strings = tbWarnLogMapper.selectList(
                    new QueryWrapper<TbWarnLog>().lambda()
                            .ge(TbWarnLog::getWarnTime, DateUtil.offsetDay(new Date(), -2))
                            .isNotNull(TbWarnLog::getDeviceToken)

            ).stream().map(TbWarnLog::getDeviceToken).distinct().toList();
            if (pa.getStatus() == 0) {
                allData = allData.stream().filter(e -> !strings.contains(e.getUniqueToken())).toList();
            } else {
                allData = allData.stream().filter(e -> strings.contains(e.getUniqueToken())).toList();
            }

        }
        if (CollectionUtils.isEmpty(allData)) {
            return PageUtil.Page.empty();
        }
        if (pa.getRuleID() != null) {
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(pa.getRuleID());
            if (tbWarnRule != null && tbWarnRule.getProductID() != null && StringUtils.isNotBlank(tbWarnRule.getDeviceCSV())) {
                Integer productID = tbWarnRule.getProductID();
                List<String> SNList = tbWarnRule.getDeviceCSV().equals("ALL") ? null : Arrays.stream(tbWarnRule.getDeviceCSV().split(",")).toList();
                allData = allData.stream().filter(e -> e.getProductID().equals(productID) && (SNList == null || SNList.contains(e.getUniqueToken()))).toList();
            }
        }
        if (CollectionUtils.isEmpty(allData)) {
            return PageUtil.Page.empty();
        }
        Collection<String> uniqueTokens = allData.stream().map(SimpleDeviceV5::getUniqueToken).collect(Collectors.toSet());

        List<SensorWithMore> sensorWithMores = tbSensorMapper.querySensorWithMoreBy(uniqueTokens, pa.getCompanyID(), projectIDList, pa.getMonitorItemID());
        Map<String, List<SensorWithMore>> map = sensorWithMores.stream().collect(Collectors.groupingBy(SensorWithMore::getUniqueToken));
        Map<Integer, TbProjectInfo> projectInfoMap = tbProjectInfoMapper.selectBatchIds(projectIDList).stream().collect(Collectors.toMap(TbProjectInfo::getID, Function.identity()));
        if (StringUtils.isNotBlank(pa.getQueryCode())) {
            // 支持模糊查询设备SN/工程名称/监测点名称
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
                                        pid -> projectInfoMap.get(pid).getProjectName().contains(pa.getQueryCode())
                                )
                        ) {
                            return true;
                        }
                        return false;
                    }
            ).collect(Collectors.toList());
        }
        allData.sort(Comparator.comparing(SimpleDeviceV5::getDeviceID).reversed());
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
                    .status(null)
                    .build();
            device4Web.setProjectList(
                    item.getProjectIDList().stream().map(
                            projectID -> {
                                TbProjectInfo tbProjectInfo = projectInfoMap.get(projectID);
                                return Device4Web.Porject.builder().projectID(tbProjectInfo.getID())
                                        .projectName(tbProjectInfo.getProjectName())
                                        .projectShortName(tbProjectInfo.getShortName())
                                        .location(tbProjectInfo.getLocation())
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
        return new PageUtil.Page<>(page.totalPage(), collect, page.totalCount());
    }
}