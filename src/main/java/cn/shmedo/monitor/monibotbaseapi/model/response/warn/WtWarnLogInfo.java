package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-2023-04-14 17:08
 */
@Data
public class WtWarnLogInfo {
    private Integer warnID;
    private String warnName;
    private Integer warnLevel;
    private Date warnTime;
    private String warnContent;
    private Integer projectID;
    private String projectName;
    private Integer monitorItemID;
    private String monitorItemName;
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorTypeID;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer workOrderID;
    private String orderCode;
    private Integer orderStatus;
}
