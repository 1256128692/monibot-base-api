package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:10
 */
@Data
@Builder(toBuilder = true)
public class WtQueryReportInfo {
    private Date startTime;
    private Date endTime;
    private String period;
    private String companyName;
    private Integer total;
    private List<String> monitorTypeClassList;
    private List<WtReportProjectInfo> dataList;
    private final List<WtReportProjectInfo> projectDataList = new ArrayList<>();

    @JsonProperty("monitorTypeClassSize")
    private Integer getMonitorTypeClassSize() {
        return this.monitorTypeClassList.size();
    }

    public void addMonitorTypeClass(final String typeClassName) {
        this.monitorTypeClassList.add(typeClassName);
    }

    public void addProjectData(final WtReportProjectInfo data) {
        this.projectDataList.add(data);
    }
}
