package cn.shmedo.monitor.monibotbaseapi.service.third.ys;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDeviceBaseInfoV1;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

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
     * 获取播放地址<br>
     * 本接口不要使用，在传入正确的设备序列号和通道号时，会提示该设备不存在该通道号<br>
     * 此外，虽然萤石文档上写的{@code channelNo}和{@code expireTime}是非必填项,但是实际传参为null时会提示该参数不正确。<br>
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
                                                   @NotNull @Param("expireTime") Integer expireTime,
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
                              @Param("validateCode") String validateCode);

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

    @RequestLine("POST /api/lapp/device/list")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&pageStart={pageStart}&pageSize={pageSize}")
    YsResultPageWrapper<VideoDeviceBaseInfoV1> getBaseDeviceInfoByPage(@Param("accessToken") String accessToken,
                                                                       @Param("pageStart") Integer pageStart,
                                                                       @Param("pageSize") Integer pageSize);

    /**
     * 添加预置点<br>
     * 该萤石接口性能较低,响应经常在1s以上
     *
     * @return 预置点序号 e.g.{"index":3}
     * @see <a href="https://open.ys7.com/help/59#device_ptz-api4">接口文档-添加预置点</a>
     */
    @RequestLine("POST /api/lapp/device/preset/add")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&channelNo={channelNo}")
    YsResultWrapper<Map<String, Integer>> addPresetPoint(@Param("accessToken") String accessToken, @Param("deviceSerial") String deviceSerial,
                                                         @Param("channelNo") Integer channelNo);

    /**
     * 调用预置点
     *
     * @see <a href="https://open.ys7.com/help/59#device_ptz-api5">接口文档-调用预置点</a>
     */
    @RequestLine("POST /api/lapp/device/preset/move")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&channelNo={channelNo}&index={index}")
    YsResultWrapper movePresetPoint(@Param("accessToken") String accessToken, @Param("deviceSerial") String deviceSerial,
                                    @Param("channelNo") Integer channelNo, @Param("index") Integer index);

    /**
     * 删除预置点
     *
     * @see <a href="https://open.ys7.com/help/59#device_ptz-api6">接口文档-删除预置点</a>
     */
    @RequestLine("POST /api/lapp/device/preset/clear")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("accessToken={accessToken}&deviceSerial={deviceSerial}&channelNo={channelNo}&index={index}")
    YsResultWrapper clearPresetPoint(@Param("accessToken") String accessToken, @Param("deviceSerial") String deviceSerial,
                                     @Param("channelNo") Integer channelNo, @Param("index") Integer index);
}
