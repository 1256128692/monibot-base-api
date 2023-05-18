package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-18 13:16
 */
@Data
@Builder(toBuilder = true)
public class StAnalysisData {
    private Integer monitorPointID;
    private Integer distance;
    private Date time;
    private String monitorPointName;
    private String pointConfig;
}
