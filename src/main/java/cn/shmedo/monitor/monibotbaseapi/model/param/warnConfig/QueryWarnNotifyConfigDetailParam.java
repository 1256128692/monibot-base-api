package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 13:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryWarnNotifyConfigDetailParam extends CompanyPlatformParam {
    @NotNull(message = "报警通知配置ID不能为空")
    @Positive(message = "报警通知配置ID不能小于1")
    private Integer notifyConfigID;
    @JsonIgnore
    private WarnNotifyConfigDetail detail = null;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (Objects.isNull(detail = ContextHolder.getBean(TbWarnNotifyConfigMapper.class)
                .selectWarnNotifyConfigDetailByID(getNotifyConfigID()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警通知配置不存在");
        }
        return null;
    }
}
