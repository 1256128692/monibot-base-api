package cn.shmedo.monitor.monibotbaseapi.interceptor;

/**
 * 数字越小，优先级越高
 * 注意在interceptor中的异常处理，以免自定义异常被包装成通用异常
 */
public class InterceptorOrder {


    /**
     * 接口返回结果包装成统一类型
     */
    public static final int WRAP_RESULT = 1;

    /**
     * 设置当前接口的调用主体
     */
    public static final int CURRENT_USER_SET = 2;

    /**
     * 接口性能统计
     */
    public static final int PERFORMANCE = 3;

    /**
     * 接口参数的逻辑校验
     */
    public static final int VALIDATE_PARAMETER = 5;

    /**
     * 接口权限校验
     */
    public static final int VALIDATE_PERMISSION = 10;

    /**
     * 操作日志记录
     */
    public static final int LOG = 15;
}
