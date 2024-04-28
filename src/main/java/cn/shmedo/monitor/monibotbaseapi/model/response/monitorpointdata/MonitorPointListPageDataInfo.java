package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import lombok.Data;

import java.util.Date;

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
    private Date time;
    private Object data;
    private String fieldToken;
    private String fieldName;
    private String engUnit;
    private String chnUnit;
    private String unitClass;
    private String unitDesc;
}
