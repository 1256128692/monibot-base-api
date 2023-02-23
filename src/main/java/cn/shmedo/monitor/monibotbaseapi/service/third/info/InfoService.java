package cn.shmedo.monitor.monibotbaseapi.service.third.info;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultWrapper;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface InfoService {

    @RequestLine("Post /GetCurrentSubject")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<CurrentSubject> getCurrentSubjectByApp(@Param("appKey") String appKey, @Param("appSecret") String appSecret);


}