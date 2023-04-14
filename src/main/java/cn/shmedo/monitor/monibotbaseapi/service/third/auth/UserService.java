package cn.shmedo.monitor.monibotbaseapi.service.third.auth;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.auth.OpenAuthApplicationHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionInBatchResourceParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface UserService {
    @RequestLine("GET /GetCurrentSubject")
    @Headers("Authorization: Bearer {accessToken}")
    ResultWrapper<CurrentSubject> getCurrentSubject(@Param("accessToken") String accessToken);

    @RequestLine("GET /GetCurrentSubject")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<CurrentSubject> getCurrentSubjectByApp(@Param("appKey") String appKey, @Param("appSecret") String appSecret);

    /**
     * 验证应用是否具有指定权限
     *
     * @param pa 参数
     */
    @RequestLine("POST /ApplicationHasPermission")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Boolean> applicationHasPermission(OpenAuthApplicationHasPermissionParameter pa,
                                                    @Param("appKey") String appKey, @Param("appSecret") String appSecret);

    /**
     * 验证用户在某服务的某资源中是否具有指定权限
     *
     * @param pa 参数
     */
    @RequestLine("POST /QueryHasPermission")
    @Headers("Authorization: {accessToken}")
    ResultWrapper<Boolean> queryHasPermission(OpenAuthQueryHasPermissionParameter pa,
                                              @Param("accessToken") String accessToken);

    /**
     * 验证用户在某服务的批量资源中是否具有指定权限
     *
     * @param pa 参数
     */
    @RequestLine("POST /QueryHasPermissionInBatchResource")
    @Headers("Authorization: {accessToken}")
    ResultWrapper<Boolean> queryHasPermissionInBatchResource(OpenAuthQueryHasPermissionInBatchResourceParameter pa,
                                                             @Param("accessToken") String accessToken);

    /**
     * 批量查询用户名称
     */
    @RequestLine("POST /QueryUserIDName")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Object> queryUserIDName(QueryUserIDNameParameter pa,
                                          @Param("appKey") String appKey, @Param("appSecret") String appSecret);
}
