package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.model.response.AuthService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 11:03
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface IMultiPlatformCheck {
    Collection<Integer> getMultiPlatform();

    /**
     * (non-Javadoc)
     *
     * @see #allPlatformValid(RedisService)
     */
    default boolean allPlatformValid() {
        return allPlatformValid(ContextHolder.getBean(RedisConstant.AUTH_REDIS_SERVICE));
    }

    /**
     * 校验是否所有传入的平台都有效
     */
    default boolean allPlatformValid(RedisService redisService) {
        Collection<Integer> multiPlatform = getMultiPlatform();
        final Set<Integer> serviceIDCacheSet = redisService.multiGet(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE,
                        multiPlatform.stream().map(String::valueOf).collect(Collectors.toSet()))
                .stream().map(u -> JSONUtil.toBean(u, AuthService.class)).map(AuthService::getId).collect(Collectors.toSet());
        return serviceIDCacheSet.containsAll(multiPlatform);
    }
}
