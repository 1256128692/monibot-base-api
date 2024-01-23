package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import lombok.Data;

@Data
public class DataCountStatisticsInfo {

    private Integer dataCount;
    private Long monitorItemCount;
    private Long monitorPointTotalCount;


}
