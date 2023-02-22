package cn.shmedo.monitor.monibotbaseapi.service.third.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", CommonVariable.JSON_WITH_UTF8);
    }
}
