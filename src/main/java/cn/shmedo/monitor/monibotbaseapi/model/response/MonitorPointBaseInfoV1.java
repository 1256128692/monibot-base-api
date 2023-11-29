package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;

@Data
public class MonitorPointBaseInfoV1 {

    private Integer monitorPointID;

    private Integer monitorItemID;
    private Integer groupID;

    private String name;

    private Boolean enable;

}
