package cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Arrays;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-24 14:58
 */
@Getter
public class WarnConfigClearDto extends ApplicationEvent {
    private final List<String> keys;

    public WarnConfigClearDto(Object source, String... keys) {
        super(source);
        this.keys = Arrays.stream(keys).toList();
    }
}
