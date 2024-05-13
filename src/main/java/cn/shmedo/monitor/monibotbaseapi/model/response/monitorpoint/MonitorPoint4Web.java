package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorConfigListResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 17:20
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorPoint4Web extends TbMonitorPoint {

    private Integer monitorTypeID;
    private String monitorTypeName;
    private String monitorTypeAlias;

    private String monitorItemName;
    private String monitorItemAlias;

    private Boolean monitorTypeMultiSensor;

    private List<TbSensor> sensorList;
    private List<SensorConfigListResponse.MonitorGroup> monitorGroupList;
}
