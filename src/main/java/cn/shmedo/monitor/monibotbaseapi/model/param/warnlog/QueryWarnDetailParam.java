package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 16:24
 */
@Data
public class QueryWarnDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Positive(message = "公司ID为正值")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @Positive(message = "报警记录ID为正值")
    @NotNull(message = "报警记录ID不能为空")
    private Integer warnLogID;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
