package cn.shmedo.monitor.monibotbaseapi.service.third.auth;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.*;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Set;

public interface PermissionService {

    /**
     * 验证应用是否具有指定权限
     *
     * @param pa
     * @return
     */

    @RequestLine("POST /ApplicationHasPermission")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Boolean> applicationHasPermission(ApplicationHasPermissionParameter pa, @Param("appKey") String appKey,
                                                    @Param("appSecret") String appSecret);

    /**
     * 验证在某资源中是否具有某权限
     *
     * @param pa
     * @return
     */

    @RequestLine("POST /QueryHasPermission")
    @Headers("Authorization: Bearer {accessToken}")
    ResultWrapper<Boolean> queryHasPermission(QueryHasPermissionParameter pa, @Param("accessToken") String accessToken);

    /**
     * 验证在批量资源中是否具有某权限
     *
     * @param pa
     * @return
     */
    @RequestLine("POST /QueryHasPermissionInBatchResource")
    @Headers("Authorization: Bearer {accessToken}")
    ResultWrapper<Boolean> queryHasPermissionInBatchResource(BatchResourceParameter pa,
                                                             @Param("accessToken") String accessToken);

    /**
     * 验证用户在某服务的批量资源中的任一资源是否具有指定权限
     *
     * @param pa 参数与 queryHasPermissionInBatchResource一致
     * @return
     */
    @RequestLine("POST /QueryHasPermissionInAnyoneBatchResource")
    @Headers("Authorization: Bearer {accessToken}")
    ResultWrapper<Boolean> queryHasPermissionInAnyoneBatchResource(BatchResourceParameter pa, @Param("accessToken") String accessToken);

    /**
     * 验证在某公司中是否具有某权限
     *
     * @param pa
     * @return
     */
    @RequestLine("POST /QueryPermissionInService")
    @Headers("Authorization: Bearer {accessToken}")
    ResultWrapper<Boolean> queryPermissionInService(QueryPermissionInServiceParameter pa,
                                                    @Param("accessToken") String accessToken);

    /**
     * 获取具有某权限的资源列表
     *
     * @param pa
     * @return
     */
    @RequestLine("POST /QueryResourceListByPermission")
    @Headers("Authorization: Bearer {accessToken}")
    ResultWrapper<Set<String>> queryResourceListByPermission(QueryResourceListByPermissionParameter pa,
                                                             @Param("accessToken") String accessToken);


    @RequestLine("POST /DeleteMdmbaseResource")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Object> deleteMdmbaseResource(@Param("appKey") String authAppKey,
                                                @Param("appSecret") String authAppSecret,
                                                DeleteResourcesParameter deleteResourcesParameter);


    @RequestLine("POST /AddMdmbaseResource")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Object> addMdmbaseResource(@Param("appKey") String authAppKey,
                                             @Param("appSecret") String authAppSecret,
                                             AddResourcesParameter addResourcesParameter);

    @RequestLine("POST /UpdateMdmbaseResourceDesc")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Object> updateMdmbaseResourceDesc(@Param("appKey") String authAppKey,
                                                    @Param("appSecret") String authAppSecret,
                                                    UpdateResourceDescParameter updateResourceDescParameter);

    @RequestLine("POST /TransferMdmbaseResource")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Object> transferMdmbaseResource(@Param("appKey")String authAppKey,
                                                  @Param("appSecret")String authAppSecret,
                                                  TransferResourceParameter transferResourceParameter);
}
