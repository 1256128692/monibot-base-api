package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import lombok.Data;

@Data
public class EventBaseInfo {

    private Integer id;
    private String eventName;
    private Integer frequency;
    private String frequencyStr;
    private String timeRange;

}
