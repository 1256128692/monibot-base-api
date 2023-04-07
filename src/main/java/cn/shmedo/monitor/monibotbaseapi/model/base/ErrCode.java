package cn.shmedo.monitor.monibotbaseapi.model.base;

/**
 * Created on 2015/4/23
 *
 * @author Liudongdong
 */

public class ErrCode {

    public static final int NONE = 0;
    public static final int ERR_USERNAME = 1;
    public static final int ERR_PASSWORD = 2;
    public static final int ERR_POWER = 3;
    public static final int ERR_API = 4;
    public static final int ERR_VERSION = 5;
    public static final int ERR_NO_SENSORTYPE = 6;
    public static final int ERR_NO_SENSOR = 7;
    /**
     * 服务内部错误
     */
    public static final int ERR_SERVER_INTERNAL_ERROR = 8;
    /**
     * 不允许的访问终端
     */
    public static final int ERR_ACCESS_TYPE = 9;
    /**
     * token已在别处登录
     */
    public static final int ERR_RACE_ACCESS_TOKEN = 10;
    /**
     * 非法的访问标记，或者访问标记已过期
     */
    public static final int ERR_INVALID_ACCESS_TOKEN = 11;
    /**
     * 没有权限
     */
    public static final int ERR_NO_PERMISSION = 12;
    /**
     * 非法的参数
     */
    public static final int ERR_INVALID_PARAMETER = 13;
    /**
     * 服务未授权或者已过期
     */
    public static final int ERR_SERVICE_NOT_AUTHENTICATION = 14;
    /**
     * 微信登录失败
     */
    public static final int ERR_WX_LOGIN_ERROR = 15;
    /**
     * 不包含access_type的HttpHead或者access_type的值错误
     */
    public static final int ERR_INVALID_ACCESS_TYPE_STRING = 16;
    /**
     * 当前用户已被禁用
     */
    public static final int ERR_USER_DISABLE = 17;
    /**
     * 手机号验证码正确，但是相应的用户不存在
     */
    public static final int ERR_CELL_PHONE_USER_NOT_EXISTS = 18;
    /**
     * 手机号或者验证码错误
     */
    public static final int ERR_CELL_PHONE_OR_CODE_ERROR = 19;
    /**
     * 系统初始化数据错误
     */
    public static final int ERR_SYSTEM_INIT_DATA_ERROR = 20;
    /**
     * 不包含app_key(app_secret)或者错误的app_key(app_secret)
     */
    public static final int ERR_APP_KEY_OR_SECRET = 21;
    /**
     * Redis事务错误
     */
    public static final int ERR_REDIS_TRANSACTION_ERROR = 22;
    /**
     * 资源限制
     */
    public static final int ERR_RESOURCES_LIMIT = 23;
    /**
     * 第三方服务调用失败
     */
    public static final int ERR_THIRD_PARTY_SERVICE_INVOKE_ERROR = 24;

    /**
     * 访问过于频繁
     */
    public static final int ERR_INVOKE_FREQUENCY_ERROR = 25;
    /**
     * 文件资源未找到
     */
    public static final int ERR_RESOURCE_NOT_FOUND = 26;
    /**
     * 文件格式错误
     */
    public static final int ERR_FILE_FORMAT_ERROR = 28;
    /**
     * 用户已过有效期
     */
    public static final int ERR_USER_EXPIRED_ERROR = 29;
    /**
     * 校验器开启后校验异常
     */
    public static final int ERR_VALIDATOR_ERROR = 30;
    /**
     * 用户已限制登录
     */
    public static final int ERR_USER_LOGIN_LIMIT_ERROR = 31;

    public static String getErrorString(int errCode) {
        switch (errCode) {
            case ERR_USERNAME:
                return "用户名或者密码错误";
            case ERR_SERVER_INTERNAL_ERROR:
                return "服务内部错误";
            case ERR_ACCESS_TYPE:
                return "不允许此种类型的访问终端登录";
            case ERR_RACE_ACCESS_TOKEN:
                return "当前用户已在别处登录";
            case ERR_INVALID_ACCESS_TOKEN:
                return "非法的访问标记或者访问标记已失效";
            case ERR_NO_PERMISSION:
                return "没有权限";
            case ERR_INVALID_PARAMETER:
                return "非法的参数";
            case ERR_SERVICE_NOT_AUTHENTICATION:
                return "服务未授权或者已过期";
            case ERR_WX_LOGIN_ERROR:
                return "微信登录失败";
            case ERR_INVALID_ACCESS_TYPE_STRING:
                return "不包含access_type的httphead或者access_type的值错误";
            case ERR_USER_DISABLE:
                return "当前用户已被禁用";
            case ERR_CELL_PHONE_USER_NOT_EXISTS:
                return "手机号对应的用户不存在";
            case ERR_CELL_PHONE_OR_CODE_ERROR:
                return "手机号或者验证码错误";
            case ERR_SYSTEM_INIT_DATA_ERROR:
                return "系统初始化数据错误";
            case ERR_APP_KEY_OR_SECRET:
                return "不包含app_key(app_secret)或者错误的app_key(app_secret)";
            case ERR_REDIS_TRANSACTION_ERROR:
                return "Redis事务错误";
            case ERR_RESOURCES_LIMIT:
                return "资源限制";
            case ERR_THIRD_PARTY_SERVICE_INVOKE_ERROR:
                return "第三方服务调用失败";
            case ERR_INVOKE_FREQUENCY_ERROR:
                return "当前访问过于频繁，请稍后重试";
            case ERR_RESOURCE_NOT_FOUND:
                return "文件资源未找到";
            case ERR_FILE_FORMAT_ERROR:
                return "文件格式错误";
            case ERR_USER_EXPIRED_ERROR:
                return "用户已过有效期";
            case ERR_VALIDATOR_ERROR:
                return "校验器开启后校验异常";
            case ERR_USER_LOGIN_LIMIT_ERROR:
                return "用户已限制登录，请通过手机找回密码或者联系管理员重置密码";
            default:
                return null;
        }
    }
}
