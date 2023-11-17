package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import lombok.Data;

@Data
public class MonitorTypeFieldBaseInfo {

    private Integer monitorTypeFieldID;
    private String fieldToken;
    private String fieldName;
    private Integer monitorItemID;
    private Integer fieldUnitID;
    private String engUnit;
    private String chnUnit;


}
