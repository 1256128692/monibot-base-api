package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

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
    private Integer period;
    private String companyName;
    private Integer total;
    private List<String> monitorClassList;
    private List<WtReportProjectInfo> dataList;
    private List<WtReportProjectInfo> projectDataList;

    @JsonProperty("monitorClassSize")
    private Integer getMonitorTypeClassSize() {
        return this.monitorClassList.size();
    }
}
