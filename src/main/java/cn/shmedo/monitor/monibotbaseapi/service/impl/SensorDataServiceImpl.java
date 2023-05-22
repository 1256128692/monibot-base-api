package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.StatisticsSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.service.SensorDataService;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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

    @Override
    public void statisticsSensorData(StatisticsSensorDataParam pa) {
        List<TbMonitorTypeField> temp = tbMonitorTypeFieldMapper.selectList(
                new QueryWrapper<TbMonitorTypeField>().lambda().eq(TbMonitorTypeField::getMonitorType, pa.getMonitorType())
        );
        List<FieldSelectInfo> fieldSelectInfoList = wtMonitorService.getFieldSelectInfoListFromModleTypeFieldList(temp);
        if (CollectionUtil.isEmpty(fieldSelectInfoList)) {
            return;
        }
        List<Map<String, Object>> list = sensorDataDao.querySensorDayStatisticsData(pa.getSensorIDList(),
                new Timestamp(pa.getBegin().getTime()),
                new Timestamp(pa.getEnd().getTime()),
                fieldSelectInfoList, pa.getRaw(), pa.getMonitorType());

        sensorDataDao.insertSensorData(list, true, pa.getRaw(), fieldSelectInfoList, pa.getMonitorType());
    }
}
