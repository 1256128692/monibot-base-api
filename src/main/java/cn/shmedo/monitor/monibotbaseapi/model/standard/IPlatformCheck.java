package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 10:21
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface IPlatformCheck {
    Integer getPlatform();

    /**
     * (non-Javadoc)
     *
     * @see #validPlatform(RedisService)
     */
    default boolean validPlatform() {
        return validPlatform(ContextHolder.getBean(RedisConstant.AUTH_REDIS_SERVICE));
    }

    /**
     * 校验传入的平台是否有效
     */
    default boolean validPlatform(RedisService redisService) {
        return redisService.hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, getPlatform().toString());
    }
}
