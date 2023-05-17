package cn.shmedo.monitor.monibotbaseapi.model.response.project;


import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithSensor;
import lombok.Data;

import java.util.List;

@Data
public class QueryProjectBaseInfoResponse {

    private Integer projectID;

    private Integer projectType;

    private String projectName;

    private String projectShortName;

    private List<MonitorPointWithSensor> monitorPointList;


}
