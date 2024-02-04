package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 10:21
 */
public interface IPlatformCheck {
    Integer getPlatform();

    default boolean validPlatform() {
        return validPlatform(ContextHolder.getBean(RedisConstant.AUTH_REDIS_SERVICE));
    }

    default boolean validPlatform(RedisService redisService) {
        return redisService.hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, getPlatform().toString());
    }
}
