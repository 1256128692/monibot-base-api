package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:52
 */
@Data
public class DmThematicAnalysisInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private List<Map<String, Object>> historyData;

    @Builder(toBuilder = true)
    public DmThematicAnalysisInfo(final Integer monitorPointID, final String monitorPointName) {
        this.monitorPointID = monitorPointID;
        this.monitorPointName = monitorPointName;
    }
}
