package cn.shmedo.monitor.monibotbaseapi.model.response.workorder;

import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-18 18:01
 */
@Data
public class WtWorkOrderWarnDetail {
    private Integer projectID;
    private String projectName;
    private Integer monitorTypeID;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private String monitorPointLocation;
    private String installLocation;
    private String warnName;
    private Integer warnLevel;
    private Date warnTime;
    private String warnContent;
    private String deviceToken;
    private String deviceTypeName;
    private String regionArea;
}
