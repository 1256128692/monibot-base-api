package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 14:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FillDealOpinionParam extends WarnHandleParam {
    private String opinion;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (Objects.isNull(opinion)) {
            opinion = "";
        }
        return null;
    }

}
