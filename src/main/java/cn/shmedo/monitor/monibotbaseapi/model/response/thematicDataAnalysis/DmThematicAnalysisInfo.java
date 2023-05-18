package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import jakarta.validation.constraints.NotNull;
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
public class DmThematicAnalysisInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Map<String, Object> newData;
    private Map<String, Object> historyData;

    public void setNewData(@NotNull final Date time, @NotNull final List<DmAnalysisData> dataList) {
        this.newData = Map.of("time", time, "dataList", dataList);
    }

    public void setHistoryData(@NotNull final Date time, @NotNull final List<DmAnalysisData> dataList) {
        this.historyData = Map.of("time", time, "dataList", dataList);
    }

    private void setNewData(final Map<String, Object> newData) {
        this.newData = newData;
    }

    private void setHistoryData(final Map<String, Object> historyData) {
        this.historyData = historyData;
    }

    @Builder(toBuilder = true)
    public DmThematicAnalysisInfo(final Integer monitorPointID, final String monitorPointName) {
        this.monitorPointID = monitorPointID;
        this.monitorPointName = monitorPointName;
    }
}
