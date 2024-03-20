package cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<String> videoDeviceTokens;
    private List<String> iotDeviceTokens;
}
