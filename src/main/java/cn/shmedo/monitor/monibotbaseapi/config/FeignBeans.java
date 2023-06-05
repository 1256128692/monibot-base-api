package cn.shmedo.monitor.monibotbaseapi.config;


import cn.shmedo.monitor.monibotbaseapi.factory.*;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import cn.shmedo.monitor.monibotbaseapi.service.third.wt.WtReportService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import feign.Request;
import feign.hystrix.SetterFactory;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Feign实例Bean
 *
 * @author Chengfs on 2022/12/5
 */
@Component
@AllArgsConstructor
public class FeignBeans {
    private final FileConfig config;
    private final ObjectMapper objectMapper;
    private final UserServiceFallbackFactory userServiceFallbackFactory;
    private final IotServiceFallbackFactory iotServiceFallbackFactory;
    private final MdInfoServiceFallbackFactory mdInfoServiceFallbackFactory;
    private final WtReportServiceFallbackFactory wtReportServiceFallbackFactory;
    private final YsServiceFallbackFactory ysServiceFallbackFactory;

    @Bean
    @Primary
    public UserService userService() {
        return FeignFactory.hystrixClient(UserService.class, config.getAuthServiceAddress(), userServiceFallbackFactory,
                value -> value.encoder(new JacksonEncoder(objectMapper))
                        .decoder(new JacksonDecoder(objectMapper))
                        .requestInterceptor(template -> template
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    @Bean
    @Primary
    public IotService iotService() {
        return FeignFactory.hystrixClient(IotService.class,
                config.getIotServiceAddress(), iotServiceFallbackFactory);
    }

    @Bean
    public MdInfoService mdInfoService() {
        SetterFactory timeoutCommandKey = (target, method) -> HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(MdInfoService.class.getSimpleName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(method.getName()))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(30000));
        return FeignFactory.hystrixClient(MdInfoService.class, config.getMdInfoServiceAddress(),
                mdInfoServiceFallbackFactory, value -> value.encoder(new JacksonEncoder(objectMapper))
                        .decoder(new JacksonDecoder(objectMapper))
                        .setterFactory(timeoutCommandKey)
                        .options(new Request.Options(10, TimeUnit.SECONDS, 20, TimeUnit.SECONDS, true))
                        .requestInterceptor(template -> template
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(DefaultConstant.APP_KEY, config.getAuthAppKey())
                                .header(DefaultConstant.APP_SECRET, config.getAuthAppSecret())));
    }

    @Bean
    @Primary
    public WtReportService wtReportService() {
        return FeignFactory.hystrixClient(WtReportService.class,
                config.getWtReportServiceAddress(), wtReportServiceFallbackFactory);
    }

    @Bean
    @Primary
    public YsService ysService() {
        return FeignFactory.hystrixClient(YsService.class, config.getYsUrl(),
                ysServiceFallbackFactory, value -> value.encoder(new JacksonEncoder(objectMapper))
                        .decoder(new JacksonDecoder(objectMapper)));
    }
}

    
    