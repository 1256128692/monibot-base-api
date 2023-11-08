package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-08 15:18
 */
@Data
public class ThematicEigenValueData {
    private Integer eigenValueID;
    private String eigenValueName;
    private Double eigenValue;
    private String chnUnit;
    private String engUnit;
}
