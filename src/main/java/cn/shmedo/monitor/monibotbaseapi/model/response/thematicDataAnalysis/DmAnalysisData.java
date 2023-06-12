package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:52
 */
@Data
@NoArgsConstructor
public class DmAnalysisData {
    private Integer sensorID;
    private String sensorName;
    private String fieldToken;
    private Double totalValue;
    private Double segmentValue = 0d;
    @JsonIgnore
    private Date groupTime;
    private String config;

    @Builder
    public DmAnalysisData(Integer sensorID, String sensorName, String fieldToken, Double totalValue, Date groupTime, String config) {
        this.sensorID = sensorID;
        this.sensorName = sensorName;
        this.fieldToken = fieldToken;
        this.totalValue = totalValue;
        this.groupTime = groupTime;
        this.config = config;
    }
}
