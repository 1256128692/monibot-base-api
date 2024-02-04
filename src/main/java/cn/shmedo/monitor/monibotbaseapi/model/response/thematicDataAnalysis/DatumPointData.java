package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 18:01
 */
@Data
@Builder
public class DatumPointData {
    private Integer monitorPointID;
    private String monitorPointName;
    private Double upper;
    private Double lower;
    private Double value;
}
