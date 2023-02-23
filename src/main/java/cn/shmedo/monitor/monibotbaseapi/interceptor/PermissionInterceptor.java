package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.base.Holder;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.ApplicationHasPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.BatchResourceParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.QueryHasPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.ResourceItemV2;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.AuthHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.PermissionService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
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
// @Component
@Order(InterceptorOrder.VALIDATE_PERMISSION)
public class PermissionInterceptor {

    @Around(value = "cn.shmedo.monitor.monibotbaseapi.interceptor.ResourcePointcuts.resourceMethod()&& @annotation(permission)")
    public Object aroundResourceMethod(ProceedingJoinPoint joinPoint, Permission permission) {
        try {
            Object[] args = joinPoint.getArgs();
            ResultWrapper permissionResult = validatePermission(args, permission);
            if (permissionResult != null) {
                return permissionResult;
            }
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new RuntimeException(throwable);
            }

        }
    }

    private ResultWrapper validatePermission(Object[] args, Permission permission) {
        /**
         * 公共权限
         */
        if (permission.permissionScope().equals(PermissionScope.PUBLIC)) {
            return null;
        }
        CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
        if (currentSubject == null) {
            return ResultWrapper.noPermission(permission.permissionName());
        }
        /**
         * 登录权限
         */
        if (permission.permissionScope().equals(PermissionScope.LOGGED)) {
            return null;
        }
        Tuple<String, String> servicePermission = PermissionUtil.splitServiceAndPermission(permission.permissionName());
        if (currentSubject.getSubjectType().equals(SubjectType.APPLICATION)) {
            Tuple<String, String> appKeySecret = CurrentSubjectHolder.getCurrentSubjectExtractData();
            return validateApplicationPermission(appKeySecret.getItem1(), appKeySecret.getItem2(),
                    permission, servicePermission);
        } else {
            return validateUserPermission(currentSubject.getSubjectID(), currentSubject.getSubjectName(),
                    currentSubject.getCompanyID(), args, permission, servicePermission);
        }
    }


    /**
     * 校验应用是否有相应权限
     *
     * @param appKey            应用key
     * @param appSecret         应用secret
     * @param permission        权限注解
     * @param servicePermission 服务和权限
     * @return 权限验证结果:有权限返回null，无权限返回对应的{@link ResultWrapper}
     */
    private ResultWrapper validateApplicationPermission(String appKey, String appSecret,
                                                        Permission permission, Tuple<String, String> servicePermission) {
        if (!permission.allowApplication()) {
            return ResultWrapper.noPermission(permission.permissionName());
        }
        PermissionService permissionService = ThirdHttpService.getInstance(PermissionService.class,ThirdHttpService.Auth);
        ApplicationHasPermissionParameter pa = new ApplicationHasPermissionParameter();
        pa.setServiceName(servicePermission.getItem1());
        pa.setPermissionToken(servicePermission.getItem2());
        ResultWrapper<Boolean> resultWrapper = permissionService.applicationHasPermission(pa, appKey, appSecret);
        return transferApiResultWrapperToPermissionResultWrapper(resultWrapper, permission.permissionName());
    }


    /**
     * 校验用户是否有相应权限
     *
     * @param userID            用户ID
     * @param userName          用户名称
     * @param userRawCompanyID  用户所直属的公司ID
     * @param args              接口参数
     * @param permission        权限
     * @param servicePermission 服务和权限
     * @return 权限验证结果
     */
    private ResultWrapper validateUserPermission(int userID, String userName, int userRawCompanyID, Object[] args,
                                                 Permission permission, Tuple<String, String> servicePermission) {
        if (args == null || args.length <= 0) {
            return ResultWrapper.noPermission(permission.permissionName());
        }
        Holder<ResourcePermissionProvider> providerHolder = new Holder<>();
        Arrays.stream(args).forEach(arg -> {
            if (arg instanceof ResourcePermissionProvider) {
                providerHolder.setData((ResourcePermissionProvider) arg);
            }
        });
        ResourcePermissionProvider provider = providerHolder.getData();
        if (provider == null) {
            return ResultWrapper.noPermission(permission.permissionName());
        }
        String token = CurrentSubjectHolder.getCurrentSubjectExtractData();
        ResourcePermissionType resourcePermissionType = provider.resourcePermissionType();
        PermissionService permissionService = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
        ResultWrapper validateResultWrapper = null;
        switch (resourcePermissionType) {
            case SINGLE_RESOURCE_SINGLE_PERMISSION:
                Resource resource = (Resource) provider.parameter();
                QueryHasPermissionParameter pa = new QueryHasPermissionParameter();
                pa.setServiceName(servicePermission.getItem1());
                pa.setPermissionToken(servicePermission.getItem2());
                pa.setResourceToken(resource.getResourceToken());
                pa.setResourceType(resource.getResourceType().toInt());
                pa.setAllowInherit(permission.allowInherit());
                validateResultWrapper = permissionService.queryHasPermission(pa, token);
                break;
            case BATCH_RESOURCE_SINGLE_PERMISSION:
                List<Resource> resourceList = (List<Resource>) provider.parameter();
                List<ResourceItemV2> itemV2List = resourceList.stream().map(ResourceItemV2::new)
                        .collect(Collectors.toList());
                BatchResourceParameter batchPa = new BatchResourceParameter();
                batchPa.setServiceName(servicePermission.getItem1());
                batchPa.setPermissionToken(servicePermission.getItem2());
                batchPa.setResourceList(itemV2List);
                validateResultWrapper = permissionService.queryHasPermissionInBatchResource(batchPa, token);
                break;
            default:
                break;
        }
        ResultWrapper result = transferApiResultWrapperToPermissionResultWrapper(validateResultWrapper, permission.permissionName());
        return result;
    }

    private ResultWrapper transferApiResultWrapperToPermissionResultWrapper(ResultWrapper<Boolean> apiResultWrapper, String permissionName) {
        if (apiResultWrapper == null) {
            return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, "授权服务调用失败");
        }
        if (!apiResultWrapper.apiSuccess()) {
            return apiResultWrapper;
        }
        return apiResultWrapper.getData() ? null : ResultWrapper.noPermission(permissionName);
    }
}
