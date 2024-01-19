package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.iot.entity.api.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 15:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDeviceWarnPageParam extends QueryWarnPageBaseParam {
    @Range(min = 1, max = 2, message = "设备类型 1.物联网设备 2.视频设备")
    private Integer deviceType;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        return null;
    }
}
