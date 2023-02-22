package cn.shmedo.monitor.monibotbaseapi.service.third.auth;


import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * 米度统一验证授权服务
 */
public class AuthHttpService {

    public static <T> T getInstance(Class<T> tClass) {
        FileConfig fileConfig = ContextHolder.getBean(FileConfig.class);
        String url = fileConfig.getAuthServiceAddress();
        ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
        T instance = Feign.builder()
                .requestInterceptor(new AuthRequestInterceptor())
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(tClass, url);
        return instance;
    }

}
