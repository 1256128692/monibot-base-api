package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.Holder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 18/3/16.
 *
 * @author Liudongdong
 */
@Aspect
@Component
@Order(InterceptorOrder.VALIDATE_PARAMETER)
public class ValidateParameterInterceptor {

    @Around(value = "cn.shmedo.monitor.monibotbaseapi.interceptor.ResourcePointcuts.resourceMethod()")
    public Object validate(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            ResultWrapper temp = validateParameter(args);
            if (temp != null) {
                return temp;
            }
            return joinPoint.proceed();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private ResultWrapper validateParameter(Object[] parameters) {
        if (parameters == null || parameters.length == 0)
            return null;
        List<Object> paList = Arrays.asList(parameters);
        Holder<ResultWrapper> resultWrapperHolder = new Holder<>(null);
        paList.forEach(obj -> {
            if (obj instanceof ParameterValidator) {
                ParameterValidator validator = (ParameterValidator) obj;
                ResultWrapper temp = validator.validate();
                if (temp != null) {
                    resultWrapperHolder.setData(temp);
                }
            }
        });
        return resultWrapperHolder.getData();
    }
}
