package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtTriggerActionInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnTriggerService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class BatchUpdateWtEngineEnableParam extends BatchDeleteWtEngineParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "启用状态不能为空")
    private Boolean enable;

    @Override
    public ResultWrapper validate() {
        List<Integer> engineIDList = getEngineIDList();
        if (engineIDList.size() == 1 && enable) {
            ITbWarnTriggerService tbWarnTriggerService = ContextHolder.getBean(ITbWarnTriggerService.class);
            WtTriggerActionInfo info = Optional.of(engineIDList).map(w -> w.get(0)).map(u -> {
                        List<Integer> list = new ArrayList<>();
                        list.add(u);
                        return list;
                    }).map(tbWarnTriggerService::queryWarnStatusByEngineIds).filter(u -> u.size() > 0).map(u -> u.get(0))
                    .orElse(WtTriggerActionInfo.builder().warnID(null).build());
            if (Objects.isNull(info.getWarnID())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该规则未配置完整，不能开启");
            }
        }
        return super.validate();
    }
}
