package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;

import java.util.List;

@Data
public class MonitorClassInfo {

    private Integer monitorClass;

    private String monitorClassCnName;

    private List<MonitorTypeAndChildMonitorItemInfo> monitorTypeList;

}
