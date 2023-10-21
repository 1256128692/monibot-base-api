package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

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

    private Integer projectType;

    private Integer companyID;

    private String name;

    private Integer modelType;

    private Integer modelTypeSubType;

    private Integer groupID;

    private Integer platform;

    private Byte createType;

    @JsonIgnore
    RedisTemplate<String, String> redisTemplate = ContextHolder.getBean(RedisTemplate.class);

    @Override
    public ResultWrapper<?> validate() {
        modelType = Objects.isNull(this.modelType) ? PropertyModelType.BASE_PROJECT.getCode() : this.modelType;
        groupID = PropertyModelType.BASE_PROJECT.getCode().equals(modelType) ? this.projectType : this.groupID;

        if(Objects.isNull(modelID)){
            if (PropertyModelType.BASE_PROJECT.getCode().equals(modelType) &&
                    Objects.nonNull(projectType) &&
                    !ProjectTypeCache.projectTypeMap.containsKey(Byte.valueOf(String.valueOf(projectType)))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
            }
            if (createType != null && !CreateType.isValid(createType)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "创建类型不合法");
            }
            if (PropertyModelType.WORK_FLOW.getCode().equals(modelType) && Objects.isNull(modelTypeSubType)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型为工作流时，所属子分类不能为空");
            }
            if (PropertyModelType.WORK_FLOW.getCode().equals(modelType) && Objects.isNull(platform)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型为工作流时，所属平台不能为空");
            }
            if(Objects.nonNull(platform) && !redisTemplate.opsForHash().hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, platform.toString())){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "所属平台不合法");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
        return new Resource(Objects.nonNull(companyID) ? companyID.toString() : currentSubject.getCompanyID().toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

}
