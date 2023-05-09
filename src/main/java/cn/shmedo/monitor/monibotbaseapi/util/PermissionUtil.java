package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.QueryResourceListByPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.PermissionService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class PermissionUtil {
    /**
     * 将"serviceName:permissionToken"拆分为具体的服务和权限
     *
     * @param permissionName
     * @return
     */
    public static Tuple<String, String> splitServiceAndPermission(String permissionName) {
        String[] parts = permissionName.split(DefaultConstant.COLON);
        return new Tuple<>(parts[0], parts[1]);
    }

    /**
     * 获取当前用户有权限的项目列表，仅用于用户在请求线程中调用
     *
     * @return 有权限的项目列表
     */
    public static Collection<Integer> getHavePermissionProjectList(Integer companyID) {
        return getHavePermissionProjectList(companyID, null);
    }

    /**
     * 获取当前用户有权限的项目列表，仅用于用户在请求线程中调用
     *
     * @param projectList 需要比较的项目列表，结果将和此集合做交集，为null时将不会做交集
     * @return 有权限的项目列表
     */
    public static Collection<Integer> getHavePermissionProjectList(Integer companyID, Collection<Integer> projectList) {
        Collection<Integer> resourceList = getResourceList(companyID, DefaultConstant.MDNET_SERVICE_NAME,
                DefaultConstant.LIST_PROJECT, ResourceType.BASE_PROJECT).stream().map(Integer::parseInt).toList();
        return CollectionUtils.isEmpty(projectList) ? resourceList : CollectionUtil.intersection(projectList, resourceList);
    }

    /**
     * 远程调用获当前用户有取资源列表
     *
     * @param companyID       公司ID
     * @param serviceName     服务名
     * @param permissionToken 权限名
     * @param resourceType    资源类型
     * @return 资源列表
     */
    public static Collection<String> getResourceList(@Nonnull Integer companyID,
                                                     @Nonnull String serviceName,
                                                     @Nonnull String permissionToken,
                                                     @Nonnull ResourceType resourceType) {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        Object extract = CurrentSubjectHolder.getCurrentSubjectExtractData();
        if (subject != null && extract instanceof String token) {
            PermissionService instance = ThirdHttpService.getInstance(PermissionService.class, ThirdHttpService.Auth);
            QueryResourceListByPermissionParameter pa = new QueryResourceListByPermissionParameter();
            pa.setCompanyID(companyID);
            pa.setServiceName(serviceName);
            pa.setPermissionToken(permissionToken);
            pa.setResourceType(resourceType.toInt());
            ResultWrapper<Set<String>> info = instance.queryResourceListByPermission(pa, token);
            Assert.isTrue(info.apiSuccess(), "获取权限列表失败");
            return info.getData();
        }
        return Collections.emptyList();
    }
}
