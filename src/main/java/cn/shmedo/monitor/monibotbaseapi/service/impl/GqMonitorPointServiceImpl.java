package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.StatisticalMethods;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.StatisticsSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.service.GqMonitorPointService;
import cn.shmedo.monitor.monibotbaseapi.service.SensorDataService;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import cn.shmedo.monitor.monibotbaseapi.util.InfluxDBDataUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@EnableTransactionManagement
@Service
@AllArgsConstructor
public class GqMonitorPointServiceImpl implements GqMonitorPointService {

    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;

    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    private final TbSensorMapper tbSensorMapper;

    private SensorDataDao sensorDataDao;

    private final SensorDataService sensorDataService;

    @Override
    public GqMonitorPointDataResponse gqQueryMonitorPointDataList(GqQueryMonitorPointDataParam pa) {

        List<SensorBaseInfoV3> sensorInfoList = tbSensorMapper.selectListByGqQueryCondition(pa);

        List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(pa.getMonitorType());

        if (!CollectionUtil.isNullOrEmpty(sensorInfoList)) {
            List<Integer> sensorIDList = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> maps = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(), DisplayDensity.FOUR_HOUR.getValue(),
                    StatisticalMethods.FIRST.getValue(), fieldInfoList, pa.getMonitorType());
            sensorInfoList.forEach(s -> {
                s.setSensorDataList(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID())).collect(Collectors.toList()));
                s.setWaterMeasuringTypeName(WaterMeasureType.getDescByCode(pa.getToken()));
            });
        }

        return new GqMonitorPointDataResponse(sensorInfoList, fieldInfoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object gqMonitorPointDataPush(GqMonitorPointDataPushParam pa) {

        List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(pa.getMonitorType());

        List<Map<String, Object>> sensorDataList = new ArrayList<>();
        pa.getDataList().forEach(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sensorID", p.getSid());
            map.put("time", p.getTime());
            p.getSensorDataList().forEach(d -> {
                map.put(d.getFieldToken(), Convert.toDouble(d.getValue()));
            });
            sensorDataList.add(map);
        });

        String tableSuffix = null;
        sensorDataDao.insertSensorCommonData(sensorDataList, fieldInfoList, pa.getMonitorType(), tableSuffix);

        // 更新日平均数据
        StatisticsSensorDataParam param = new StatisticsSensorDataParam();
        param.setRaw(false);
        param.setBegin(DateUtil.beginOfDay(pa.getDataList().get(0).getTime()));
        param.setEnd(DateUtil.endOfDay(pa.getDataList().get(0).getTime()));
        param.setSensorIDList(pa.getDataList().stream().map(SensorInfluxdbData::getSid).collect(Collectors.toList()));
        param.setSensorList(tbSensorMapper.selectBatchIds(pa.getDataList().stream().map(SensorInfluxdbData::getSid).collect(Collectors.toList())));
        param.setMonitorType(pa.getMonitorType());
        if (!CollectionUtil.isNullOrEmpty(param.getSensorList())) {
            sensorDataService.statisticsSensorData(param);
        }

        return ResultWrapper.successWithNothing();
    }

    @Override
    public Object gqQueryMonitorPointStatisticsDataPage(GqQueryMonitorPointStatisticsDataPageParam pa) {
        List<Map<String, Object>> maps = new LinkedList<>();
        List<SensorBaseInfoV3> sensorInfoList = new LinkedList<>();
        if (pa.getProjectTypeID() == null) {
            List<SensorBaseInfoV3> sensorInfoV1 = tbSensorMapper.selectListByGqQueryCondition(new GqQueryMonitorPointDataParam(
                    pa.getCompanyID(), pa.getProjectTypeID(), pa.getKind(), null, pa.getMonitorPointName(),
                    null, null, MonitorType.SLUICE_REGIMEN.getKey(), pa.getTokenStr1()
            ));
            List<SensorBaseInfoV3> sensorInfoV2 = tbSensorMapper.selectListByGqQueryCondition(new GqQueryMonitorPointDataParam(
                    pa.getCompanyID(), pa.getProjectTypeID(), pa.getKind(), null, pa.getMonitorPointName(),
                    null, null, MonitorType.CHANNEL_WATER_LEVEL.getKey(), pa.getTokenStr2()
            ));

            if (!CollectionUtil.isNullOrEmpty(sensorInfoV1)) {
                sensorInfoList.addAll(sensorInfoV1);
                // 监测项目与监测子字段类型关系表
                List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(MonitorType.SLUICE_REGIMEN.getKey());

                List<Integer> sensorIDListV1 = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
                List<Map<String, Object>> mapsV1 = sensorDataDao.queryCommonSensorDataList(sensorIDListV1, pa.getBegin(), pa.getEnd(), pa.getDensityType(),
                        pa.getStatisticsType(), fieldInfoList, MonitorType.SLUICE_REGIMEN.getKey());
                maps.addAll(mapsV1);
            }
            if (!CollectionUtil.isNullOrEmpty(sensorInfoV2)) {
                sensorInfoList.addAll(sensorInfoV2);
                // 监测项目与监测子字段类型关系表
                List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(MonitorType.CHANNEL_WATER_LEVEL.getKey());

                List<Integer> sensorIDListV2 = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
                List<Map<String, Object>> mapsV2 = sensorDataDao.queryCommonSensorDataList(sensorIDListV2, pa.getBegin(), pa.getEnd(), pa.getDensityType(),
                        pa.getStatisticsType(), fieldInfoList, MonitorType.CHANNEL_WATER_LEVEL.getKey());
                maps.addAll(mapsV2);
            }

        } else {
            List<SensorBaseInfoV3> sensorInfoV1 = tbSensorMapper.selectListByGqQueryCondition(new GqQueryMonitorPointDataParam(
                    pa.getCompanyID(), pa.getProjectTypeID(), pa.getKind(), null, pa.getMonitorPointName(),
                    null, null, pa.getMonitorType(), pa.getTokenStr3()
            ));

            sensorInfoList.addAll(sensorInfoV1);
            if (CollectionUtil.isNullOrEmpty(sensorInfoList)) {
                return PageUtil.Page.empty();
            }

            // 监测项目与监测子字段类型关系表
            List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(pa.getMonitorType());

            List<Integer> sensorIDList = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> mapsV1 = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(), pa.getDensityType(),
                    pa.getStatisticsType(), fieldInfoList, pa.getMonitorType());
            maps.addAll(mapsV1);
        }


        if (CollectionUtil.isNullOrEmpty(maps)) {
            return PageUtil.Page.empty();
        }

        if (pa.getDensityType() == DisplayDensity.WEEK.getValue() || pa.getDensityType() == DisplayDensity.MONTH.getValue() ||
                pa.getDensityType() == DisplayDensity.YEAR.getValue()) {
            sensorInfoList.forEach(s -> {
                s.setSensorDataList(InfluxDBDataUtil.calculateStatistics(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID()))
                        .collect(Collectors.toList()), pa.getDensityType(), pa.getStatisticsType(), true));
            });
        } else {
            sensorInfoList.forEach(s -> {
                s.setSensorDataList(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID())).collect(Collectors.toList()));
            });
        }

        List<SensorBaseInfoV3> resultList = new LinkedList<SensorBaseInfoV3>();
        sensorInfoList.forEach(sensor -> {
            sensor.getSensorDataList().stream()
                    .forEach(dataMap -> {
                        Date time = null;
                        Double afterwater = null;
                        Double totalFlow = null;
                        Double dailyWaterTotal = null;
                        SensorBaseInfoV3 clonedObject = null;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        if (sensor.getMonitorType() == MonitorType.SLUICE_REGIMEN.getKey()) {
                            // 闸门类型
                                afterwater = Convert.toDouble(dataMap.get("afterwater"));
                                totalFlow = Convert.toDouble(dataMap.get("totalFlow"));
                        } else {
                            // 渠系类型
                                afterwater = Convert.toDouble(dataMap.get("waterH"));
                                totalFlow = Convert.toDouble(dataMap.get("waterS"));
                        }

                        if (pa.getDensityType() == DisplayDensity.SPECIAL_DAY.getValue()) {
                            try {
                                time = dateFormat.parse((String) dataMap.get("time"));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            time = (Date) dataMap.get("time");
                        }
                        clonedObject = BeanUtil.toBean(sensor, SensorBaseInfoV3.class);
                        if (totalFlow != null) {
                            dailyWaterTotal = totalFlow * 24 * 3600 / 10000;
                            clonedObject.setData(new IrrigatedAreaInfo(
                                    time, afterwater, totalFlow, dailyWaterTotal
                            ));
                        }
                        clonedObject.setTime(time);
                        resultList.add(clonedObject);
                    });
        });
        resultList.forEach(r -> {
            r.setSensorDataList(null);
        });
        Comparator<SensorBaseInfoV3> comparator = Comparator.comparing(SensorBaseInfoV3::getTime);
        if (pa.getDataSort() == null || !pa.getDataSort()) {
            comparator = comparator.reversed();
        }

        List<SensorBaseInfoV3> list = resultList.stream().sorted(comparator).collect(Collectors.toList());
        PageUtil.Page<SensorBaseInfoV3> page = PageUtil.page(list, pa.getPageSize(), pa.getCurrentPage());
        return new PageUtil.Page<SensorBaseInfoV3>(page.totalPage(), page.currentPageData(), page.totalCount());
    }

    @Override
    public Object gqQueryMonitorPointStatisticsDataList(GqQueryMonitorPointStatisticsDataParam pa) {

        List<Map<String, Object>> maps = new LinkedList<>();
        List<SensorBaseInfoV3> sensorInfoList = new LinkedList<>();
        if (pa.getProjectTypeID() == null) {
            List<SensorBaseInfoV3> sensorInfoV1 = tbSensorMapper.selectListByGqQueryCondition(new GqQueryMonitorPointDataParam(
                    pa.getCompanyID(), pa.getProjectTypeID(), pa.getKind(), null, pa.getMonitorPointName(),
                    null, null, MonitorType.SLUICE_REGIMEN.getKey(), pa.getTokenStr1()
            ));
            List<SensorBaseInfoV3> sensorInfoV2 = tbSensorMapper.selectListByGqQueryCondition(new GqQueryMonitorPointDataParam(
                    pa.getCompanyID(), pa.getProjectTypeID(), pa.getKind(), null, pa.getMonitorPointName(),
                    null, null, MonitorType.CHANNEL_WATER_LEVEL.getKey(), pa.getTokenStr2()
            ));

            if (!CollectionUtil.isNullOrEmpty(sensorInfoV1)) {
                sensorInfoList.addAll(sensorInfoV1);
                // 监测项目与监测子字段类型关系表
                List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(MonitorType.SLUICE_REGIMEN.getKey());

                List<Integer> sensorIDListV1 = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
                List<Map<String, Object>> mapsV1 = sensorDataDao.queryCommonSensorDataList(sensorIDListV1, pa.getBegin(), pa.getEnd(), pa.getDensityType(),
                        pa.getStatisticsType(), fieldInfoList, MonitorType.SLUICE_REGIMEN.getKey());
                maps.addAll(mapsV1);
            }
            if (!CollectionUtil.isNullOrEmpty(sensorInfoV2)) {
                sensorInfoList.addAll(sensorInfoV2);
                // 监测项目与监测子字段类型关系表
                List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(MonitorType.CHANNEL_WATER_LEVEL.getKey());

                List<Integer> sensorIDListV2 = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
                List<Map<String, Object>> mapsV2 = sensorDataDao.queryCommonSensorDataList(sensorIDListV2, pa.getBegin(), pa.getEnd(), pa.getDensityType(),
                        pa.getStatisticsType(), fieldInfoList, MonitorType.CHANNEL_WATER_LEVEL.getKey());
                maps.addAll(mapsV2);
            }

        } else {
            List<SensorBaseInfoV3> sensorInfoV1 = tbSensorMapper.selectListByGqQueryCondition(new GqQueryMonitorPointDataParam(
                    pa.getCompanyID(), pa.getProjectTypeID(), pa.getKind(), null, pa.getMonitorPointName(),
                    null, null, pa.getMonitorType(),  pa.getTokenStr3()
            ));

            sensorInfoList.addAll(sensorInfoV1);
            if (CollectionUtil.isNullOrEmpty(sensorInfoList)) {
                return PageUtil.Page.empty();
            }

            // 监测项目与监测子字段类型关系表
            List<FieldBaseInfo> fieldInfoList = tbMonitorTypeFieldMapper.selectListByType(pa.getMonitorType());

            List<Integer> sensorIDList = sensorInfoList.stream().map(SensorBaseInfoV3::getSensorID).collect(Collectors.toList());
            List<Map<String, Object>> mapsV1 = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(), pa.getDensityType(),
                    pa.getStatisticsType(), fieldInfoList, pa.getMonitorType());
            maps.addAll(mapsV1);
        }


        if (CollectionUtil.isNullOrEmpty(maps)) {
            return PageUtil.Page.empty();
        }

        if (pa.getDensityType() == DisplayDensity.WEEK.getValue() || pa.getDensityType() == DisplayDensity.MONTH.getValue() ||
                pa.getDensityType() == DisplayDensity.YEAR.getValue()) {
            sensorInfoList.forEach(s -> {
                s.setSensorDataList(InfluxDBDataUtil.calculateStatistics(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID()))
                        .collect(Collectors.toList()), pa.getDensityType(), pa.getStatisticsType(), true));
            });
        } else {
            sensorInfoList.forEach(s -> {
                s.setSensorDataList(maps.stream().filter(m -> m.get("sensorID").equals(s.getSensorID())).collect(Collectors.toList()));
            });
        }

        List<SensorBaseInfoV3> resultList = new LinkedList<SensorBaseInfoV3>();
        sensorInfoList.forEach(sensor -> {
            sensor.getSensorDataList().stream()
                    .forEach(dataMap -> {
                        Date time = null;
                        Double afterwater = null;
                        Double totalFlow = null;
                        Double dailyWaterTotal = null;
                        SensorBaseInfoV3 clonedObject = null;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        if (sensor.getMonitorType() == MonitorType.SLUICE_REGIMEN.getKey()) {
                            // 闸门类型
                            afterwater = Convert.toDouble(dataMap.get("afterwater"));
                            totalFlow = Convert.toDouble(dataMap.get("totalFlow"));
                        } else {
                            // 渠系类型
                            afterwater = Convert.toDouble(dataMap.get("waterH"));
                            totalFlow = Convert.toDouble(dataMap.get("waterS"));
                        }

                        if (pa.getDensityType() == DisplayDensity.SPECIAL_DAY.getValue()) {
                            try {
                                time = dateFormat.parse((String) dataMap.get("time"));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            time = (Date) dataMap.get("time");
                        }
                        clonedObject = BeanUtil.toBean(sensor, SensorBaseInfoV3.class);
                        if (totalFlow != null) {
                            dailyWaterTotal = totalFlow * 24 * 3600 / 10000;
                            clonedObject.setData(new IrrigatedAreaInfo(
                                    time, afterwater, totalFlow, dailyWaterTotal
                            ));
                        }
                        clonedObject.setTime(time);
                        resultList.add(clonedObject);
                    });
        });
        resultList.forEach(r -> {
            r.setSensorDataList(null);
        });
        Comparator<SensorBaseInfoV3> comparator = Comparator.comparing(SensorBaseInfoV3::getTime);
        if (pa.getDataSort() == null || !pa.getDataSort()) {
            comparator = comparator.reversed();
        }

        return resultList.stream().sorted(comparator).collect(Collectors.toList());
    }
}
