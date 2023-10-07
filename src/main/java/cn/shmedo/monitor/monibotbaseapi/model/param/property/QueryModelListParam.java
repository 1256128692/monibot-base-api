package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-02 10:23
 **/
@Data
@ToString
public class QueryModelListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    private Integer modelID;

    private String name;

    @NotNull(message = "模板类型不能为空")
    private Integer modelType;

    private Integer modelTypeSubType;

    private Integer groupID;

    private Byte createType;

    @Override
    public ResultWrapper<?> validate() {
        if (PropertyModelType.BASE_PROJECT.getCode().equals(modelType) && Objects.isNull(groupID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型为项目时，groupID不能为空");
        }
        if (Objects.nonNull(groupID) && !ProjectTypeCache.projectTypeMap.containsKey(Byte.valueOf(String.valueOf(groupID)))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        if (createType != null && !CreateType.isValid(createType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "创建类型不合法");
        }
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
