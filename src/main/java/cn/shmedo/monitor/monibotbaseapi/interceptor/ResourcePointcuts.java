package cn.shmedo.monitor.monibotbaseapi.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created on 18/1/24.
 *
 * @author Liudongdong
 */
@Aspect
public class ResourcePointcuts {

    @Pointcut("execution(*  cn.shmedo.monitor.monibotbaseapi.controller..*.*(..))")
    public void resourceMethod() {
    }

}
