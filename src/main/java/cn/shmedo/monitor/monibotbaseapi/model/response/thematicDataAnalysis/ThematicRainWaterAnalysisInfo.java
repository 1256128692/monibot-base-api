package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 15:25
 */
@Data
@Builder
public class ThematicRainWaterAnalysisInfo {
    private List<ThematicRainWaterDataInfo> dataList;
    private List<Map<String, Object>> maxDataList;
    private List<ThematicEigenValueData> eigenvalueDataList;
    private List<Map<String, Object>> dataEventDataList;
}
