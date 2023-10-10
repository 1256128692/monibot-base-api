package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

/**
 * 查询属性数据 请求体
 *
 * @author Chengfs on 2023/2/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPropertyValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 公司ID
     */
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    /**
     * 项目ID
     */
    @Range(min = 1, message = "项目ID必须大于0")
    private Integer projectID;

    /**
     * 项目类型
     */
    @NotNull(message = "项目类型不能为空")
    private Integer projectType;

    /**
     * 模板类型
     */
    private Integer modelType;

    /**
     * 模板类型子分类
     */
    private Integer modelTypeSubType;

    /**
     * 分组ID
     */
    private Integer groupID;

    /**
     * 创建类型 0-预定义 1-自定义
     */
    @Range(min = 0, max = 1, message = "创建类型必须为0或1")
    private Integer createType;

    /**
     * 属性名称
     */
    @NotNull(message = "属性名称不能为空")
    private String propertyName;

    @Override
    public ResultWrapper<?> validate() {
        modelType = Objects.isNull(this.modelType) ? PropertyModelType.BASE_PROJECT.getCode() : this.modelType;
        groupID = PropertyModelType.BASE_PROJECT.getCode().equals(modelType) ? this.projectType : this.groupID;

        if (createType == null) {
            createType = 0;
        }
        if (!PropertyModelType.BASE_PROJECT.getCode().equals(modelType))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型除工程项目外，其他模板类型功能暂未开放");
        if (!ProjectTypeCache.projectTypeMap.containsKey(Byte.valueOf(String.valueOf(groupID))))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
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

    
    