package cn.shmedo.monitor.monibotbaseapi.service.third.ys;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import jakarta.validation.constraints.NotEmpty;

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

    /**
     * 获取播放地址
     *
     * @see <a href="https://open.ys7.com/help/1414">接口文档-获取播放地址</a>
     */
    @RequestLine("POST /api/lapp/v2/live/address/get")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&channelNo={channelNo}&protocol={protocol}&code={code}" +
            "&expireTime={expireTime}&type={type}&quality={quality}&startTime={startTime}&stopTime={stopTime}" +
            "&supportH265={supportH265}&playbackSpeed={playbackSpeed}&gbchannel={gbchannel}")
    YsResultWrapper<YsStreamUrlInfo> getStreamInfo(@NotEmpty @Param("accessToken") String accessToken,
                                                   @NotEmpty @Param("deviceSerial") String deviceSerial,
                                                   @Param("channelNo") Integer channelNo,
                                                   @Param("protocol") Integer protocol,
                                                   @Param("code") String code,
                                                   @Param("expireTime") Integer expireTime,
                                                   @Param("type") String type,
                                                   @Param("quality") Integer quality,
                                                   @Param("startTime") String startTime,
                                                   @Param("stopTime") String stopTime,
                                                   @Param("supportH265") Integer supportH265,
                                                   @Param("playbackSpeed") String playbackSpeed,
                                                   @Param("gbchannel") String gbchannel);

    /**
     * 获取设备能力集
     *
     * @see <a href="https://open.ys7.com/help/678">接口文档-获取设备能力集</a>
     * @see <a href="https://open.ys7.com/help/77">接口文档-设备能力集</a>
     */
    @RequestLine("POST /api/lapp/device/capacity")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}")
    YsResultWrapper<YsCapacityInfo> capacity(@NotEmpty @Param("accessToken") String accessToken,
                                             @NotEmpty @Param("deviceSerial") String deviceSerial);

    @RequestLine("POST /api/lapp/device/add")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&validateCode={validateCode}")
    YsResultWrapper addDevice(@Param("accessToken") String accessToken,
                              @Param("deviceSerial") String deviceSerial,
                              @Param("validateCode")  String validateCode);

    @RequestLine("POST /api/lapp/device/delete")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}")
    YsResultWrapper deleteDevice(@Param("accessToken") String accessToken,
                                 @Param("deviceSerial") String deviceSerial);

    @RequestLine("POST /api/lapp/device/name/update")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&deviceName={deviceName}")
    YsResultWrapper updateDevice(@Param("accessToken") String accessToken,
                                 @Param("deviceSerial") String deviceSerial,
                                 @Param("deviceName") String deviceName);
}
