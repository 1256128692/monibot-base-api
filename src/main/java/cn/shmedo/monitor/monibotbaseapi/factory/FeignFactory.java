package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.monitor.monibotbaseapi.config.CommonBeans;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.util.function.Consumer;

/**
 * Feign客户端工厂<br>
 *
 * @author Chengfs on 2022/12/1
 */
public class FeignFactory {

    /**
     * 获得带熔断的远程服务实例, 自定义构造过程
     *
     * @param tClass          服务接口
     * @param url             服务地址
     * @param fallbackFactory 服务降级工厂 {@link FallbackFactory}
     * @param handler         服务构造器 {@link HystrixFeign.Builder}
     * @return 远程服务实例
     */
    public static <T> T hystrixClient(Class<T> tClass, String url, FallbackFactory<T> fallbackFactory,
                                      @Nullable Consumer<HystrixFeign.Builder> handler) {
        HystrixFeign.Builder builder = HystrixFeign.builder();
        if (handler != null) {
            handler.accept(builder);
        }
        return builder.target(tClass, url, fallbackFactory);
    }

    /**
     * 获得带熔断的远程服务实例, 使用默认配置<br>
     * 默认序列化: {@link CommonBeans#camelObjectMapper()}<br>
     * 默认请求头: {@code APP_KEY}、{@code APP_SECRET}、{@code CONTENT_TYPE}<br>
     *
     * @param tClass 服务接口
     * @param url    服务地址
     * @return 远程服务实例
     */
    public static <T> T hystrixClient(Class<T> tClass, String url, FallbackFactory<T> fallbackFactory) {
        ObjectMapper objectMapper = SpringUtil.getBean(CommonBeans.CAMEL_OBJECT_MAPPER, ObjectMapper.class);
        FileConfig config = SpringUtil.getBean(FileConfig.class);
        return hystrixClient(tClass, url, fallbackFactory, value -> value.encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .requestInterceptor(template -> template
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(DefaultConstant.APP_KEY, config.getAuthAppKey())
                        .header(DefaultConstant.APP_SECRET, config.getAuthAppSecret())));
    }

    /**
     * 获得远程服务实例，自定义构造过程
     *
     * @param tClass  服务接口
     * @param url     服务地址
     * @param handler 服务构造器 {@link Feign.Builder}
     * @return 远程服务实例
     */
    public static <T> T client(Class<T> tClass, String url, @Nullable Consumer<Feign.Builder> handler) {
        Feign.Builder builder = Feign.builder();
        if (handler != null) {
            handler.accept(builder);
        }
        return builder.target(tClass, url);
    }

    /**
     * 获得远程服务实例，使用默认配置<br>
     * 默认序列化: {@link CommonBeans#camelObjectMapper()}<br>
     * 默认请求头: {@code APP_KEY}、{@code APP_SECRET}、{@code CONTENT_TYPE}<br>
     *
     * @param tClass 服务接口
     * @param url    服务地址
     * @return 远程服务实例
     */
    public static <T> T client(Class<T> tClass, String url) {
        ObjectMapper objectMapper = SpringUtil.getBean(CommonBeans.CAMEL_OBJECT_MAPPER, ObjectMapper.class);
        FileConfig config = SpringUtil.getBean(FileConfig.class);

        return client(tClass, url, value -> value.encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .requestInterceptor(template -> template
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(DefaultConstant.APP_KEY, config.getAuthAppKey())
                        .header(DefaultConstant.APP_SECRET, config.getAuthAppSecret())));
    }

}

    
    