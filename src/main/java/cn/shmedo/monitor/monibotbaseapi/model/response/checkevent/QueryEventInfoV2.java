package cn.shmedo.monitor.monibotbaseapi.model.response.checkevent;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryEventInfoV2 {

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
    private Integer handleUserID;
    private String handleUserName;
    private String comment;
    private String exValue;

    private Integer checkerID;
    private String checkerName;
    private Date taskBeginTime;

    private Date taskEndTime;

    private Integer orderID;
    private Integer instanceID;

    private String annexes;

    private List<FileInfoResponse> fileInfoList;

}
