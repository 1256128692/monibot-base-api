package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新传感器 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateSensorRequest extends SaveSensorRequest {

    private Boolean enable;

    @NotNull(message = "传感器ID不能为空")
    private Integer sensorID;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

}

    
    