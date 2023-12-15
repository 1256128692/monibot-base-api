package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SensorBaseInfoV3 {

    private Integer companyID;
    private Integer monitorPointID;
    private Integer monitorPointName;
    private Integer monitorType;
    private Integer monitorTypeName;
    private Integer monitorTypeAlias;
    private Integer sensorID;
    private Integer sensorName;
    private Integer sensorAlias;
    private Integer projectID;
    private Integer projectTypeID;
    private Integer projectTypeName;
    private String waterMeasuringTypeName;
    private Integer kind;
    private List<Map<String, Object>> sensorDataList;

}
