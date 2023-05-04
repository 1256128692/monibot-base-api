package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys;

/**
 * 萤石云接口返回状态码
 */
public class YsCode {
    /**
     * 操作成功
     */
    public static final String SUCCESS = "200";
    /**
     * 设备不存在
     */
    public static final String DEVICE_NOT_EXISTS = "20002";
    /**
     * deviceSerial不合法
     */
    public static final String INVALID_DEVICE_SERIAL = "20014";
    /**
     * 该用户不拥有该设备
     */
    public static final String NOT_OWNED_BY_USER = "20018";
}
