package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 16:07
 */
@Data
@Builder(toBuilder = true)
public class StThematicAnalysisInfo {
    private Integer monitorPointGroupID;
    private String monitorPointGroupName;
    private String groupImage;
    private String groupConfig;
    private List<StAnalysisDataGroup> newData;
    private List<StAnalysisDataGroup> avgData;
}
