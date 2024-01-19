package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 17:06
 */
@Data
public class DeviceProjectInfo {
    private Integer projectID;
    private String projectName;
    private String projectShortName;
    private String regionArea;
    private String regionAreaName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private String gpsLocation;
}
