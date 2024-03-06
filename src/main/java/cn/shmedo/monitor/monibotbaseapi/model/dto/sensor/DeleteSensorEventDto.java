package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.dto.ListenerEventAppend;
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
    private final ListenerEventAppend append;

    public DeleteSensorEventDto(Object source, Collection<Integer> sensorIDList, ListenerEventAppend append) {
        super(source);
        this.sensorIDList = sensorIDList;
        this.append = append;
    }
}
