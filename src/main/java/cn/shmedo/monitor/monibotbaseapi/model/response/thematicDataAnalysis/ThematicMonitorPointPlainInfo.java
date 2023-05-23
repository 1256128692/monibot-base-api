package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.enums.ThematicPlainMonitorItemEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-22 13:53
 */
@Data
@Builder(toBuilder = true)
public class ThematicMonitorPointPlainInfo {
    private Integer thematicType;
    /**
     * @see ThematicPlainMonitorItemEnum
     */
    private ThematicPlainMonitorItemEnum monitorItemEnum;
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorType;
}
