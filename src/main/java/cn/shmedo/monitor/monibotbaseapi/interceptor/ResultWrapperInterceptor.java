package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.shmedo.iot.entity.api.ResultWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 拦截所有的API接口方法，包装其返回类型为ResultWrapper
 * 处理所有的异常，包装为相应的错误码和消息
 */
@Aspect
@Component
@Order(InterceptorOrder.WRAP_RESULT)
public class ResultWrapperInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ResultWrapperInterceptor.class);

    @Around(value = "cn.shmedo.monitor.monibotbaseapi.interceptor.ResourcePointcuts.resourceMethod()")
    public Object aroundResourceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
            Object result = joinPoint.proceed();
            if (!(result instanceof ResultWrapper)) {
                result = ResultWrapper.success(result);
            }
            return result;
//        } catch (Throwable throwable) {
//            logException(throwable);
//            if (throwable instanceof BaseException) {
//                BaseException baseException = (BaseException) throwable;
//                ResultCode resultCode = ResultCode.valueOfCode(baseException.errCode());
//                String message = baseException.getMessage();
//                if (message == null || message.length() <= 0) {
//                    message = resultCode.toString();
//                }
//                return ResultWrapper.withCode(resultCode, message);
//            }
//            return ResultWrapper.fail(throwable);
//        }
    }

    /**
     * 判断日志是否需要写入到日志文件，如果需要，将日志写入到文件中
     *
     * @param throwable
     */
    private void logException(Throwable throwable) {
        logger.error(throwable.getMessage(), throwable);
    }

}
