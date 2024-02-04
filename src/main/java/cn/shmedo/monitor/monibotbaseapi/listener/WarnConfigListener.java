package cn.shmedo.monitor.monibotbaseapi.listener;

import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnConfigClearDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnConfigEventDto;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 11:46
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WarnConfigListener {
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private final RedisService monitorRedisService;

    // 先暂时不用异步,等有需要再用.给一个统一的入口,也方便查找在哪些地方更新了报警阈值
    // @Async
    @EventListener
    public void listenSaveWarnConfigCacheEvent(WarnConfigEventDto dto) {
        String key = dto.getKey();
        String hashKey = dto.getHashKey();
        Long expires = dto.getExpires();
        Object value = dto.getValue();
        if (Objects.isNull(hashKey)) {
            if (Objects.isNull(expires)) {
                monitorRedisService.set(key, value);
            } else {
                monitorRedisService.set(key, value, expires);
            }
        } else {
            monitorRedisService.put(key, hashKey, value);
        }
    }

    @EventListener
    public void listenClearWarnEvent(WarnConfigClearDto dto) {
        monitorRedisService.del(dto.getKeys());
    }
}
