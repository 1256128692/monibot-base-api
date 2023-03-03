package cn.shmedo.monitor.monibotbaseapi.config;


import cn.shmedo.monitor.monibotbaseapi.factory.FeignFactory;
import cn.shmedo.monitor.monibotbaseapi.factory.UserServiceFallbackFactory;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
    @Bean
    @Primary
    public UserService userService() {
        return FeignFactory.hystrixClient(UserService.class, config.getAuthServiceAddress(), userServiceFallbackFactory,
                value -> value.encoder(new JacksonEncoder(objectMapper))
                        .decoder(new JacksonDecoder(objectMapper))
                        .requestInterceptor(template -> template
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }


    /**
     * 通用构造过程，设置序列化器、拦截加入授权头
     *
     * @return {@link FeignFactory.Handler<HystrixFeign.Builder>}
     */
    private FeignFactory.Handler<HystrixFeign.Builder> generalHandler() {
        return value -> value.encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .requestInterceptor(template -> template
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(DefaultConstant.APP_KEY, config.getAuthAppKey())
                        .header(DefaultConstant.APP_SECRET, config.getAuthAppSecret()));
    }
}

    
    