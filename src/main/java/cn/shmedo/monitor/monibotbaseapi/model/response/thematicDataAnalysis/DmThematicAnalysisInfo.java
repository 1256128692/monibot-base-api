package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:52
 */
@Data
@Builder(toBuilder = true)
public class DmThematicAnalysisInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Map<String, Object> newData;
    private Map<String, Object> historyData;

    public void setNewData(Date time, List<DmAnalysisData> dataList) {
        this.newData = Map.of("time", time, "dataList", dataList);
    }

    public void setHistoryData(Date time, List<DmAnalysisData> dataList) {
        this.historyData = Map.of("time", time, "dataList", dataList);
    }
}
