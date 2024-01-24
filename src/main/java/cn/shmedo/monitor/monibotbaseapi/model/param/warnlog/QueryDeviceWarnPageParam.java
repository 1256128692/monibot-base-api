package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public QueryWtTerminalWarnLogPageParam build() {
        QueryWtTerminalWarnLogPageParam param = new QueryWtTerminalWarnLogPageParam();
        Optional.ofNullable(getProjectID()).map(List::of).ifPresent(param::setProjectIDList);
        return param;
    }
}
