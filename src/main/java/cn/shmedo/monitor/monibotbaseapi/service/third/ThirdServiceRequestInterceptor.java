package cn.shmedo.monitor.monibotbaseapi.service.third;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 15:35
 **/
public class ThirdServiceRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", DefaultConstant.JSON);
    }
}
