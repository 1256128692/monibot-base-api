package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-28 16:28
 */
@Data
@Builder
public class CompareAnalysisData {
    private Date autoTime;
    private Date manualTime;
    private Boolean normal;
    private Double autoData;
    private Double manualData;
    private String chnUnit;
    private String engUnit;
    private Double abnormalValue;
}
