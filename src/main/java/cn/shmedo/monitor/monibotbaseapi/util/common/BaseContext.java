package cn.shmedo.monitor.monibotbaseapi.util.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于ThreadLocal封装的工具类，用于保存用户id
 * @Author cyf
 * @Date 2023/2/22 13:36
 * @PackageName:com.cyf.exercise.common
 * @ClassName: BaseContext
 * @Description: TODO
 * @Version 1.0
 */

@Slf4j
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setUserID(Long id){
        log.info("存入当前登录用户id：{}",id);
        threadLocal.set(id);
    }

    public static Long getUserID(){
        log.info("获取当前登录用户id：{}",threadLocal.get());
        return threadLocal.get();
    }
}
