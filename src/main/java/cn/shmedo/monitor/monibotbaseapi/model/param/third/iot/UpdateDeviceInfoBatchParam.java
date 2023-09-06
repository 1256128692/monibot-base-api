package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class UpdateDeviceInfoBatchParam {

    @NotEmpty
    private List<@NotNull DeviceInfoV1> list;

}
