package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import lombok.Data;

@Data
public class EigenBaseInfo {

    private Integer id;
    private Integer projectID;
    private Integer scope;
    private String scopeStr;
    private Integer monitorItemID;
    private Integer monitorTypeFieldID;
    private String monitorTypeFieldName;
    private String name;
    private Double value;
    private Integer fieldUnitID;
    private String engUnit;
    private String chnUnit;

}
