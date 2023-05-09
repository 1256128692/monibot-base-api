package cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class WtVideoPageInfo {


    /**
     * 设备SN
     */
    private String videoSN;
    /**
     * 设备型号
     */
    private String videoType;
    /**
     * 在线状态 在线:true 离线:false
     */
    private Boolean online;
    /**
     * 设备状态 0.正常 1.异常
     */
    private String status;
    private Integer projectID;
    private String projectName;
    private String projectShortName;
    /**
     * 工程项目行政区划
     */
    private String location;
    private String locationInfo;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private String pointGpsLocation;
    private String pointImageLocation;


    @JsonIgnore
    private String exValues;

}
