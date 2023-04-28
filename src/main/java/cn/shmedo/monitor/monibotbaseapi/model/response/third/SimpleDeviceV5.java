package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import lombok.Data;

import java.util.Date;

/**
 * 物联网 设备简单信息
 *
 * @author Chengfs on 2023/4/28
 */
@Data
public class SimpleDeviceV5 {
    private Integer deviceID;
    private String deviceToken;
    private String uniqueToken;
    private String deviceName;
    private Integer productID;
    private String productToken;
    private String  productName;
    private String  productType;
    private String  firmwareVersion;
    private Boolean onlineStatus;
    private Date lastActiveTime;
}