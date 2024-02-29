package cn.shmedo.monitor.monibotbaseapi.model.response.checkevent;

import lombok.Data;

import java.util.Date;

@Data
public class TaskInfo {

    private Integer taskID;
    private String taskName;
    private String taskSerialNumber;
    private Integer checkType;
    private Integer status;
    private Date taskDate;
    private String projectID;
}
