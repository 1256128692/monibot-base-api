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
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.Group4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.GroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.GroupMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorGroupService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.ParamBuilder;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        tbMonitorGroupMapper.updateByPrimaryKey( pa.update(now, userID));
        if (CollectionUtils.isNotEmpty(pa.getMonitorItemIDList())){
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
        if (CollectionUtils.isNotEmpty(pa.getMonitorPointIDList())){
            tbMonitorGroupPointMapper.delete(
                    new QueryWrapper<TbMonitorGroupPoint>().lambda().eq(TbMonitorGroupPoint::getMonitorGroupID, pa.getGroupID())
            );
            List<TbMonitorGroupPoint> tbMonitorGroupPointList = pa.getMonitorPointIDList().stream().map(item -> TbMonitorGroupPoint.builder()
                    .monitorGroupID(pa.getGroupID())
                    .monitorPointID(item)
                    .build()).collect(Collectors.toList());
            tbMonitorGroupPointMapper.insertBatchSomeColumn(
                    tbMonitorGroupPointList
            );
        }
    }

    @Override
    public String uploadMonitorGroupImage(UploadMonitorGroupImageParam pa, Integer userID) {
        AddFileUploadRequest pojo = ParamBuilder.buildAddFileUploadRequest(pa.getImageContent(),pa.getImageSuffix(),userID,pa.getFileName(),pa.getCompanyID());
        ResultWrapper<FilePathResponse> info = mdInfoService.addFileUpload(pojo);
        String path = null;
        if (!info.apiSuccess()) {
            throw new CustomBaseException(info.getCode(), ErrorConstant.IMAGE_INSERT_FAIL) ;
        } else {
            path  = info.getData().getPath();
        }
        tbMonitorGroupMapper.updateImg(path, pa.getGroupID(), userID, new Date());
        return fileService.getFileUrl(path);
    }

    @Override
    public void configMonitorPointImageLocation(ConfigMonitorPointImageLocationParam pa) {
        tbMonitorGroupPointMapper.updateImgLocation(pa.getPointLocationList(), pa.getGroupID());
    }

    @Override
    public PageUtil.Page<Group4Web> queryMonitorGroupPage(QueryMonitorGroupPageParam pa) {
        Page<Group4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<Group4Web> pageData= tbMonitorGroupMapper.queryPage(page, pa.getProjectID(), pa.getName(), pa.getMonitorItemID(), true);
        if (CollectionUtils.isEmpty(pageData.getRecords())){
            return PageUtil.Page.empty();
        }
        List<Integer> groupIDList = pageData.getRecords().stream().map(Group4Web::getID).collect(Collectors.toList());
        List<Group4Web>  sonGroupList = tbMonitorGroupMapper.queryGroup4WebByParentIDs(groupIDList);
        // 处理监测项目和监测点
        handleGroup4Web(pageData.getRecords(), sonGroupList);
        return new PageUtil.Page<>( pageData.getTotal(),pageData.getRecords(),  pageData.getSize());
    }

    private void handleGroup4Web(List<Group4Web> parentGroupList, List<Group4Web> sonGroupList) {
        List<Integer> allGroupIDList = new java.util.ArrayList<>(parentGroupList.stream().map(Group4Web::getID).toList());
        allGroupIDList.addAll(sonGroupList.stream().map(Group4Web::getID).toList());
        List<GroupMonitorItem> monitorItems = tbMonitorItemMapper.queryMonitorItemByGroupIDs(allGroupIDList);
        Map<Integer, List<GroupMonitorItem>> monitorItemMap = monitorItems.stream().collect(Collectors.groupingBy(GroupMonitorItem::getGroupID));


        List<GroupPoint> groupPoints = tbMonitorPointMapper.queryGroupPointByGroupIDs(allGroupIDList);
        Map<Integer, List<GroupPoint>> groupPointMap = groupPoints.stream().collect(Collectors.groupingBy(GroupPoint::getGroupID));
        sonGroupList.forEach(
                group -> {
                    group.setMonitorItemList(monitorItemMap.getOrDefault(group.getID(), Collections.emptyList()));
                    group.setMonitorPointList(groupPointMap.getOrDefault(group.getID(), Collections.emptyList()));
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
        if (StrUtil.isNotBlank(group4Web.getImagePath())){
            group4Web.setImgURL(fileService.getFileUrl(group4Web.getImagePath()));
        }
        List<Group4Web>  sonGroupList = tbMonitorGroupMapper.queryGroup4WebByParentIDs(List.of(group4Web.getID()));
        handleGroup4Web(List.of(group4Web), sonGroupList);
        return group4Web;
    }
}
