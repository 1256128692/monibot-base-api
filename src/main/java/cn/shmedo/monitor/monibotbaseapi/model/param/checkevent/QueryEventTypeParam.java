package cn.shmedo.monitor.monibotbaseapi.model.param.checkevent;


import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventTypeMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class QueryEventTypeParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    private String name;
    private Integer serviceID;


    @Override
    public ResultWrapper validate() {
        TbCheckEventTypeMapper checkEventTypeMapper = ContextHolder.getBean(TbCheckEventTypeMapper.class);
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
