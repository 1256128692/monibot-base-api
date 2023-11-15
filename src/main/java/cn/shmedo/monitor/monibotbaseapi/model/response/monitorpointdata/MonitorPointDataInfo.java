package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MonitorPointDataInfo {

    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Boolean multiSensor;
    private List<SensorBaseInfoResponse> sensorList;

    private List<FieldBaseInfo> fieldList;

    /**
     * 特征值列表
     */
    private List<EigenBaseInfo> eigenValueList;

    /**
     * 大事记名称
     */
    private List<EventBaseInfo> eventList;


}
