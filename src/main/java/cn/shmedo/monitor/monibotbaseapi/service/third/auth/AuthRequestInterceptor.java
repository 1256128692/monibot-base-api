package cn.shmedo.monitor.monibotbaseapi.service.third.auth;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", DefaultConstant.JSON);
    }
}
