package cn.shmedo.monitor.monibotbaseapi.constants;

import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis 常量类
 *
 * @author Chengfs on 2023/3/1
 */
public class RedisConstant {

    /**
     * {@link StringRedisTemplate} 的beanName后缀
     */
    public static final String TEMPLATE_SUFFIX = "RedisTemplate";

    /**
     * {@link RedisService} 的beanName后缀
     */
    public static final String SERVICE_SUFFIX = "RedisService";

    /**
     * 物联网平台redisTemplate
     */
    public static final String IOT_REDIS_TEMPLATE = "iotRedisTemplate";

    /**
     * 监测平台redisTemplate
     */
    public static final String MONITOR_REDIS_TEMPLATE = "monitorRedisTemplate";

    /**
     * 权限平台redisTemplate
     */
    public static final String AUTH_REDIS_TEMPLATE = "authRedisTemplate";

    /**
     * 监测平台redisService
     */
    public static final String MONITOR_REDIS_SERVICE = "monitorRedisService";

    /**
     * 物联网平台redisService
     */
    public static final String IOT_REDIS_SERVICE = "iotRedisService";
    /**
     * 权限平台redisService
     */
    public static final String AUTH_REDIS_SERVICE = "authRedisService";
}

    
    