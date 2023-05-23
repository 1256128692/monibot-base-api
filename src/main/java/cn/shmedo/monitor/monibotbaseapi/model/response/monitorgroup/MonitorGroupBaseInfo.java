package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-23 10:20
 */
@Data
@Builder(toBuilder = true)
public class MonitorGroupBaseInfo {
    private Integer monitorGroupID;
    private String monitorGroupName;
    private Boolean monitorGroupEnable;
    private List<MonitorPointBaseInfo> monitorPointDataList;
}
