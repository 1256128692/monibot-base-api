package cn.shmedo.monitor.monibotbaseapi.service.third.auth;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.auth.OpenAuthApplicationHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionInBatchResourceParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserBase;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserContact;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.SetNotifyStatusParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.DeleteNotifyParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.QueryNotifyStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.QueryUserInCompanyListParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyPageResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

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
     * 批量查询用户名称<br>
     * list item上限100条
     */
    @RequestLine("POST /QueryUserIDName")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<UserIDName>> queryUserIDName(QueryUserIDNameParameter pa,
                                                    @Param("appKey") String appKey, @Param("appSecret") String appSecret);


    @RequestLine("POST /ListCompanyIDName")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<CompanyIDAndNameV2>> listCompanyIDName(CompanyIDListParam pa,
                                                              @Param("appKey") String appKey, @Param("appSecret") String appSecret);

    @RequestLine("POST /QueryUserInCompanyList")
    @Headers({"Authorization: {accessToken}"})
    ResultWrapper<List<CompanyIDAndNameV2>> queryUserInCompanyList(QueryUserInCompanyListParameter param, @Param("accessToken") String accessToken);

    @RequestLine("POST /QueryDeptSimpleList")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<DeptSimpleInfo>> queryDeptSimpleList(QueryDeptSimpleListParam pa,
                                                            @Param("appKey") String appKey, @Param("appSecret") String appSecret);

    @RequestLine("POST /QueryUserInDeptListNoPage")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<UserNoPageInfo>> queryUserInDeptListNoPage(QueryUserInDeptListNoPageParam pa,
                                                                  @Param("appKey") String appKey, @Param("appSecret") String appSecret);

    /**
     * 批量添加系统通知
     */
    @RequestLine("POST /AddNotify")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<Integer>> addSysNotify(SysNotify request, @Param("appKey") String appKey,
                                              @Param("appSecret") String appSecret);

    /**
     * 分页查询系统通知列表
     */
    @RequestLine("POST /QueryNotifyPageList")
    @Headers("Authorization: {accessToken}")
    ResultWrapper<PageUtil.Page<NotifyPageInfo>> queryNotifyPageList(QueryNotifyPageListParam param, @Param("accessToken") String accessToken);

    /**
     * 不分页查询系统通知列表
     */
    @RequestLine("POST /QueryNotifyList")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<NotifyPageResponse>> queryNotifyList(QueryNotifyListParam param, @Param("appKey") String appKey, @Param("appSecret") String appSecret);

    /**
     * 设置消息通知状态
     */
    @RequestLine("POST /SetNotifyStatus")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<?> setNotifyStatus(SetNotifyStatusParam param,
                                     @Param("appKey") String appKey,
                                     @Param("appSecret") String appSecret);

    /**
     * 删除系统通知
     */
    @RequestLine("POST /DeleteNotify")
    @Headers("Authorization: {accessToken}")
    ResultWrapper<Void> deleteNotify(DeleteNotifyParam param, @Param("accessToken") String accessToken);

    @RequestLine("POST /QueryNotifyDetail")
    @Headers("Authorization: {accessToken}")
    ResultWrapper<NotifyDetailInfo> queryNotifyDetail(QueryNotifyDetailParam param, @Param("accessToken") String accessToken);

    /**
     * 批量查询用户联系方式
     */
    @RequestLine("POST /QueryUserContact")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Map<Integer, UserContact>> queryUserContact(QueryUserContactParam param, @Param("appKey") String appKey,
                                                              @Param("appSecret") String appSecret);

    @RequestLine("POST /QueryDepartmentIncludeUserInfoList")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<DepartmentIncludeUserInfo> queryDepartmentIncludeUserInfoList(QueryDepartmentIncludeUserInfoListParameter parameter,
                                                                                @Param("appKey") String appKey,
                                                                                @Param("appSecret") String appSecret);

    /**
     * 批量查询用户
     *
     * @return {@link List<UserBase>}
     */
    @RequestLine("POST /QueryUserBatch")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<UserBase>> queryUserBatch(QueryUserBatchRequest request,
                                                 @Param("appKey") String appKey,
                                                 @Param("appSecret") String appSecret);
}
