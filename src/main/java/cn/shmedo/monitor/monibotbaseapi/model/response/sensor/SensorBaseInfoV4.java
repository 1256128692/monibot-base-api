package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;


import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class SensorBaseInfoV4 {

    private Integer sensorID;
    private String sensorName;

    private String configFieldValue;
    private Integer monitorPointID;
    private Integer monitorType;
    private String monitorPointName;
    private String gpsLocation;

    private String imageLocation;
    private String overallViewLocation;
    private String spatialLocation;

    private Integer monitorItemID;
    private String monitorItemName;
    private Integer monitorGroupID;
    private String monitorGroupName;
    private Date dataTime;
    private Boolean multiSensor;
    private Integer dataWarnStatus;
    private Integer deviceOnlineStatus;
    private Boolean monitorPointCollection;

    private Map<String, Object> sensorData;

    private List<FieldBaseInfo> monitorTypeFields;



}
