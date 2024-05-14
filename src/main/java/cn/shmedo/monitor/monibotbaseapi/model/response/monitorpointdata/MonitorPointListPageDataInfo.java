package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-04-28 09:46
 */
@Data
public class MonitorPointListPageDataInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Boolean multiSensor;
    private Integer projectID;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private Date time;
    private Map<String, Object> data;
    private Map<String, Integer> maxMark;
    private Map<String, Integer> minMark;
    private List<EventBaseWithHitDateInfo> eventList;
}
