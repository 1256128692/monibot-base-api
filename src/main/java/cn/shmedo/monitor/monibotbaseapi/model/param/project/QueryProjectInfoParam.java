package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author cyf
 * @Date 2023/2/22 17:14
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.project
 * @ClassName: QueryProjectInfoParam
 * @Description:
 * @Version 1.0
 */
@Data
public class QueryProjectInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @JsonProperty(value = "ID")
    private Integer ID;
    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
