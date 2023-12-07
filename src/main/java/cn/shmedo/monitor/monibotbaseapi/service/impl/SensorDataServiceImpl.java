package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.entity.MonitorTypeExValue;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AvgDensityType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorStatisticsType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.StatisticalMethods;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.QuerySensorHasDataCountParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.StatisticsSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHasDataCountResponse;
import cn.shmedo.monitor.monibotbaseapi.service.SensorDataService;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-18 15:15
 **/
@Service
@AllArgsConstructor

public class SensorDataServiceImpl implements SensorDataService {

    private final SensorDataDao sensorDataDao;
    private final WtMonitorService wtMonitorService;
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;
    private final TbMonitorTypeMapper tbMonitorTypeMapper;

    @Override
    public void statisticsSensorData(StatisticsSensorDataParam pa) {
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.queryByType(pa.getMonitorType());
        if (tbMonitorType == null) {
            return;
        }
        List<TbMonitorTypeField> temp = tbMonitorTypeFieldMapper.selectList(
                new QueryWrapper<TbMonitorTypeField>().lambda().eq(TbMonitorTypeField::getMonitorType, pa.getMonitorType())
        );
        List<FieldSelectInfo> fieldSelectInfoList = wtMonitorService.getFieldSelectInfoListFromModleTypeFieldList(temp);
        if (CollectionUtil.isEmpty(fieldSelectInfoList)) {
            return;
        }
        List<SensorStatisticsType> statisticsTypes = new ArrayList<>();
        String exValueStr = tbMonitorType.getExValues();
        if (ObjectUtil.isNotEmpty(exValueStr)) {
            MonitorTypeExValue exValue = JsonUtil.toObject(exValueStr, MonitorTypeExValue.class);
            if (ObjectUtil.isNotEmpty(exValue.getStatisticalMethods())) {
                statisticsTypes.addAll(exValue.getStatisticalMethods().stream().map(SensorStatisticsType::getByCode)
                        .filter(ObjectUtil::isNotEmpty)
                        .toList());
            }
        }
        if (ObjectUtil.isEmpty(statisticsTypes)) {
            return;
        }
        statisticsTypes.forEach(e -> {
            List<Map<String, Object>> list = sensorDataDao.querySensorDayStatisticsData(pa.getSensorIDList(),
                    new Timestamp(pa.getBegin().getTime()),
                    new Timestamp(pa.getEnd().getTime()),
                    fieldSelectInfoList, pa.getRaw(), pa.getMonitorType(), e);
            sensorDataDao.insertSensorData(list, false, pa.getRaw(), fieldSelectInfoList, pa.getMonitorType(), e.getTableSuffix());
        });


    }

    @Override
    public SensorHasDataCountResponse querySensorHasDataCount(QuerySensorHasDataCountParam pa) {
        SensorHasDataCountResponse vo = new SensorHasDataCountResponse();
        List<Map<String, Object>> allSensorDataList = new LinkedList<Map<String, Object>>();

        // 遍历分组
        for (Map.Entry<Integer, List<TbSensor>> entry : pa.getGroupedByMonitorType().entrySet()) {
            Integer monitorType = entry.getKey();
            List<TbSensor> sensors = entry.getValue();

            // 提取传感器ID列表
            List<Integer> sensorIDList = sensors.stream()
                    .map(TbSensor::getID)
                    .collect(Collectors.toList());
            List<FieldBaseInfo> fieldList = tbMonitorTypeFieldMapper.selectListByMonitorType(monitorType);
            // 进行数据查询
            List<Map<String, Object>> sensorDataList = sensorDataDao.queryCommonSensorDataList(sensorIDList, pa.getBegin(), pa.getEnd(),
                    pa.getDensity(), StatisticalMethods.LATEST.getValue(), fieldList, monitorType);

            // 将查询结果添加到总的列表中
            allSensorDataList.addAll(sensorDataList.stream()
                    .filter(m -> m.get("sensorID") != null && m.get("time") != null &&
                            m.entrySet().stream()
                                    .filter(data -> !data.getKey().equals("sensorID") &&
                                            !data.getKey().equals("time"))
                                    .allMatch(data -> data.getValue() != null))
                    .distinct()
                    .collect(Collectors.toList()));
        }
        if (CollectionUtil.isEmpty(allSensorDataList)) {
            return null;
        }
        vo.setDensity(pa.getRawDensity());
        // (小时,日,全部)将时间格式转换为"yyyy-MM-dd"
        List<String> dataList = allSensorDataList.stream()
                .filter(map -> map.containsKey("time"))
                .map(map -> (String) map.get("time"))
                .map(time -> time.substring(0, 10))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (pa.getDensity().equals(DisplayDensity.MONTH.getValue())) {
            // (月份)将时间格式转换为"yyyy-MM"
            dataList = dataList.stream().map(date -> date.substring(0, 7)).distinct().collect(Collectors.toList());
        } else if (pa.getDensity().equals(DisplayDensity.YEAR.getValue())) {
            // (年份)将时间格式转换为"yyyy"
            dataList = dataList.stream().map(date -> date.substring(0, 4)).distinct().collect(Collectors.toList());
        }
        vo.setDataList(dataList);
        return vo;
    }
}
