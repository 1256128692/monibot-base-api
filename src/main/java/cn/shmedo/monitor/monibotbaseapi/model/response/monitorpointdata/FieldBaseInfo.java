package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import lombok.Data;

@Data
public class FieldBaseInfo {

    private String fieldToken;

    private String fieldName;

    private Integer fieldUnitID;

    private String engUnit;

    private String chnUnit;

    private String unitClass;

    private String unitDesc;

    private Integer displayOrder;

}
