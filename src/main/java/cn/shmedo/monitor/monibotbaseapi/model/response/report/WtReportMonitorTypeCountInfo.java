package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:30
 */
@Data
@Builder(toBuilder = true)
public class WtReportMonitorTypeCountInfo {
    private String monitorTypeName;
    private Integer total;
    private Integer normal;
    private Integer abnormal;
    private Integer noData;
    private Double rate;
    private final List<WtReportWarn> warnCountList = new ArrayList<>();

    public void addWarnCount(WtReportWarn data) {
        this.warnCountList.add(data);
    }
}
