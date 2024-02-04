package cn.shmedo.monitor.monibotbaseapi.model.param.third.wt;

import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-01 13:12
 **/
@Data
public class QueryReservoirResponsibleListResponseItem {
    private Integer id;
    private Integer projectID;
    private Integer monitorType;
    private Integer type;
    private Integer userID;
    private String user;
    private Integer initID;
    private String unit;
    private String position;
    private String cellPhone;
}
