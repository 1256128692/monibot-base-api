package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import lombok.Data;

@Data
public class DeviceAssetsStatisticsInfo {

    private Integer projectID;
    private Integer intelligenceCount;
    private Integer intelligenceOnlineCount;
    private Integer intelligenceOfflineCount;
    private Integer videoCount;
    private Integer videoOnlineCount;
    private Integer videoOfflineCount;
    private Integer otherCount;

}
