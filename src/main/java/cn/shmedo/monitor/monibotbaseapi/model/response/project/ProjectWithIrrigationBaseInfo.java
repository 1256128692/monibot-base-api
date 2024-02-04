package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import lombok.Data;

import java.util.List;

@Data
public class ProjectWithIrrigationBaseInfo {

    private Integer projectID;

    private String projectName;

    private List<ProjectWithIrrigationInfo> dataList;

}
