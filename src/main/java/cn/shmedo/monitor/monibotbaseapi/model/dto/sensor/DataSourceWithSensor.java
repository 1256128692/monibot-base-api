package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensorDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 传感器数据源 + 传感器信息
 * @author Chengfs on 2023/4/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataSourceWithSensor extends TbSensorDataSource {

    private List<TbSensor> sensorInfoList;
}

    
    