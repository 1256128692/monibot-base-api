package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.auth.OpenAuthApplicationHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionInBatchResourceParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.resource.OpenAuthResourceItemV2;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.base.Holder;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限校验拦截器
 */
@Aspect
@Component
@AllArgsConstructor
@Order(InterceptorOrder.VALIDATE_PERMISSION)
public class PermissionInterceptor {

    private final UserService userService;

    @Around(value = "cn.shmedo.monitor.monibotbaseapi.interceptor.ResourcePointcuts.resourceMethod()&& @annotation(permission)")
    public Object aroundResourceMethod(ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        Object[] args = joinPoint.getArgs();
        ResultWrapper<Boolean> permissionResult = validatePermission(args, permission);
        if (permissionResult.apiSuccess() && permissionResult.getData()) {
            return joinPoint.proceed();
        }
        String message = StrUtil.isEmpty(permission.permissionName()) ? StrUtil.EMPTY : permission.permissionName();
        //抛出异常由全局异常处理器处理
        throw new CustomBaseException(ResultCode.NO_PERMISSION.toInt(), ResultCode.NO_PERMISSION + message);
    }

    private ResultWrapper<Boolean> validatePermission(Object[] args, Permission permission) {

        //公共权限
        if (permission.permissionScope().equals(PermissionScope.PUBLIC)) {
            return ResultWrapper.success(Boolean.TRUE);
        }

        //私有权限
        CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
        if (currentSubject == null) {
            // 找不到调用主体，直接返回失败
            return ResultWrapper.success(Boolean.FALSE);
        }

        // 登录权限
        if (permission.permissionScope().equals(PermissionScope.LOGGED)) {
            return ResultWrapper.success(Boolean.TRUE);
        }

        Tuple<String, String> servicePermission = PermissionUtil.splitServiceAndPermission(permission.permissionName());
        if (currentSubject.getSubjectType().equals(SubjectType.APPLICATION)) {
            Tuple<String, String> appKeySecret = CurrentSubjectHolder.getCurrentSubjectExtractData();
            return validateApplicationPermission(permission, appKeySecret, servicePermission);
        } else {
            String token = CurrentSubjectHolder.getCurrentSubjectExtractData();
            return validateUserPermission(args, permission, token, servicePermission);
        }
    }

    /**
     * 校验应用是否有相应权限
     *
     * @param permission        权限注解
     * @param servicePermission 服务和权限
     * @return 权限验证结果 {@link ResultWrapper<Boolean>}
     */
    private ResultWrapper<Boolean> validateApplicationPermission(Permission permission,
                                                                 Tuple<String, String> appKeySecret,
                                                                 Tuple<String, String> servicePermission) {

        if (permission.allowApplication()) {
            OpenAuthApplicationHasPermissionParameter pa = new OpenAuthApplicationHasPermissionParameter();
            pa.setServiceName(servicePermission.getItem1());
            pa.setPermissionToken(servicePermission.getItem2());

            return userService.applicationHasPermission(pa, appKeySecret.getItem1(), appKeySecret.getItem2());
        }
        return ResultWrapper.success(Boolean.FALSE);
    }

    /**
     * 校验用户是否有相应权限
     *
     * @param args              接口参数
     * @param permission        权限
     * @param servicePermission 服务和权限
     * @return 权限验证结果
     */
    private ResultWrapper<Boolean> validateUserPermission(Object[] args, Permission permission,
                                                          String token, Tuple<String, String> servicePermission) {
        if (ArrayUtil.isNotEmpty(args)) {

            Holder<ResourcePermissionProvider<?>> providerHolder = new Holder<>();
            Arrays.stream(args)
                    .filter(arg -> arg instanceof ResourcePermissionProvider<?>)
                    .findFirst()
                    .map(arg -> (ResourcePermissionProvider<?>) arg)
                    .ifPresent(providerHolder::setData);

            ResourcePermissionProvider<?> provider = providerHolder.getData();
            if (provider != null) {
                switch (provider.resourcePermissionType()) {
                    case SINGLE_RESOURCE_SINGLE_PERMISSION -> {
                        Resource resource = (Resource) provider.parameter();
                        OpenAuthQueryHasPermissionParameter pa = new OpenAuthQueryHasPermissionParameter();
                        pa.setServiceName(servicePermission.getItem1());
                        pa.setPermissionToken(servicePermission.getItem2());
                        pa.setResourceToken(resource.getResourceToken());
                        pa.setResourceType(resource.getResourceType().toInt());
                        pa.setAllowInherit(permission.allowInherit());
                        return userService.queryHasPermission(pa, token);
                    }
                    case BATCH_RESOURCE_SINGLE_PERMISSION -> {
                        List<Resource> resourceList = Convert.toList(Resource.class, provider.parameter());
                        List<OpenAuthResourceItemV2> itemV2List = resourceList.stream().map(OpenAuthResourceItemV2::new)
                                .collect(Collectors.toList());
                        OpenAuthQueryHasPermissionInBatchResourceParameter batchPa =
                                new OpenAuthQueryHasPermissionInBatchResourceParameter();
                        batchPa.setServiceName(servicePermission.getItem1());
                        batchPa.setPermissionToken(servicePermission.getItem2());
                        batchPa.setResourceList(itemV2List);
                        return userService.queryHasPermissionInBatchResource(batchPa, token);
                    }
                }
            }
        }
        return ResultWrapper.success(Boolean.FALSE);
    }
}
