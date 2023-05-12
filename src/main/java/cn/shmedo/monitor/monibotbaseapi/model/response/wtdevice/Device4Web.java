package cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-05 15:23
 **/
@Data
@Builder
public class Device4Web {
    private Integer deviceID;
    private String deviceSN;
    private String firewallVersion;
    private Integer productID;
    private String productName;
    private Boolean online;
    private Integer status;
    private Date lastActiveTime;
    private Date createTime;
    private List<Porject> projectList;

    @Data
    @Builder
    public static class MonitorPoint {
        private Integer monitorPointID;
        private String monitorPointName;
        private String pointGpsLocation;
        private String pointImageLocation;
        private Integer monitorItemID;
        private String monitorItemName;
        private String monitorItemAlias;
    }

    @Data
    @Builder
    public static class Porject {
        private Integer projectID;
        private String projectName;
        private String projectShortName;
        private String location;
        private String projectAddress;

        private List<MonitorPoint> monitorPointList;
    }
}
