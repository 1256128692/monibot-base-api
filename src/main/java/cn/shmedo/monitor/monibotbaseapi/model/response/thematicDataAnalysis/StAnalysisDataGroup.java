package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-18 13:24
 */
@Data
@Builder(toBuilder = true)
public class StAnalysisDataGroup {
    private Integer distance;
    private Date time;
    private List<StAnalysisData> dataList;
}
