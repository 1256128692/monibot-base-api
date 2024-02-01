package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Builder;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 17:33
 **/
@Data
@Builder
public class PointWithProjectInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer projectID;
    private String projectName;
    private String shortName;
}
