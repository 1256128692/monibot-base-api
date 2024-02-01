package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 11:08
 **/
@Data
public class ReservoirProjectStatisticsParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    @Positive
    private Integer companyID;


    @Override
    public ResultWrapper validate() {
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
