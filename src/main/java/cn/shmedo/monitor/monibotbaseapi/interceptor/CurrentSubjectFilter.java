package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.BaseException;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.AuthHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(InterceptorOrder.CURRENT_USER_SET)
public class CurrentSubjectFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CurrentSubjectFilter.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String APP_KEY_HEADER = "appKey";
    private static final String APP_SECRET_HEADER = "appSecret";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String token = httpRequest.getHeader(TOKEN_HEADER);
            String appKey = httpRequest.getHeader(APP_KEY_HEADER);
            String appSecret = httpRequest.getHeader(APP_SECRET_HEADER);
            UserService userService = AuthHttpService.getInstance(UserService.class);
            ResultWrapper<CurrentSubject> currentSubjectResultWrapper = null;
            if (!ObjectUtil.isEmpty(token)) {
                token = token.replace("Bearer ", "");
                currentSubjectResultWrapper = userService.getCurrentSubject(token);
                if (currentSubjectResultWrapper == null) {
                    currentSubjectResultWrapper = ResultWrapper.withCode(ResultCode.INVALID_ACCESS_TOKEN);
                }
            } else {
                if (!ObjectUtil.isEmpty(appKey) && (!ObjectUtil.isEmpty(appSecret))) {
                    currentSubjectResultWrapper = userService.getCurrentSubjectByApp(appKey, appSecret);
                    if (currentSubjectResultWrapper == null) {
                        currentSubjectResultWrapper = ResultWrapper.withCode(ResultCode.APP_KEY_OR_SECRET);
                    }
                }
            }
            if (currentSubjectResultWrapper != null) {
                if (currentSubjectResultWrapper.apiSuccess()) {
                    CurrentSubjectHolder.setCurrentSubject(currentSubjectResultWrapper.getData());
                    if (currentSubjectResultWrapper.getData().getSubjectType().equals(SubjectType.APPLICATION)) {
                        Tuple<String, String> extractData = new Tuple<>(appKey, appSecret);
                        CurrentSubjectHolder.setCurrentSubjectExtractData(extractData);
                    } else {
                        CurrentSubjectHolder.setCurrentSubjectExtractData(token);
                    }
                } else {
                    endWithResultWrapper(currentSubjectResultWrapper, response);
                    return;
                }
            } else{
                CurrentSubjectHolder.setCurrentSubject(null);
            }
        } catch (Exception ex) {
            String result = getExceptionResult(ex);
            response.setContentType(CommonVariable.JSON);
            response.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
            /**
             * 请求终止
             */
            return;
        }
        chain.doFilter(request, response);
    }

    private void endWithResultWrapper(ResultWrapper resultWrapper, ServletResponse response) {
        try {
            ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
            String result = objectMapper.writeValueAsString(resultWrapper);
            response.setContentType(CommonVariable.JSON);
            response.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private String getExceptionResult(Exception ex) {
        try {
            ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
            ResultWrapper resultWrapper = null;
            if (ex instanceof BaseException) {
                ResultCode resultCode = ResultCode.valueOfCode(((BaseException) ex).errCode());
                String msg = ex.getMessage();
                if (ObjectUtil.isEmpty(msg)) {
                    msg = resultCode.toString();
                }
                resultWrapper = ResultWrapper.withCode(resultCode, msg);
            } else {
                resultWrapper = ResultWrapper.fail(ex);
            }
            return objectMapper.writeValueAsString(resultWrapper);
        } catch (Exception exOuter) {
            logger.error(exOuter.getMessage(), exOuter);
            return "handle exception error";
        }
    }
}
