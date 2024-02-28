package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-28 14:09
 */
@Data
@Builder
public class LongitudinalDataInfo {
    private Date time;
    private Integer wtPointID;
    private String wtPointName;
    private Double wtPointValue;
    private List<ThematicPipeData> pipeDataList;
}
