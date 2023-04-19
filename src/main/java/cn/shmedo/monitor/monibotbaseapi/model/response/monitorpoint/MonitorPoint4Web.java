package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 17:20
 **/
@Data
public class MonitorPoint4Web extends TbMonitorPoint {
    private String monitorTypeName;
    private String monitorTypeAlias;

    private String monitorItemName;
    private String monitorItemAlias;

    private Boolean monitorTypeMultiSensor;

    private List<TbSensor> sensorList;
}
