package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.*;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 萤石云服务远程调用 熔断降级
 *
 * @author Chengfs on 2023/4/6
 */
@Slf4j
@Component
public class YsServiceFallbackFactory implements FallbackFactory<YsService> {
    @Override
    public YsService create(Throwable cause) {
        log.error("萤石云服务调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));

        return new YsService() {

            @Override
            public YsResultWrapper<YsTokenInfo> getToken(String appKey, String appSecret) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper<YsDeviceInfo> getDeviceInfo(String accessToken, String deviceSerial) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper<YsBaseDeviceInfo> getBaseDeviceInfo(String accessToken, String deviceSerial) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper<List<YsChannelInfo>> getDeviceChannelInfo(String accessToken, String deviceSerial) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper startPtz(String accessToken, String deviceSerial, Integer channelNo, Integer direction, Integer speed) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper stopPtz(String accessToken, String deviceSerial, Integer channelNo, Integer direction) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper<YsStreamUrlInfo> getStreamInfo(String accessToken, String deviceSerial, Integer channelNo,
                                                                  Integer protocol, String code, Integer expireTime,
                                                                  String type, Integer quality, String startTime,
                                                                  String stopTime, Integer supportH265, String playbackSpeed,
                                                                  String gbchannel) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper addDevice(String accessToken, String deviceSerial, String validateCode) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper<YsCapacityInfo> capacity(String accessToken, String deviceSerial) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }

            @Override
            public YsResultWrapper deleteDevice(String accessToken, String deviceSerial) {
                return YsResultWrapper.withCode("500", "萤石云服务调用失败");
            }
        };
    }

}

    
    