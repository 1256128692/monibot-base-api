package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnLevelType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 15:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDataWarnPageParam extends QueryWarnPageBaseParam {
    private Integer warnLevel;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (Objects.nonNull(warnLevel) && !DataWarnLevelType.fromCode(getTbWarnBaseConfig().getWarnLevelType())
                .getWarnLevelSet().contains(warnLevel)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警等级不合法");
        }
        return null;
    }
}
