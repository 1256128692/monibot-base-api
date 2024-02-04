package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

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
public class DeviceOnlineRes {
    private String date;
    private Double onlineRate;
}
