package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import lombok.Data;

/**
 * @author Chengfs on 2023/12/14
 */
@Data
public class MonitorPointSimple {

    private Integer id;

    private Integer projectID;

    private Integer monitorType;

    private Integer monitorItemID;

    private String name;

    private Integer projectType;

    private String projectTypeName;

    private String projectName;
}