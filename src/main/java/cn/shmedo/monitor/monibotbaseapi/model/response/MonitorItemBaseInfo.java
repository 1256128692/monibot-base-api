package cn.shmedo.monitor.monibotbaseapi.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MonitorItemBaseInfo {

    private Integer monitorItemID;

    private Integer projectID;

    private String name;

    private String alias;


    @JsonIgnore
    private Integer monitorType;

    private Integer monitorClass;


    private Boolean enable;

    private Integer eventID;

}
