package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.shmedo.iot.entity.api.*;
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
    private String keyword;
    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
        return new Resource(currentSubject.getCompanyID().toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
