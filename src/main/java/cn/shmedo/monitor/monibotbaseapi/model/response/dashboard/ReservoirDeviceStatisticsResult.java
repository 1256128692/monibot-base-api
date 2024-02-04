package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Builder;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-01 10:18
 **/
@Data
@Builder
public class ReservoirDeviceStatisticsResult {
    private Integer videoDeviceCount;
    private Integer iotDeviceCount;
}
