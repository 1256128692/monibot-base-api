package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 16:53
 */
@Data
public class DeviceWarnPageInfo {
    private Integer id;
    private String warnName;
    private Integer workOrderID;
    private String dealContent;
    private Integer dealUserID;
    private String dealUserName;
    private Date dealTime;
    private Integer dealStatus;
    private Date warnTime;
    private Date warnEndTime;
    private Integer deviceType;
    private String deviceToken;
    private Integer productID;
    private String deviceModel;
    private String gpsLocation;
    private String firmwareVersion;
    private List<DeviceProjectInfo> projectList;
    @JsonIgnore
    private String uniqueToken;
    @JsonIgnore
    private Integer projectID;
}
