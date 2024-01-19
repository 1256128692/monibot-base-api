package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnLevelAliasInfo;
import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 16:53
 */
@Data
public class DataWarnPageInfo {
    private Integer id;
    private String warnName;
    private Integer workOrderID;
    private String dealContent;
    private Integer dealUserID;
    private String dealUserName;
    private Date dealTime;
    private Integer dealStatus;
    private Date warnTime;
    private Date warnEndTime;
    private Integer projectID;
    private String projectName;
    private String projectShotName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private String gpsLocation;
    private Integer fieldID;
    private String fieldName;
    private String fieldToken;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
    private WarnLevelAliasInfo aliasConfig;
}
