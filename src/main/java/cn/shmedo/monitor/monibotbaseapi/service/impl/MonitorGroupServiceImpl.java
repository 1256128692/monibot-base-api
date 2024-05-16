package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.ErrorConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.QueryManualSensorListByMonitorParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.*;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorGroupService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.ParamBuilder;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 16:53
 **/
@Service
@AllArgsConstructor
@Slf4j
public class MonitorGroupServiceImpl implements MonitorGroupService {
    private final TbMonitorGroupMapper tbMonitorGroupMapper;
    private final TbMonitorGroupItemMapper tbMonitorGroupItemMapper;
    private final TbMonitorGroupPointMapper tbMonitorGroupPointMapper;
    private final MdInfoService mdInfoService;
    private final FileService fileService;
    private final TbMonitorItemMapper tbMonitorItemMapper;
    private final TbMonitorPointMapper tbMonitorPointMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMonitorGroup(AddMonitorGroupParam pa, Integer userID) {
        TbMonitorGroup group = Param2DBEntityUtil.fromAddMonitorGroupParam(pa, userID);
        tbMonitorGroupMapper.insert(group);
        if (CollectionUtils.isNotEmpty(pa.getMonitorItemIDList())) {
            List<TbMonitorGroupItem> tbMonitorGroupItemList = pa.getMonitorItemIDList().stream().map(monitorItemID -> TbMonitorGroupItem.builder()
                    .monitorGroupID(group.getID())
                    .monitorItemID(monitorItemID)
                    .build()).collect(Collectors.toList());
            tbMonitorGroupItemMapper.insertBatchSomeColumn(
                    tbMonitorGroupItemList
            );
        }
        if (CollectionUtils.isNotEmpty(pa.getMonitorPointIDList())) {
            List<TbMonitorGroupPoint> tbMonitorGroupPointList = pa.getMonitorPointIDList().stream().map(monitorPointID -> TbMonitorGroupPoint.builder()
                    .monitorGroupID(group.getID())
                    .monitorPointID(monitorPointID)
                    .build()).collect(Collectors.toList());
            tbMonitorGroupPointMapper.insertBatchSomeColumn(
                    tbMonitorGroupPointList
            );
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorGroup(List<Integer> groupIDList) {
        groupIDList.addAll(
                tbMonitorGroupMapper.selectList(
                        new QueryWrapper<TbMonitorGroup>().lambda().in(TbMonitorGroup::getParentID, groupIDList)
                ).stream().map(TbMonitorGroup::getID).toList()
        );
        tbMonitorGroupMapper.deleteBatchIds(groupIDList);
        tbMonitorGroupItemMapper.delete(
                new QueryWrapper<TbMonitorGroupItem>().lambda().in(TbMonitorGroupItem::getMonitorGroupID, groupIDList)
        );
        tbMonitorGroupPointMapper.delete(
                new QueryWrapper<TbMonitorGroupPoint>().lambda().in(TbMonitorGroupPoint::getMonitorGroupID, groupIDList)
        );
    }

    @Override
    public void updateMonitorGroup(UpdateMonitorGroupParam pa, Integer userID) {
        Date now = new Date();
        tbMonitorGroupMapper.updateByPrimaryKey(pa.update(now, userID));
        if (CollectionUtils.isNotEmpty(pa.getMonitorItemIDList())) {
            tbMonitorGroupItemMapper.delete(
                    new QueryWrapper<TbMonitorGroupItem>().lambda().eq(TbMonitorGroupItem::getMonitorGroupID, pa.getGroupID())
            );
            List<TbMonitorGroupItem> tbMonitorGroupItemList = pa.getMonitorItemIDList().stream().map(item -> TbMonitorGroupItem.builder()
                    .monitorGroupID(pa.getGroupID())
                    .monitorItemID(item)
                    .build()).collect(Collectors.toList());
            tbMonitorGroupItemMapper.insertBatchSomeColumn(
                    tbMonitorGroupItemList
            );
        }
        if (CollectionUtils.isNotEmpty(pa.getMonitorPointIDList())) {
            List<Integer> allList = tbMonitorGroupPointMapper.queryPointIDByGroupID(pa.getGroupID());
            // 不需要处理的点
            List<Integer> notDealPintIDList = pa.getMonitorPointIDList().stream().filter(allList::contains).collect(Collectors.toList());
            LambdaQueryWrapper<TbMonitorGroupPoint> deleteQuery = new QueryWrapper<TbMonitorGroupPoint>().lambda().eq(TbMonitorGroupPoint::getMonitorGroupID, pa.getGroupID());
            if (CollectionUtils.isNotEmpty(notDealPintIDList)
            ) {
                deleteQuery.notIn(TbMonitorGroupPoint::getMonitorPointID, notDealPintIDList);
            }
            tbMonitorGroupPointMapper.delete(
                    deleteQuery

            );
            List<TbMonitorGroupPoint> tbMonitorGroupPointList = pa.getMonitorPointIDList().stream()
                    .filter(item -> !allList.contains(item))
                    .map(item -> TbMonitorGroupPoint.builder()
                            .monitorGroupID(pa.getGroupID())
                            .monitorPointID(item)
                            .build()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(tbMonitorGroupPointList)) {
                tbMonitorGroupPointMapper.insertBatchSomeColumn(
                        tbMonitorGroupPointList
                );
            }
        } else {
            // 监测点为空，解绑监测组和之前监测点的绑定关系
            tbMonitorGroupPointMapper.delete(new LambdaQueryWrapper<TbMonitorGroupPoint>()
                    .eq(TbMonitorGroupPoint::getMonitorGroupID, pa.getGroupID()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadMonitorGroupImage(UploadMonitorGroupImageParam pa, Integer userID) {
        AddFileUploadRequest pojo = ParamBuilder.buildAddFileUploadRequest(pa.getImageContent(), pa.getImageSuffix(), userID, pa.getFileName(), pa.getCompanyID());
        ResultWrapper<FilePathResponse> info = mdInfoService.addFileUpload(pojo);
        String path = null;
        if (!info.apiSuccess()) {
            throw new CustomBaseException(info.getCode(), ErrorConstant.IMAGE_INSERT_FAIL);
        } else {
            path = info.getData().getPath();
        }
        tbMonitorGroupMapper.updateImg(path, pa.getGroupID(), userID, new Date());
        if (pa.getCleanLocation() != null && pa.getCleanLocation()) {
            tbMonitorGroupPointMapper.updateLocationByGroupID(null, pa.getGroupID());
        }
        return fileService.getFileUrl(path);
    }

    @Override
    public void configMonitorPointImageLocation(ConfigMonitorPointImageLocationParam pa) {
        tbMonitorGroupPointMapper.updateImgLocation(pa.getPointLocationList(), pa.getGroupID());
    }

    @Override
    public PageUtil.Page<Group4Web> queryMonitorGroupPage(QueryMonitorGroupPageParam pa) {
        Page<Group4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<Group4Web> pageData = tbMonitorGroupMapper.queryPage(page, pa.getProjectID(), pa.getQueryCode(), pa.getMonitorItemID(), true);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return PageUtil.Page.empty();
        }
        List<Integer> groupIDList = pageData.getRecords().stream().map(Group4Web::getID).collect(Collectors.toList());
        List<Group4Web> sonGroupList = tbMonitorGroupMapper.queryGroup4WebByParentIDs(groupIDList);
        // 处理监测项目和监测点
        handleGroup4Web(pageData.getRecords(), sonGroupList);
        // 处理额外字段
        page.getRecords().forEach(item -> {
            item.setGroupID(item.getID());
            item.setGroupName(item.getName());
        });
        sonGroupList.forEach(item -> {
            item.setGroupID(item.getID());
            item.setGroupName(item.getName());
        });
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    /**
     * 处理监测项目和监测点
     *
     * @param parentGroupList 可以为一级或二级
     * @param sonGroupList
     */
    private void handleGroup4Web(List<Group4Web> parentGroupList, List<Group4Web> sonGroupList) {
        List<Integer> allMonitorItemIDList = new ArrayList<>();
        allMonitorItemIDList.addAll(parentGroupList.stream().map(Group4Web::getID).toList());
        allMonitorItemIDList.addAll(sonGroupList.stream().map(Group4Web::getID).toList());
        List<GroupMonitorItem> monitorItems = tbMonitorItemMapper.queryMonitorItemByGroupIDs(allMonitorItemIDList);
//        Map<Integer, List<GroupMonitorItem>> monitorItemMap = monitorItems.stream().collect(Collectors.groupingBy(GroupMonitorItem::getGroupID));

        // 过滤掉 monitorItemID 为 null 的数据
        Map<Integer, List<GroupMonitorItem>> monitorItemMap = monitorItems.stream()
                .filter(item -> item.getMonitorItemID() != null)
                .collect(Collectors.groupingBy(GroupMonitorItem::getGroupID));
        List<GroupPoint> groupPoints = tbMonitorPointMapper.queryGroupPointByGroupIDs(allMonitorItemIDList);
//        Map<Integer, List<GroupPoint>> groupPointMap = groupPoints.stream().collect(Collectors.groupingBy(GroupPoint::getGroupID));

        Map<Integer, List<GroupPoint>> groupPointMap = groupPoints.stream()
                .filter(item -> item.getMonitorItemID() != null)
                .collect(Collectors.groupingBy(GroupPoint::getGroupID));

        sonGroupList.forEach(
                group -> {
                    group.setMonitorPointList(groupPointMap.getOrDefault(group.getID(), Collections.emptyList()));
                    group.setMonitorItemList(monitorItemMap.getOrDefault(group.getID(), Collections.emptyList()));
                });
        parentGroupList.forEach(
                group -> {
                    group.setChildGroupList(sonGroupList.stream().filter(sonGroup -> sonGroup.getParentID().equals(group.getID())).collect(Collectors.toList()));
                    group.setMonitorItemList(monitorItemMap.getOrDefault(group.getID(), Collections.emptyList()));
                    group.setMonitorPointList(groupPointMap.getOrDefault(group.getID(), Collections.emptyList()));

                }
        );
    }

    @Override
    public Group4Web queryMonitorGroupDetail(TbMonitorGroup tbMonitorGroup) {
        Group4Web group4Web = new Group4Web();
        BeanUtil.copyProperties(tbMonitorGroup, group4Web);
        if (StrUtil.isNotBlank(group4Web.getImagePath())) {
            group4Web.setImgURL(fileService.getFileUrl(group4Web.getImagePath()));
        }
        List<Group4Web> sonGroupList = tbMonitorGroupMapper.queryGroup4WebByParentIDs(List.of(group4Web.getID()));
        handleGroup4Web(List.of(group4Web), sonGroupList);

        group4Web.setGroupID(group4Web.getID());
        group4Web.setGroupName(group4Web.getName());

        return group4Web;
    }

    @Override
    public List<Group4Web> queryMonitorGroupList(QueryMonitorGroupListParam pa) {
        List<Group4Web> list = tbMonitorGroupMapper.queryList(pa.getProjectID(), pa.getGroupName(), pa.getSecondaryGroupName(), pa.getMonitorItemID(), true);
        if (CollectionUtils.isEmpty(list)) {
            return List.of();
        }
        List<Integer> groupIDList = list.stream().map(Group4Web::getID).collect(Collectors.toList());
        List<Group4Web> sonGroupList = tbMonitorGroupMapper.queryGroup4WebByParentIDs(groupIDList);
        // 处理监测项目和监测点
        handleGroup4Web(list, sonGroupList);
        // 处理额外字段
        list.forEach(item -> {
            item.setGroupID(item.getID());
            item.setGroupName(item.getName());
        });
        sonGroupList.forEach(item -> {
            item.setGroupID(item.getID());
            item.setGroupName(item.getName());
        });
        return list;
    }

    @Override
    public List<SimpleMonitorInfo> queryMonitorGroupItemNameList(QueryMonitorGroupItemNameListParam pa) {
        return tbMonitorGroupMapper.queryMonitorGroupItemNameList(pa);
    }

    @Override
    public List<MonitorGroupParentBaseInfo> queryProjectGroupInfoList(QueryProjectGroupInfoParam param) {
        return tbMonitorGroupMapper.queryProjectGroupInfoList(param).stream().collect(Collectors.groupingBy(
                ProjectGroupPlainInfo::getMonitorGroupParentID)).values().stream().map(p1 -> p1.stream().findFirst()
                .map(p2 -> MonitorGroupParentBaseInfo.builder().monitorGroupParentID(p2.getMonitorGroupParentID())
                        .monitorGroupParentName(p2.getMonitorGroupParentName()).monitorGroupParentEnable(
                                p2.getMonitorGroupParentEnable()).monitorGroupDataList(p1.stream().collect(
                                        Collectors.groupingBy(ProjectGroupPlainInfo::getMonitorGroupID)).values()
                                .stream().map(p3 -> p3.stream().findFirst().map(p4 -> MonitorGroupBaseInfo.builder()
                                        .monitorGroupID(p4.getMonitorGroupID()).monitorGroupName(p4.getMonitorGroupName())
                                        .monitorGroupEnable(p4.getMonitorGroupEnable()).monitorPointDataList(p3.stream()
                                                .collect(Collectors.groupingBy(ProjectGroupPlainInfo::getMonitorPointID))
                                                .values().stream().map(p5 -> p5.stream().findFirst().map(p6 ->
                                                        MonitorPointBaseInfo.builder().monitorPointID(p6.getMonitorPointID())
                                                                .monitorPointName(p6.getMonitorPointName())
                                                                .monitorPointEnable(p6.getMonitorPointEnable())
                                                                .monitorType(p6.getMonitorType())
                                                                .monitorItemID(p6.getMonitorItemID())
                                                                .multiSensor(p6.getMultiSensor()).sensorDataList(p5.stream()
                                                                        .filter(w -> Objects.nonNull(w.getSensorID()))
                                                                        .map(p7 -> SensorBaseInfo.builder().sensorID(
                                                                                p7.getSensorID()).sensorName(
                                                                                p7.getSensorName()).sensorAlias(
                                                                                p7.getSensorAlias()).build()).toList())
                                                                .build()).orElse(null)).filter(Objects::nonNull)
                                                .toList()).build()).orElse(null)).filter(Objects::nonNull).toList())
                        .build()).orElse(null)).filter(Objects::nonNull).toList();
    }

    @Override
    public List<MonitorGroupPointBaseInfo> queryMonitorTypeGroupPoint(QueryManualSensorListByMonitorParam pa) {
        List<MonitorGroupPointBaseInfo> monitorGroupPointBaseInfos = tbMonitorGroupMapper.queryMonitorTypeGroupPoint(pa);
        if (!CollectionUtil.isNullOrEmpty(monitorGroupPointBaseInfos)) {
            monitorGroupPointBaseInfos.forEach(m -> {
                // TODO:判断监测类型为 位移结果数据(相对) 时, 如果监测组下的监测项目名称为[水位位移,垂直位移]
            });
        }
        return monitorGroupPointBaseInfos;
    }
}
