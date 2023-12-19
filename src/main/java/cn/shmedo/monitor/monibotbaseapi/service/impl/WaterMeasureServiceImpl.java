package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure.MonitorPointExValue;
import cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure.WaterMeasurePointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure.WaterMeasurePointSimple;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorKindEnum;
import cn.shmedo.monitor.monibotbaseapi.model.param.watermeasure.*;
import cn.shmedo.monitor.monibotbaseapi.service.WaterMeasureService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Chengfs on 2023/12/15
 */
@Service
@RequiredArgsConstructor
public class WaterMeasureServiceImpl implements WaterMeasureService {

    private final TbSensorMapper sensorMapper;
    private final TbMonitorPointMapper monitorPointMapper;
    private final TbMonitorItemMapper monitorItemMapper;

    @Override
    public PageUtil.Page<WaterMeasurePointSimple> measurePointPage(WaterMeasurePointPageRequest request) {
        Page<WaterMeasurePointSimple> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        sensorMapper.measurePointPage(page, request);

        page.getRecords().forEach(item -> {
            parseExValue(item);
            Optional.ofNullable(item.getProjectType()).ifPresent(t -> item.setProjectTypeName(t.getName()));
        });
        return new PageUtil.Page<>(page.getPages(), page.getRecords(), page.getTotal());
    }

    @Override
    public WaterMeasurePointInfo singleMeasurePoint(SingleMeasurePointRequest request) {
        WaterMeasurePointInfo result = sensorMapper.singleMeasurePoint(request.getSensorID());
        Optional.ofNullable(result).ifPresent(item -> {
            parseExValue(item);
            Optional.ofNullable(item.getProjectType()).ifPresent(t -> item.setProjectTypeName(t.getName()));
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMeasurePoint(AddMeasurePointRequest request) {
        Date now = new Date();
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();

        TbMonitorPoint point = new TbMonitorPoint();
        point.setProjectID(request.getProjectID());
        point.setMonitorType(request.getSensorID());
        point.setMonitorItemID(request.getMonitorItemID());
        point.setName(request.getMonitorPointName());
        point.setGpsLocation(request.getGpsLocation());
        point.setEnable(Boolean.TRUE);
        point.setCreateTime(now);
        point.setUpdateTime(now);

        MonitorPointExValue exValue = new MonitorPointExValue(request.getWaterMeasureType(), request.getWaterMeasureWay(),
                request.getCalculateType(), request.getMonitorElements().stream().toList());
        point.setExValues(JsonUtil.toJson(exValue));
        point.setCreateUserID(subject.getSubjectID());
        point.setUpdateUserID(subject.getSubjectID());
        monitorPointMapper.insert(point);

        TbSensor sensor = request.getSensor();
        sensor.setMonitorPointID(point.getID());
        sensorMapper.updateById(sensor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMeasurePoint(UpdateMeasurePointRequest request) {
        TbMonitorPoint point = monitorPointMapper.selectOne(Wrappers.<TbMonitorPoint>lambdaQuery()
                .eq(TbMonitorPoint::getID, request.getSensor().getMonitorPointID()).select(TbMonitorPoint::getID));
        Optional.ofNullable(request.getGpsLocation()).ifPresent(point::setGpsLocation);

        MonitorPointExValue exValue = new MonitorPointExValue(request.getWaterMeasureType(), request.getWaterMeasureWay(),
                request.getCalculateType(), request.getMonitorElements().stream().toList());
        point.setExValues(JsonUtil.toJson(exValue));
        monitorPointMapper.updateById(point);
    }

    @Override
    public List<?> listWaterMeasureSensor(ListWaterMeasureSensorRequest request) {
        record result(Integer id, String alias) {}
        return sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                        .eq(TbSensor::getProjectID, request.getProjectID())
                        .eq(TbSensor::getMonitorType, request.getMonitorType())
                        .eq(TbSensor::getKind, SensorKindEnum.MANUAL_KIND.getCode())
                        .eq(TbSensor::getEnable, Boolean.TRUE)
                        .isNull(TbSensor::getMonitorPointID)
                        .select(TbSensor::getID, TbSensor::getAlias)
                        .orderByDesc(TbSensor::getID))
                .stream().map(r -> new result(r.getID(), r.getAlias())).toList();
    }

    private void parseExValue(WaterMeasurePointSimple item) {
        if (JsonUtil.isJson(item.getExValues())) {
            MonitorPointExValue exValue = JsonUtil.toObject(item.getExValues(), MonitorPointExValue.class);
            Optional.ofNullable(exValue).ifPresent(e -> {
                item.setWaterMeasureType(e.getWaterMeasureType());
                item.setWaterMeasureWay(e.getWaterMeasureWay());
                item.setCalculateType(e.getCalculateType());
                item.setMonitorElements(e.getMonitorElements());
            });
        }
    }
}