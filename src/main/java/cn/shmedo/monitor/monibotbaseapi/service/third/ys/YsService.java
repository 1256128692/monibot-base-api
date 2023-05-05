package cn.shmedo.monitor.monibotbaseapi.service.third.ys;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsTokenInfo;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface YsService {
    @RequestLine("POST /api/lapp/token/get")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("appKey={appKey}&appSecret={appSecret}")
    YsResultWrapper<YsTokenInfo> getToken(@Param("appKey") String appKey, @Param("appSecret") String appSecret);

    @RequestLine("POST /api/lapp/device/info")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}")
    YsResultWrapper<YsDeviceInfo> getDeviceInfo(@Param("accessToken") String accessToken,
                                                @Param("deviceSerial") String deviceSerial);
}
