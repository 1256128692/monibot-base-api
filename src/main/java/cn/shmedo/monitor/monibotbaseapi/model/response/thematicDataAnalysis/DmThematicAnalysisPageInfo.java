package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-29 10:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DmThematicAnalysisPageInfo extends DmAnalysisData {
    private Integer monitorPointID;
    private String monitorPointName;
    private Date time;

    @Builder(builderMethodName = "pageFieldBuilder")
    public DmThematicAnalysisPageInfo(Integer sensorID, String sensorName, String fieldToken, Double totalValue,
                                      Date groupTime, String config, Integer monitorPointID, String monitorPointName) {
        super(sensorID, sensorName, fieldToken, totalValue, groupTime, config);
        this.monitorPointID = monitorPointID;
        this.monitorPointName = monitorPointName;
        this.time = groupTime;
    }

    @Builder(builderMethodName = "pageParentBuilder")
    public DmThematicAnalysisPageInfo(DmAnalysisData parent, Integer monitorPointID, String monitorPointName) {
        super(parent.getSensorID(), parent.getSensorName(), parent.getFieldToken(), parent.getTotalValue(),
                parent.getGroupTime(), parent.getConfig());
        this.monitorPointID = monitorPointID;
        this.monitorPointName = monitorPointName;
        this.time = parent.getGroupTime();
    }
}
