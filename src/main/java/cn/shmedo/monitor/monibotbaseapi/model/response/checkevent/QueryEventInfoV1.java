package cn.shmedo.monitor.monibotbaseapi.model.response.checkevent;

import lombok.Data;

import java.util.Date;

@Data
public class QueryEventInfoV1 {

    private Integer eventID;
    private Integer status;
    private String eventSerialNumber;
    private Integer projectID;
    private String projectName;
    private Integer taskID;
    private String taskName;
    private String taskSerialNumber;
    private Integer typeID;
    private String typeName;
    private String address;
    private String location;
    private String describe;
    private Integer reportUserID;
    private String reportUserName;
    private Date reportTime;
    private Date handleTime;
    private String comment;
    private String exValue;

}
