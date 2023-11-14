package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class QueryProjectLocationParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer companyID;

    @NotNull
    private Integer monitorType;
    @NotNull
    private Integer monitorClassType;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
