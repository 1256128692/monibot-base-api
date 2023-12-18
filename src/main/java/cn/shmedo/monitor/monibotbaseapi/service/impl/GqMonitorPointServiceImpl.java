package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.enums.StatisticalMethods;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqMonitorPointDataPushParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqQueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.GqMonitorPointDataResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoV3;
import cn.shmedo.monitor.monibotbaseapi.service.GqMonitorPointService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableTransactionManagement
@Service
@AllArgsConstructor
public class GqMonitorPointServiceImpl implements GqMonitorPointService {

    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;

    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    private final TbSensorMapper tbSensorMapper;

    private SensorDataDao sensorDataDao;

    @Override
    public GqMonitorPointDataResponse gqQueryMonitorPointDataList(GqQueryMonitorPointDataParam pa) {

        List<SensorBaseInfoV3> sensorInfoList =  tbSensorMapper.selectListByGqQueryCondition(pa);

        List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(pa.getMonitorType());

        if (!CollectionUtil.isNullOrEmpty(sensorInfoList)) {
            List<Integer> sensorIDList = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> maps = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(), DisplayDensity.FOUR_HOUR.getValue(),
                    StatisticalMethods.LATEST.getValue(), fieldInfoList, pa.getMonitorType());
            sensorInfoList.forEach(s -> {
                s.setSensorDataList(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID())).collect(Collectors.toList()));
                s.setWaterMeasuringTypeName(WaterMeasureType.getDescByCode(pa.getToken()));
            });
        }

        return new GqMonitorPointDataResponse(sensorInfoList, fieldInfoList);
    }

    @Override
    public Object gqMonitorPointDataPush(GqMonitorPointDataPushParam pa) {

        List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(pa.getMonitorType());

        List<Map<String, Object>> sensorDataList = new ArrayList<>();
        pa.getDataList().forEach(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sensorID", p.getSid());
            map.put("time", p.getTime());
            p.getSensorDataList().forEach(d -> {
                map.put(d.getFieldToken(), d.getValue());
            });
            sensorDataList.add(map);
        });

        String tableSuffix = null;
        sensorDataDao.insertSensorCommonData(sensorDataList, fieldInfoList, pa.getMonitorType(), tableSuffix);

        return ResultWrapper.successWithNothing();
    }
}
