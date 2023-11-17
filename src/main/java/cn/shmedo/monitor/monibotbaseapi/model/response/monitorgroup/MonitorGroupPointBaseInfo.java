package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-17 18:13
 */
@Data
public class MonitorGroupPointBaseInfo {
    private Integer monitorGroupID;
    private String monitorGroupName;
    /**
     * e.g. {"monitorPointID":1,"monitorPointName":"监测点1"}
     */
    private List<Map<String, Object>> monitorPointList;
}