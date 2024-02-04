package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IThresholdConfigValueCheck;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;


/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-24 17:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnThresholdConfigEnableBatchParam extends QueryWarnThresholdConfigListParam implements IThresholdConfigValueCheck {
    @NotNull(message = "启用/禁用不能为空")
    private Boolean enable;
    @JsonIgnore
    private List<TbWarnThresholdConfig> updateList;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        return null;
    }
}
