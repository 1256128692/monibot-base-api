package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

@Data
public class MonitorPointBaseInfoV2 {

    private Integer monitorPointID;
    private String monitorPointName;
    private Boolean monitorPointEnable;
    private Integer monitorType;
    private Integer monitorItemID;

    private Integer eigenValueID;
}
