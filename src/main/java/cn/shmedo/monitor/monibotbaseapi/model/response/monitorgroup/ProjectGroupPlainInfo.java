package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-23 10:30
 */
@Data
public class ProjectGroupPlainInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Boolean monitorPointEnable;
    private Integer monitorGroupID;
    private String monitorGroupName;
    private Boolean monitorGroupEnable;
    private Integer monitorGroupParentID;
    private String monitorGroupParentName;
    private Boolean monitorGroupParentEnable;
    private Integer sensorID;
    private String sensorName;
    private String sensorAlias;
}
