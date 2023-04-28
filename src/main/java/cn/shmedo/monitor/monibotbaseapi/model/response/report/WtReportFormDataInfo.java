package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:35
 */
@Data
@Builder(toBuilder = true)
public class WtReportFormDataInfo {
    private String monitorPointName;
    private String monitorTypeName;
    private String monitorItemName;
    private String projectTypeName;
    private String projectName;
    private String areaName;
    private Date time;
    private final List<Map<String, ?>> fieldDataList = new ArrayList<>();

    public void addFieldDataList(final String key, final Object value) {
        this.fieldDataList.add(Map.of("key", key, "value", value));
    }
}
