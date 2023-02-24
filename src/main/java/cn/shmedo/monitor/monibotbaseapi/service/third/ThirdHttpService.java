package cn.shmedo.monitor.monibotbaseapi.service.third;

import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 15:43
 **/
public class ThirdHttpService {
    public static final int Auth = 1;
    public static <T> T getInstance(Class<T> tClass , int direct) {
        ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
        T instance = Feign.builder()
                .requestInterceptor(new ThirdServiceRequestInterceptor())
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(tClass, getUrlByDirect(direct));
        return instance;
    }


    private static String getUrlByDirect(int direct) {
        FileConfig fileConfig = ContextHolder.getBean(FileConfig.class);
        switch (direct){
            case Auth -> {
                return fileConfig.getAuthServiceAddress();
            }
            default -> {
                throw new CustomBaseException("获取第三方服务地址出错", new Exception());
            }
        }
    }
}
