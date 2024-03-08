package cn.shmedo.monitor.monibotbaseapi.model.param.notify;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 11:27
 */
@Data
public class QueryNotifyListParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    private Integer projectID;
    private Integer serviceID;
    private Integer status;
    private List<Integer> notifyIDList;

    @Override
    public Integer getPlatform() {
        return serviceID;
    }

    @Override
    public ResultWrapper<?> validate() {
        if (Objects.nonNull(serviceID) && !validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
