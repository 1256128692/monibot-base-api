package cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent;

import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryDataEventInfo {


    private Integer id;
    private Integer projectID;
    private String name;
    private Integer frequency;
    private String frequencyStr;
    private String timeRange;
    private String exValue;

    private List<MonitorItemBaseInfo> monitorItemList;
    private Date createTime;

}
