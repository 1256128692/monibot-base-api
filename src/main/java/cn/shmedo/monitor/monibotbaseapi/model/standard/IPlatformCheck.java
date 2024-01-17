package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 10:21
 */
public interface IPlatformCheck {
    Integer getPlatform();

    @SuppressWarnings("unchecked")
    default boolean validPlatform() {
        return validPlatform(ContextHolder.getBean(RedisTemplate.class));
    }

    default boolean validPlatform(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForHash().hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, getPlatform().toString());
    }
}
