package cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice;

import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-05 15:23
 **/
@Data
public class Device4Web {
    private String deviceSN;
    private String firewallVersion;
    private Integer productID;
    private String productName;
    private Boolean online;
    private Integer status;
    private List<Porject> projectList;

    @Data
    static class MonitorPoint {
        private Integer monitorPointID;
        private String monitorPointName;
        private String pointGpsLocation;
        private String pointImageLocation;
        private Integer monitorItemID;
        private String monitorItemName;
        private String monitorItemAlias;
    }

    @Data
    static class Porject {
        private Integer projectID;
        private String projectName;
        private String projectShortName;
        private String location;

        private List<MonitorPoint> monitorPointList;
    }
}
