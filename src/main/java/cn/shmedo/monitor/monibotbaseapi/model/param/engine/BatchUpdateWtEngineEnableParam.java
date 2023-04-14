package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BatchUpdateWtEngineEnableParam extends BatchDeleteWtEngineParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "启用状态不能为空")
    private Boolean enable;
}
