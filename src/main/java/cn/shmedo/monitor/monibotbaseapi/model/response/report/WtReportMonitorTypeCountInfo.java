package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:30
 */
@Data
@Builder(toBuilder = true)
public class WtReportMonitorTypeCountInfo {
    private String monitorTypeName;
    private Integer total;
    private Integer noData;
    private final List<WtReportWarn> warnCountList = new ArrayList<>();

    @JsonProperty("rate")
    private Double getRate() {
        return getNormal() * 100.0 / total;
    }

    @JsonProperty("abnormal")
    public Integer getAbnormal() {
        return noData + warnCountList.stream().map(WtReportWarn::getTotal).reduce(Integer::sum).orElse(0);
    }

    @JsonProperty("normal")
    public Integer getNormal() {
        return total - getAbnormal();
    }

    public void addWarnCount(WtReportWarn data) {
        this.warnCountList.add(data);
    }
}
