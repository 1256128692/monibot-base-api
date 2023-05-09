package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheType;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-11 13:42
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuerySimpleMonitorTypeListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    private Byte createType;
    private Boolean grouped;

    private Integer projectID;

    @Override
    public ResultWrapper validate() {
        if (createType != null && !CreateType.isValid(createType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "创建类型不合法");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
