package cn.shmedo.monitor.monibotbaseapi.model.response.project;


import lombok.Data;

@Data
public class MonitorItemCountStatisticsInfo {


    private Integer monitorItemID;
    private Integer monitorType;
    private String monitorItemName;
    private Long monitorPointCount;


}
