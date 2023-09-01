package cn.shmedo.monitor.monibotbaseapi.service.third.ys;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

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


    @RequestLine("POST /api/v3/device/searchDeviceInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}")
    YsResultWrapper<YsBaseDeviceInfo> getBaseDeviceInfo(@Param("accessToken") String accessToken,
                                                        @Param("deviceSerial") String deviceSerial);


    @RequestLine("POST /api/lapp/device/camera/list")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}")
    YsResultWrapper<List<YsChannelInfo>> getDeviceChannelInfo(@Param("accessToken") String accessToken,
                                                           @Param("deviceSerial") String deviceSerial);

    @RequestLine("POST /api/lapp/device/ptz/start")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&channelNo={channelNo}&direction={direction}&speed={speed}")
    YsResultWrapper startPtz(@Param("accessToken") String accessToken,
                             @Param("deviceSerial") String deviceSerial,
                             @Param("channelNo") Integer channelNo,
                             @Param("direction") Integer direction,
                             @Param("speed") Integer speed);


    @RequestLine("POST /api/lapp/device/ptz/stop")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&channelNo={channelNo}&direction={direction}")
    YsResultWrapper stopPtz(@Param("accessToken") String accessToken,
                            @Param("deviceSerial") String deviceSerial,
                            @Param("channelNo") Integer channelNo,
                            @Param("direction") Integer direction);


    @RequestLine("POST /api/lapp/device/add")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&validateCode={validateCode}")
    YsResultWrapper addDevice(@Param("accessToken") String accessToken,
                              @Param("deviceSerial") String deviceSerial,
                              @Param("validateCode") String validateCode);
}
