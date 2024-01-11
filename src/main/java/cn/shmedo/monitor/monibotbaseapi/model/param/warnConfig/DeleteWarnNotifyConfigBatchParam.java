package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 13:44
 */
public class DeleteWarnNotifyConfigBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotEmpty(message = "报警通知配置ID List不能为空")
    private List<Integer> notifyConfigIDList;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
