package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceInfo {
    private Integer deviceID;
    private String deviceSN;
    private String uniqueToken;
    private Integer productID;
    private String productName;
    private Boolean online;
    private Double power;
    private String flowDue;
    private Date lastTime;
    private String apiKey;
    private String productType;

}
