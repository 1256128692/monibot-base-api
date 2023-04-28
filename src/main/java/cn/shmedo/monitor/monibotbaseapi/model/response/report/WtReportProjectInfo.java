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
    private String monitorTypeClass;
    private Integer total;
    private String projectName;
    private List<String> monitorTypeList;
    private final List<WtReportMonitorTypeCountInfo> monitorTypeCountList = new ArrayList<>();
    private final List<Map<String, Object>> monitorTypeDetailList = new ArrayList<>();
    @JsonIgnore
    private final Map<String, List<WtReportMonitorItemInfo>> detailNameMap = new HashMap<>();

    @JsonProperty("monitorTypeSize")
    private Integer getMonitorTypeSize() {
        return this.monitorTypeList.size();
    }

    public void addMonitorTypeList(final String monitorTypeName) {
        this.monitorTypeList.add(monitorTypeName);
    }

    public void addMonitorTypeCount(final WtReportMonitorTypeCountInfo data) {
        this.monitorTypeCountList.add(data);
    }

    public void addMonitorTypeDetailList(final String monitorTypeName, final WtReportMonitorItemInfo data) {
        if (this.detailNameMap.containsKey(monitorTypeName)) {
            this.detailNameMap.get(monitorTypeName).add(data);
        } else {
            List<WtReportMonitorItemInfo> list = new ArrayList<>();
            list.add(data);
            this.detailNameMap.put(monitorTypeName, list);
            this.monitorTypeDetailList.add(Map.of("monitorTypeName", monitorTypeName, "monitorItemList", list));
        }
    }
}
