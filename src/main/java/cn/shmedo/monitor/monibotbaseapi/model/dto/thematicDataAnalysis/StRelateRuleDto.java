package cn.shmedo.monitor.monibotbaseapi.model.dto.thematicDataAnalysis;

import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-29 15:37
 */
@Data
public class StRelateRuleDto {
    private Integer monitorPointID;
    private Boolean monitorPointEnable;
    private Double upperLimit;
}
