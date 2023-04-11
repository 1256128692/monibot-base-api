package cn.shmedo.monitor.monibotbaseapi.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = RedisDataSourceConfig.PREFIX)
public class RedisDataSourceConfig {
 
    public static final String PREFIX = "spring.data";
 
    private Map<String, RedisConfig> redis;

    public static class RedisConfig extends RedisProperties {

        private Boolean primary = false;

        public boolean isPrimary() {
            return Boolean.TRUE.equals(primary);
        }

        public void setPrimary(Boolean primary) {
            this.primary = primary;
        }
    }
 
}