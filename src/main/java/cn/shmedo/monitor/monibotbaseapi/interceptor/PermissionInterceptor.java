package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.auth.OpenAuthApplicationHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionInBatchResourceParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.resource.OpenAuthResourceItemV2;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限校验拦截器
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
@Order(InterceptorOrder.VALIDATE_PERMISSION)
public class PermissionInterceptor {

    private final UserService userService;

    @Around(value = "cn.shmedo.monitor.monibotbaseapi.interceptor.ResourcePointcuts.resourceMethod()&& @annotation(permission)")
    public Object aroundResourceMethod(ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        //校验权限
        ResultWrapper<Boolean> result;
        try {
            result = validatePermission(joinPoint.getArgs(), permission);
            Assert.notNull(result);
        } catch (Exception e) {
            log.info("校验权限出错: {}", ExceptionUtil.stacktraceToString(e));
            throw new CustomBaseException(ResultCode.SERVER_EXCEPTION.toInt(), "校验权限出错");
        }

        //校验通过则放行，否则抛出异常
        if (result.apiSuccess() && Boolean.TRUE.equals(result.getData())) {
            return joinPoint.proceed();
        }

        //抛出异常由全局异常处理器处理
        throw StrUtil.isNotBlank(result.getMsg()) ?
                new CustomBaseException(result.getCode(), result.getMsg()) :
                new CustomBaseException(ResultCode.NO_PERMISSION.toInt(),
                        ResultCode.NO_PERMISSION + permission.permissionName());
    }

    /**
     * 校验权限
     *
     * @param args       方法参数
     * @param permission 权限注解
     * @return 权限验证结果 {@link ResultWrapper}
     */
    private ResultWrapper<Boolean> validatePermission(Object[] args, Permission permission) {
        switch (permission.permissionScope()) {
            case PUBLIC -> {
                return ResultWrapper.success(Boolean.TRUE);
            }
            case LOGGED -> {
                return ResultWrapper.success(CurrentSubjectHolder.getCurrentSubject() != null);
            }
            case REQUIRE -> {
                Tuple<String, String> tuple = PermissionUtil.splitServiceAndPermission(permission.permissionName());
                CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
                if (subject == null) {
                    return ResultWrapper.success(Boolean.FALSE);
                }
                if (SubjectType.APPLICATION.equals(subject.getSubjectType())) {
                    Tuple<String, String> appKeySecret = CurrentSubjectHolder.getCurrentSubjectExtractData();
                    return validateApplicationPermission(permission, appKeySecret, tuple);
                } else if (SubjectType.USER.equals(subject.getSubjectType())) {
                    String token = CurrentSubjectHolder.getCurrentSubjectExtractData();
                    return validateUserPermission(args, permission, token, tuple);
                }
            }
        }
        return ResultWrapper.success(Boolean.FALSE);
    }

    /**
     * 校验应用是否有相应权限
     *
     * @param permission        权限注解
     * @param servicePermission 服务和权限
     * @return 权限验证结果 {@link ResultWrapper}
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
        //FIXME: 无参数时，会直接返回无权限；
        // 多参数时，将只校验第一个实现 ResourcePermissionProvider 的参数

        if (ArrayUtil.isNotEmpty(args)) {
            for (Object arg : args) {
                if (arg instanceof ResourcePermissionProvider<?> provider) {
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
                    break;
                }
            }
        }
        return ResultWrapper.success(Boolean.FALSE);
    }
}
