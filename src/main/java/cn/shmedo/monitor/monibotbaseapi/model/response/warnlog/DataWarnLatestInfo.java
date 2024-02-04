package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-26 14:28
 */
@Data
public class DataWarnLatestInfo {
    private Integer warnLogID;
    private Integer notifyID;
    private String warnName;
    private Date warnTime;
    private Integer projectID;
    private String projectName;
    private String projectShortName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
}
