package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ProjectWithIrrigationInfo {

    @JsonIgnore
    private Integer projectID;
    private List<String> raiseCropNameList;
    private String value;

    private String keyName;
}
