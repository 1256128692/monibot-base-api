package cn.shmedo.monitor.monibotbaseapi.model.param.third.wt;

import lombok.Builder;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-01 13:10
 **/
@Data
@Builder
public class QueryReservoirResponsibleListRequest {
    private Integer companyID;
    private Integer projectID;
    private Integer monitorType;
    private Integer type;
    private String exValue;
    private Integer displayOrder;
}
