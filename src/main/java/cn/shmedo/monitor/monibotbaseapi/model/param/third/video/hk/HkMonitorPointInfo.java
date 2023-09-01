package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk;

import lombok.Data;

import java.util.List;

@Data
public class HkMonitorPointInfo {

    private int total;
    private int pageNo;
    private int pageSize;
    private List<MonitorPointDetail> list;


    @Data
    public static class MonitorPointDetail {
        private String altitude;
        private String cameraIndexCode;
        private String cameraName;
        private int cameraType;
        private String cameraTypeName;
        private String capabilitySet;
        private String capabilitySetName;
        private String intelligentSet;
        private String intelligentSetName;
        private String channelNo;
        private String channelType;
        private String channelTypeName;
        private String createTime;
        private String encodeDevIndexCode;
        private String encodeDevResourceType;
        private String encodeDevResourceTypeName;
        private String gbIndexCode;
        private String installLocation;
        private String keyBoardCode;
        private String latitude;
        private String longitude;
        private int pixel;
        private int ptz;
        private int ptzController;
        private String ptzControllerName;
        private String ptzName;
        private String recordLocation;
        private String recordLocationName;
        private String regionIndexCode;
        private int status;
        private String statusName;
        private int transType;
        private String transTypeName;
        private String treatyType;
        private String treatyTypeName;
        private String viewshed;
        private String updateTime;

    }
}
