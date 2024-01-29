package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnBaseConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnLevelAliasInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 13:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataWarnDetailInfo extends WarnBaseConfigInfo {
    private Integer id;
    private String warnName;
    private Integer workOrderID;
    private String dealContent;
    private Integer dealUserID;
    private String dealUserName;
    private Date dealTime;
    private WarnLevelAliasInfo aliasConfig;
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
}
