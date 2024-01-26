package cn.shmedo.monitor.monibotbaseapi.model.cache;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author wuxl
 * @Date 2024/1/25 11:51
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: DeviceOnlineRes
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class DeviceOnlineCache {
    private String date;
    private long online;
    private long offline;
}
