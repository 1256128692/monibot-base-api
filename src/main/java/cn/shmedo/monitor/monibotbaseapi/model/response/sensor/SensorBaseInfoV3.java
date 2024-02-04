package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class SensorBaseInfoV3 {

    private Integer companyID;
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private Integer projectID;
    private Integer projectTypeID;
    private String projectName;
    private String projectTypeName;
    private String waterMeasuringTypeName;
    private Integer kind;
    private Date time;
    private List<Map<String, Object>> sensorDataList;

    private IrrigatedAreaInfo data;
}
