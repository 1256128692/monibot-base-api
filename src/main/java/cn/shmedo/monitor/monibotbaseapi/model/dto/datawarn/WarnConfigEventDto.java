package cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 11:48
 */
@Getter
public class WarnConfigEventDto extends ApplicationEvent {
    private final String key;
    private final String hashKey;
    private final Long expires;
    private final Object value;

    /**
     * @param key     缓存key
     * @param hashKey 缓存hashKey,为空时表示该项是大key下的项
     * @param value   值
     */
    public WarnConfigEventDto(Object source, String key, @Nullable String hashKey, @Nullable Long expires, Object value) {
        super(source);
        this.key = key;
        this.hashKey = hashKey;
        this.expires = expires;
        this.value = value;
    }
}
