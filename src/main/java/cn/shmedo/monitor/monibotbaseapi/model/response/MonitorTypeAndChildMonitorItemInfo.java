package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;

import java.util.List;

@Data
public class MonitorTypeAndChildMonitorItemInfo {

    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;

    private List<MonitorItemBaseInfo> monitorItemList;
}
