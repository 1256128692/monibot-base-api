package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 16:43
 */
@Data
@Builder(toBuilder = true)
public class CompanyMonitorItemNameInfo {
    private String MonitorClassName;
    private List<MonitorTypeItemNameInfo> dataList;
}
