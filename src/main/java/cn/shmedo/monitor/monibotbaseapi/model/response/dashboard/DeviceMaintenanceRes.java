package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author wuxl
 * @Date 2024/1/24 16:19
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: DeviceMaintenanceRes
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class DeviceMaintenanceRes {
    private long activeCount;
    private long activeSuccessCount;
    private long otaCount;
    private long otaSuccessCount;
    private long execCount;
    private long execSuccessCount;
    private long onlineCount;
    private long offlineCount;
    private Double deviceOnlineRate;
}
