package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-20 16:36
 **/
@Data
public class QuerySuperMonitorItemListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    private Integer  createType;
    private Integer companyID;
    private Integer projectID;
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
