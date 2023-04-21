package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataUnit;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainMonitorPointHistoryData {

    private TbMonitorPoint monitorPoint;

    private List<TbSensor> sensorList;


    /**
     * 传感器历史数据
     */
    List<Map<String, Object>> dataList;

    /**
     * 监测类型属性字段列表
     */
    private List<FieldSelectInfo> fieldList;

    /**
     * 监测类型属性字段单位列表
     */
    private List<TbDataUnit> dataUnitList;


    private Double dailyRainfall;

    /**
     * 日降雨量数据
     */
    List<Map<String, Object>> dailyRainfallList;
}
