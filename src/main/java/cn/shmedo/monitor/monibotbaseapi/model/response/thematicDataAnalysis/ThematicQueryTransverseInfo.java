package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 16:07
 */
@Data
@Builder
public class ThematicQueryTransverseInfo {
    private Date time;
    private DatumPointData datumPointData;
    private List<ThematicMonitorPointValueInfo> monitorPointList;
}
