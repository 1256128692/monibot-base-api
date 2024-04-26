package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author wuxl
 * @Date 2024/4/26 14:26
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.project
 * @ClassName: QueryFavoriteMonitorItemListParam
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class QueryFavoriteMonitorItemListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;

    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
