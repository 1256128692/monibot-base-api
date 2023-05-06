package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 14:16
 */
@Data
@Builder(toBuilder = true)
public class WtReportProjectInfo {
    private String monitorClass;
    private Integer total;
    private String projectName;
    private List<String> monitorTypeList;
    private List<WtReportMonitorTypeCountInfo> monitorTypeCountList;
    private final List<Map<String, Object>> monitorTypeDetailList = new ArrayList<>();
    @JsonIgnore
    private final Map<String, List<WtReportMonitorItemInfo>> detailNameMap = new HashMap<>();

    @JsonProperty("monitorTypeSize")
    private Integer getMonitorTypeSize() {
        return this.monitorTypeList.size();
    }

    public void addMonitorTypeDetail(final String monitorTypeName, final List<WtReportMonitorItemInfo> data) {
        if (this.detailNameMap.containsKey(monitorTypeName)) {
            this.detailNameMap.get(monitorTypeName).addAll(data);
        } else {
            List<WtReportMonitorItemInfo> list = new ArrayList<>(data);
            this.detailNameMap.put(monitorTypeName, list);
            this.monitorTypeDetailList.add(Map.of("monitorTypeName", monitorTypeName, "monitorItemList", list));
        }
    }
}
