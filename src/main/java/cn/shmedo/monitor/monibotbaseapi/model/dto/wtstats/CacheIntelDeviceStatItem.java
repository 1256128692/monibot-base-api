package cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-02 13:27
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheIntelDeviceStatItem {
    private Integer videoDeviceCount;
    private Integer iotDeviceCount;
}
