package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:50
 */
@Data
@Builder(toBuilder = true)
public class WtReportMonitorItemInfo {
    private String monitorItemName;
    private Integer total;
    private Integer noData;
    private final List<WtReportWarn> warnCountList = new ArrayList<>();
    private final List<WtReportFormDataInfo> formList = new ArrayList<>();

    public void addWarnCount(WtReportWarn data) {
        this.warnCountList.add(data);
    }

    public void addForm(WtReportFormDataInfo data) {
        this.formList.add(data);
    }
}
