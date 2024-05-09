package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorGroupPointParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.IDNameAlias;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorGroupBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointAllInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorPointBaseInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorItemWithPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPoint4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointSimple;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorTypeFieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorPointService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
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
 * @create: 2023-04-12 13:48
 **/
@Service
@Slf4j
@AllArgsConstructor
public class MonitorPointServiceImpl implements MonitorPointService {
    private TbMonitorPointMapper tbMonitorPointMapper;
    private TbSensorMapper tbSensorMapper;
    private TbMonitorItemMapper tbMonitorItemMapper;
    private TbMonitorGroupMapper tbMonitorGroupMapper;
    private TbMonitorGroupItemMapper tbMonitorGroupItemMapper;
    private TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    @Override
    public void addMonitorPoint(AddMonitorPointParam pa, Integer userID) {
        TbMonitorPoint temp = Param2DBEntityUtil.fromAddMonitorPointParam2TbMonitorPoint(pa, userID);
        tbMonitorPointMapper.insert(temp);
    }

    @Override
    public void updateMonitorPoint(UpdateMonitorPointParam pa, Integer userID) {
        tbMonitorPointMapper.updateByPrimaryKey(pa.update(userID));
    }

    @Override
    public void deleteMonitorPoint(List<Integer> pointIDList) {
        tbMonitorPointMapper.deleteBatchIds(pointIDList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void configMonitorPointSensors(Integer pointID, List<Integer> sensorIDList, Integer userID) {
        Date now = new Date();
        tbSensorMapper.updatePointByPoint(pointID, null, userID, now);
        if (
                CollectionUtils.isNotEmpty(sensorIDList)
        ) {
            tbSensorMapper.updatePoint(pointID, sensorIDList, userID, now);
        }
    }

    @Override
    public PageUtil.Page<MonitorPoint4Web> queryMonitorPointPageList(QueryMonitorPointPageListParam pa) {
        Page<MonitorPoint4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        IPage<MonitorPoint4Web> pageData = tbMonitorPointMapper.queryPage(page, pa.getProjectID(), pa.getMonitorType(), pa.getMonitorItemID(), pa.getQueryCode());
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return PageUtil.Page.empty();
        }
        List<Integer> pIDList = pageData.getRecords().stream().map(MonitorPoint4Web::getID).collect(Collectors.toList());
        List<TbSensor> tbSensorList = tbSensorMapper.selectList(
                new QueryWrapper<TbSensor>().in("MonitorPointID", pIDList)
        );
        if (CollectionUtils.isNotEmpty(tbSensorList)) {
            Map<Integer, List<TbSensor>> map = tbSensorList.stream().collect(Collectors.groupingBy(TbSensor::getMonitorPointID));
            pageData.getRecords().forEach(item -> {
                item.setSensorList(map.get(item.getID()));
            });
        }
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public List<IDNameAlias> queryMonitorPointSimpleList(QueryMonitorPointSimpleListParam pa) {
        return tbMonitorPointMapper.querySimpleBy(pa.getProjectID(), pa.getGroupID(), pa.getMonitorItemIDList());
    }

    @Override
    public List<MonitorItemWithPoint> queryMonitorItemPointList(QueryMonitorItemPointListParam pa) {
        List<MonitorItemWithPoint> list = tbMonitorItemMapper.queryMonitorItemWithPointBy(pa.getProjectID(), pa.getMonitorItemIDList(), pa.getItemEnable());
        if (CollectionUtils.isNotEmpty(list)) {
            List<TbMonitorPoint> temp = tbMonitorPointMapper.selectList(
                    new QueryWrapper<TbMonitorPoint>()
                            .in("monitorItemID", list.stream().map(MonitorItemWithPoint::getMonitorItemID).collect(Collectors.toList()))
                            .orderByDesc("id")
            );
            Map<Integer, List<TbMonitorPoint>> map = temp.stream().collect(Collectors.groupingBy(TbMonitorPoint::getMonitorItemID));

            List<MonitorTypeFieldBaseInfo> allMonitorTypeFieldList = tbMonitorTypeFieldMapper.selectListByMonitorItemIDList(list.stream().map(MonitorItemWithPoint::getMonitorItemID).collect(Collectors.toList()));
            list.forEach(item -> {
                item.setMonitorPointList(map.get(item.getMonitorItemID()));
                if (!CollectionUtil.isNullOrEmpty(allMonitorTypeFieldList)) {
                    item.setMonitorTypeFieldList(allMonitorTypeFieldList.stream().filter(a -> a.getMonitorItemID().equals(item.getMonitorItemID())).collect(Collectors.toList()));
                }
            });
        }
        return list;
    }

    @Override
    public void addMonitorPointBatch(AddMonitorPointBatchParam pa, Integer userID) {
        List<TbMonitorPoint> list = Param2DBEntityUtil.fromAddMonitorPointBatchParam2TbMonitorPoint(pa, userID);
        tbMonitorPointMapper.insertBatch(list);
    }

    @Override
    public void updateMonitorPointBatch(UpdateMonitorPointBatchParam pa, Integer userID) {
        tbMonitorPointMapper.updateBatch(pa.updateBatch(userID), pa.getSelectUpdate());

    }

    @Override
    public List<MonitorPointAllInfoV1> queryMonitorGroupPointList(QueryMonitorGroupPointParam pa) {
        List<MonitorPointAllInfoV1> list = tbMonitorItemMapper.queryListByProjectIDAndMonitorItemID(pa.getProjectID(), pa.getMonitorItemID(), pa.getEnable());

        if (!CollectionUtil.isNullOrEmpty(list)) {
            List<Integer> monitorItemIDList = list.stream().map(MonitorPointAllInfoV1::getMonitorItemID).collect(Collectors.toList());

            List<MonitorGroupBaseInfoV1> allGroups = tbMonitorGroupMapper.selectGroupInfoByItemIDs(monitorItemIDList);
            List<MonitorPointBaseInfoV1> monitorPointList = tbMonitorPointMapper.selectPointListByMonitorItemIDList(monitorItemIDList);
            Map<Integer, List<MonitorGroupBaseInfoV1>> groupMap = new HashMap<>();
            // 将组别按照parentID分组
            allGroups.add(new MonitorGroupBaseInfoV1(-1, null, "未分配组", true, null));
            for (MonitorGroupBaseInfoV1 group : allGroups) {
                groupMap.computeIfAbsent(group.getParentID(), k -> new ArrayList<>()).add(group);
            }
            monitorPointList.forEach(m -> {
                if (m.getGroupID() == null) {
                    m.setGroupID(-1);
                }
            });
            // 构建组别树结构
            for (MonitorPointAllInfoV1 item : list) {
                item.setMonitorGroupList(buildGroupTree(groupMap, null));
                item.setMonitorPointList(monitorPointList.stream().filter(m -> m.getMonitorItemID().equals(item.getMonitorItemID())).collect(Collectors.toList()));
            }
        }
        if (Optional.ofNullable(pa.getBindPoint()).orElse(false)) {
            list = list.stream().filter(data -> CollUtil.isNotEmpty(data.getMonitorPointList())).toList();
        }

        return list;
    }

    @Override
    public Map<Integer, List<MonitorPointSimple>> queryMonitorPointWithProjectType(QueryMonitorPointWithProjectTypeParam pa) {
        return tbMonitorPointMapper.listByProjectType(pa.getProjectList(), pa.getProjectTypes())
                .stream().collect(Collectors.groupingBy(MonitorPointSimple::getProjectType));
    }

    @Override
    public Object queryMonitorPointIncludeSensorList(QueryMonitorPointIncludeSensorParam pa) {

        return tbMonitorPointMapper.listByProjectTypeAndMonitorType(pa.getProjectType(),
                pa.getMonitorType(), pa.getCompanyID());

    }

    @Override
    public List<MonitorPoint4Web> queryMonitorPointList(QueryMonitorPointListParam pa) {

        List<MonitorPoint4Web> list = tbMonitorPointMapper.queryList(pa.getProjectID(), pa.getMonitorType(), pa.getMonitorItemID(), pa.getQueryCode());
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<Integer> pIDList = list.stream().map(MonitorPoint4Web::getID).collect(Collectors.toList());
        List<TbSensor> tbSensorList = tbSensorMapper.selectList(
                new QueryWrapper<TbSensor>().in("MonitorPointID", pIDList)
        );
        if (CollectionUtils.isNotEmpty(tbSensorList)) {
            Map<Integer, List<TbSensor>> map = tbSensorList.stream().collect(Collectors.groupingBy(TbSensor::getMonitorPointID));
            list.forEach(item -> item.setSensorList(map.get(item.getID())));
        }
        if (pa.isExcludeBindingSensorOnSingle())
            // 监测类型为单传感器时，排除绑定了传感器的监测点
            list = list.stream().filter(item ->
                    !(!item.getMonitorTypeMultiSensor() && !CollectionUtil.isNullOrEmpty(item.getSensorList()))).collect(Collectors.toList());
        return list;
    }

    private List<MonitorGroupBaseInfoV1> buildGroupTree(Map<Integer, List<MonitorGroupBaseInfoV1>> groupMap, Integer parentID) {
        List<MonitorGroupBaseInfoV1> result = groupMap.get(parentID);
        if (result != null) {
            for (MonitorGroupBaseInfoV1 group : result) {
                group.setChildGroupList(buildGroupTree(groupMap, group.getGroupID()));
            }
        }
        return result;
    }


}

