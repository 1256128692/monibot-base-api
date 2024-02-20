package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-18 13:56
 */
@Getter
public class DeleteSensorEventDto extends ApplicationEvent {
    private final Collection<Integer> sensorIDList;

    public DeleteSensorEventDto(Object source, Collection<Integer> sensorIDList) {
        super(source);
        this.sensorIDList = sensorIDList;
    }
}
