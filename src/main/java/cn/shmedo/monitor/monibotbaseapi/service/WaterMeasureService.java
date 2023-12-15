package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure.WaterMeasurePointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure.WaterMeasurePointSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.watermeasure.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

public interface WaterMeasureService {

    PageUtil.Page<WaterMeasurePointSimple> measurePointPage(WaterMeasurePointPageRequest request);

    WaterMeasurePointInfo singleMeasurePoint(SingleMeasurePointRequest request);

    void addMeasurePoint(AddMeasurePointRequest request);

    void updateMeasurePoint(UpdateMeasurePointRequest request);

    List<?> listWaterMeasureSensor(ListWaterMeasureSensorRequest request);
}
