package cn.shmedo.monitor.monibotbaseapi.service.third.auth;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
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

    @RequestLine("POST /GetCompanyInfo")
    //@Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Company> getCompanyInfo(Integer ID);

}
