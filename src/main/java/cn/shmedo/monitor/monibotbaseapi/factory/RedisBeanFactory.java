package cn.shmedo.monitor.monibotbaseapi.factory;


import cn.shmedo.monitor.monibotbaseapi.config.RedisDataSourceConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import lombok.AllArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisBeanFactory implements BeanFactoryAware, InstantiationAwareBeanPostProcessor {

    private final RedisDataSourceConfig redisDataSourceConfig;


    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        redisDataSourceConfig.getRedis().forEach((name, dataSource) -> {
            StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
            RedisConnectionFactory redisConnection = getRedisConnection(dataSource);
            stringRedisTemplate.setConnectionFactory(redisConnection);
            stringRedisTemplate.afterPropertiesSet();

            // 向ioc容器中注入StringRedisTemplate
            String templateName = name + RedisConstant.TEMPLATE_SUFFIX;
            listableBeanFactory.registerBeanDefinition(templateName,  BeanDefinitionBuilder
                    .rootBeanDefinition(StringRedisTemplate.class)
                    .setPrimary(dataSource.isPrimary())
                    .getBeanDefinition());
            listableBeanFactory.registerSingleton(templateName, stringRedisTemplate);

            // 向ioc容器中注入RedisService
            String serviceName = name + RedisConstant.SERVICE_SUFFIX;
            RedisService redisService = new RedisService(stringRedisTemplate);
            listableBeanFactory.registerBeanDefinition(serviceName, BeanDefinitionBuilder
                    .rootBeanDefinition(RedisService.class)
                    .setPrimary(dataSource.isPrimary())
                    .getBeanDefinition());
            listableBeanFactory.registerSingleton(serviceName, redisService);
        });
    }




    /**
     * 配置redisConnection
     *
     * @param dataSource {@link RedisProperties}
     */
    private RedisConnectionFactory getRedisConnection(RedisProperties dataSource) {
        return getLettuceConnectionFactory(dataSource);
    }

    /**
     * 获取LettuceConnection
     *
     * @param dataSource {@link RedisProperties}
     */
    private RedisConnectionFactory getLettuceConnectionFactory(RedisProperties dataSource) {
        RedisStandaloneConfiguration redisConfig = getRedisConfig(dataSource);
        RedisProperties.Pool pool = dataSource.getLettuce().getPool();
        GenericObjectPoolConfig<?> poolConfig = getPoolConfig(pool);
        LettucePoolingClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).build();
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfig, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * 获取连接池配置
     *
     * @param pool {@link RedisProperties.Pool}
     */
    private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool pool) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            poolConfig.setTimeBetweenEvictionRuns(pool.getTimeBetweenEvictionRuns());
        }
        if (pool.getMaxWait() != null) {
            poolConfig.setMaxWait(pool.getMaxWait());
        }
        return poolConfig;
    }

    /**
     * 获取redis连接配置
     *
     * @param dataSource {@link RedisProperties}
     */
    private RedisStandaloneConfiguration getRedisConfig(RedisProperties dataSource) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(dataSource.getHost());
        redisConfig.setPort(dataSource.getPort());
        redisConfig.setUsername(dataSource.getUsername());
        redisConfig.setPassword(RedisPassword.of(dataSource.getPassword()));
        redisConfig.setDatabase(dataSource.getDatabase());
        return redisConfig;
    }

}
