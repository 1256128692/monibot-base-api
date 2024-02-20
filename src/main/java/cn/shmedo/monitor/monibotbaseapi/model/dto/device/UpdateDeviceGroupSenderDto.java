package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.UpdateDeviceGroupSenderEventParam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-18 14:47
 */
@Getter
public class UpdateDeviceGroupSenderDto extends ApplicationEvent {
    private final List<UpdateDeviceGroupSenderEventParam> dataList;

    public UpdateDeviceGroupSenderDto(Object source, List<UpdateDeviceGroupSenderEventParam> dataList) {
        super(source);
        this.dataList = dataList;
    }
}
