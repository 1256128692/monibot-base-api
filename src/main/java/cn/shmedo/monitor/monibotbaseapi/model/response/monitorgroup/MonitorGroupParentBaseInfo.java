package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-23 10:18
 */
@Data
@Builder(toBuilder = true)
public class MonitorGroupParentBaseInfo {
    private Integer monitorGroupParentID;
    private String monitorGroupParentName;
    private Boolean monitorGroupParentEnable;
    private List<MonitorGroupBaseInfo> monitorGroupDataList;
}
