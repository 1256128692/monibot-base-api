package cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent;

import lombok.Data;

@Data
public class QueryDataEventInfo {


    private Integer id;
    private Integer projectID;
    private String name;
    private Integer frequency;
    private String frequencyStr;
    private String timeRange;
    private String exValue;

}
