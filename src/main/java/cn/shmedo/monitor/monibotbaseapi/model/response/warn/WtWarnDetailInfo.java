package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-14 17:13
 */
@Data
public class WtWarnDetailInfo {
    private Integer warnID;
    private String warnName;
    private Integer projectID;
    private String projectName;
    private Integer monitorTypeID;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer monitorItemID;
    private String monitorItemName;
    private Integer warnLevel;
    private String warnContent;
    private Integer monitorPointID;
    private String monitorPointName;
    private String monitorPointLocation;
    private String installLocation;
    private Date warnTime;
    private Integer metadataID;
    private String compareRule;
    private String triggerRule;
    private Integer workOrderID;
    private String workOrderCode;
}
