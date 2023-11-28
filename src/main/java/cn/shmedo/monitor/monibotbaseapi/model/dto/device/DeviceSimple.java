package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.Data;

/**
 * 设备简单信息
 *
 * @author Chengfs on 2023/11/27
 */
@Data
public class DeviceSimple {

    private Integer deviceID;
    private String deviceSN;
    private String uniqueToken;
}