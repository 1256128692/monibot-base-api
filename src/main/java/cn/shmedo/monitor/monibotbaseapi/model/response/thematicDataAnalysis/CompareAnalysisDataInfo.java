package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-22 15:25
 */
@Data
@Builder
public class CompareAnalysisDataInfo {
    private Integer autoCount;
    private Integer manualCount;
    private Integer abnormalCount;
    private Double abnormalRatio;
    private List<CompareAnalysisData> dataList;
}
