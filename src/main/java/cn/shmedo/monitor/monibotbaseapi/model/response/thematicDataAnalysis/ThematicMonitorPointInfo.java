package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-22 11:57
 */
@Data
@Builder(toBuilder = true)
public class ThematicMonitorPointInfo {
    private Integer thematicType;
    private List<Map<String, Object>> dataList;
}
