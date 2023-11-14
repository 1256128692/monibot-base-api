package cn.shmedo.monitor.monibotbaseapi.core.annotation;

import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;

import java.lang.annotation.*;

/**
 * 此注解适用于非json-Body接口，标识参数将作为权限校验的资源
 * @author wuxl
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ResourceSymbol {
    ResourceType value();

    ResourcePermissionType permissionType() default ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
}
