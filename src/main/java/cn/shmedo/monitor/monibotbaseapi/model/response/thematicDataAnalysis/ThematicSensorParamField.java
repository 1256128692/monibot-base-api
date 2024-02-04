package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-21 11:15
 */
@Data
public class ThematicSensorParamField {
    private Integer id;
    private Integer subjectID;
    private Integer subjectType;
    private String dataType;
    private String token;
    private String name;
    private String paValue;
    private String paDesc;
    private String chnUnit;
    private String engUnit;
}
