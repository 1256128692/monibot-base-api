package cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice;

import lombok.Data;

@Data
public class OtherDeviceCountInfo {

    private Integer projectID;
    private Integer otherCount;
    private Integer otherOnlineCount;
    private Integer otherOfflineCount;


}
