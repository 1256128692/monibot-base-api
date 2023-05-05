package cn.shmedo.monitor.monibotbaseapi.dal.redis.impl;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.YsTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class YsTokenDaoImpl implements YsTokenDao {
    private RedisTemplate redisTemplate;

    @Autowired
    public YsTokenDaoImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setToken(String ysToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(RedisKeys.YS_TOKEN, ysToken, DefaultConstant.YS_TOKEN_EXPIRE_DAY, TimeUnit.DAYS);
    }

    @Override
    public String getToken() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(RedisKeys.YS_TOKEN);
    }
}
