package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-14 09:53
 */
@Data
public class ThematicEigenValueInfo {
    private Integer projectID;
    private Integer monitorItemID;
    private Integer monitorPointID;
    private Integer monitorType;
    private String fieldToken;
    private Integer eigenValueID;
    private String eigenValueName;
    private Double eigenValue;
    private String chnUnit;
    private String engUnit;
}
