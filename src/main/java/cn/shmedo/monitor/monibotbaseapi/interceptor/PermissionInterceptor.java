package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.auth.OpenAuthApplicationHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionInBatchResourceParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.resource.OpenAuthResourceItemV2;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.core.annotation.ResourceSymbol;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 权限拦截器，根据 {@link Permission} 注解进行权限校验
 *
 * @author Chengfs on 2022/11/24
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
@Order(InterceptorOrder.VALIDATE_PERMISSION)
public class PermissionInterceptor {

    private final UserService userService;

    @Around(value = "cn.shmedo.monitor.monibotbaseapi.interceptor.ResourcePointcuts.resourceMethod() && @annotation(permission)")
    public Object aroundResourceMethod(ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {

        //校验权限
        ResultWrapper<Boolean> result;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        try {
            result = Optional.ofNullable(validatePermission(signature.getMethod(),
                            joinPoint.getArgs(), permission)).orElse(ResultWrapper.success(Boolean.FALSE));
        } catch (Exception e) {
            Class<?> tClass = signature.getMethod().getDeclaringClass();
            String tName = signature.getMethod().getName();
            log.info("类: {} 方法: {} 校验权限出错: {}", tClass.getName(), tName, e.getMessage());
            log.error(ExceptionUtil.stacktraceToString(e));
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
    private ResultWrapper<Boolean> validatePermission(Method method, Object[] args, Permission permission) {
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
                if (subject.getSubjectType().equals(SubjectType.APPLICATION)) {
                    Tuple<String, String> appKeySecret = CurrentSubjectHolder.getCurrentSubjectExtractData();
                    return validateApplicationPermission(permission, appKeySecret, tuple);
                } else {
                    String token = CurrentSubjectHolder.getCurrentSubjectExtractData();
                    return validateUserPermission(method.getParameterAnnotations(), args, permission, token, tuple);
                }
            }
            default -> {
                return ResultWrapper.success(Boolean.FALSE);
            }
        }
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
    private ResultWrapper<Boolean> validateUserPermission(Annotation[][] annotations, Object[] args, Permission permission,
                                                    String token, Tuple<String, String> servicePermission) {

        ResourcePermissionType permissionType = null;
        Object permissionParameter = null;
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] array = annotations[i];
            Object arg = args[i];
            if (arg instanceof ResourcePermissionProvider<?> provider) {
                permissionType = provider.resourcePermissionType();
                permissionParameter = provider.parameter();
                break;
            } else {
                for (Annotation annotation : array) {
                    if(Objects.isNull(arg)){
                        break;
                    }
                    if (annotation instanceof ResourceSymbol symbol) {
                        permissionType = symbol.permissionType();
                        permissionParameter = ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION.equals(permissionType) ?
                                new Resource(arg.toString(), symbol.value()) :
                                Convert.toList(String.class, arg).stream()
                                        .map(e -> new Resource(e, symbol.value())).toList();
                        break;
                    }
                }
            }
            if (permissionType != null) {
                break;
            }
        }

        if (permissionType != null && permissionParameter != null) {
            switch (permissionType) {
                case SINGLE_RESOURCE_SINGLE_PERMISSION -> {
                    Resource resource = Convert.convert(Resource.class, permissionParameter);
                    OpenAuthQueryHasPermissionParameter pa = new OpenAuthQueryHasPermissionParameter();
                    pa.setServiceName(servicePermission.getItem1());
                    pa.setPermissionToken(servicePermission.getItem2());
                    pa.setResourceToken(resource.getResourceToken());
                    pa.setResourceType(resource.getResourceType().toInt());
                    pa.setAllowInherit(permission.allowInherit());
                    return userService.queryHasPermission(pa, token);
                }
                case BATCH_RESOURCE_SINGLE_PERMISSION -> {
                    List<Resource> resourceList = Convert.toList(Resource.class, permissionParameter);
                    boolean allMatch = CollUtil.split(resourceList, 100).stream().allMatch(list -> {
                        OpenAuthQueryHasPermissionInBatchResourceParameter batchPa = new OpenAuthQueryHasPermissionInBatchResourceParameter();
                        batchPa.setServiceName(servicePermission.getItem1());
                        batchPa.setPermissionToken(servicePermission.getItem2());
                        batchPa.setResourceList(list.stream().map(OpenAuthResourceItemV2::new).toList());
                        ResultWrapper<Boolean> wrapper = userService.queryHasPermissionInBatchResource(batchPa, token);
                        return wrapper.apiSuccess() && Boolean.TRUE.equals(wrapper.getData());
                    });
                    return ResultWrapper.success(allMatch);
                }

            }
        }
        return ResultWrapper.success(Boolean.FALSE);
    }
}


