package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.IDNameAlias;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorItemWithPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPoint4Web;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorPointService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Override
    public void configMonitorPointSensors(Integer pointID, List<Integer> sensorIDList, Integer userID) {
        tbSensorMapper.updatePoint(pointID, sensorIDList, userID, new Date());
    }

    @Override
    public PageUtil.Page<MonitorPoint4Web> queryMonitorPointPageList(QueryMonitorPointPageListParam pa) {
        Page<MonitorPoint4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        IPage<MonitorPoint4Web> pageData = tbMonitorPointMapper.queryPage(page, pa.getProjectID(), pa.getMonitorType(), pa.getMonitorItemName(), pa.getSensorID(), pa.getPointName());
        if (CollectionUtils.isEmpty(pageData.getRecords())){
            return PageUtil.Page.empty();
        }
        List<Integer> pIDList = pageData.getRecords().stream().map(MonitorPoint4Web::getID).collect(Collectors.toList());
        List<TbSensor> tbSensorList = tbSensorMapper.selectList(
                new QueryWrapper<TbSensor>().in("MonitorPointID", pIDList)
        );
        if (CollectionUtils.isNotEmpty(tbSensorList)){
            Map<Integer, List<TbSensor>> map = tbSensorList.stream().collect(Collectors.groupingBy(TbSensor::getMonitorPointID));
            pageData.getRecords().forEach(item ->{
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
        List<MonitorItemWithPoint>  list = tbMonitorItemMapper.queryMonitorItemWithPointBy(pa.getProjectID(), pa.getMonitorItemIDList());
        if (CollectionUtils.isNotEmpty(list)){
            List<TbMonitorPoint> temp = tbMonitorPointMapper.selectList(
                    new QueryWrapper<TbMonitorPoint>()
                            .in("monitorItemID", list.stream().map(MonitorItemWithPoint::getMonitorItemID).collect(Collectors.toList()))
                            .orderByDesc("id")
            );
            Map<Integer, List<TbMonitorPoint>> map = temp.stream().collect(Collectors.groupingBy(TbMonitorPoint::getMonitorItemID));
            list.forEach(item ->{
                item.setMonitorPointList(map.get(item.getMonitorItemID()));
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
}
