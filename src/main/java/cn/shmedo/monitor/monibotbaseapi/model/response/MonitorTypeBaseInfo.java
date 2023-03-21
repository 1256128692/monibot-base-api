package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;

@Data
public class MonitorTypeBaseInfo {

    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer pointCount;
}
