package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;


import lombok.Data;

@Data
public class SensorWithDataSourceInfo {

    private Integer sensorID;

    private String sensorName;

    private String dataSourceID;

    private Integer dataSourceComposeType;

    private String dataSourceToken;

    private String uniqueToken;

    private Boolean onlineStatus;
}
