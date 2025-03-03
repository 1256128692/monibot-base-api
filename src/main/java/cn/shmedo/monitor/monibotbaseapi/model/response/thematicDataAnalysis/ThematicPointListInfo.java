package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-08 15:17
 */
@Data
public class ThematicPointListInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Boolean monitorPointEnable;
    private Integer monitorType;
    private Integer monitorItemID;
    private Integer sensorID;
}
