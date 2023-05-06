package cn.shmedo.monitor.monibotbaseapi.model.tempitem;

import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-05 17:09
 **/
@Data
public class SensorWithMore {
    private Integer sensorID;
    private String sensorName;
    private String uniqueToken;
    private Integer monitorPointID;
    private String monitorPointName;
    private String pointGpsLocation;
    private String pointImageLocation;

    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;

    private Integer projectID;
}
