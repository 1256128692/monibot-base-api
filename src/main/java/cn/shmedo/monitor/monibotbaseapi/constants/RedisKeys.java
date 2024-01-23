package cn.shmedo.monitor.monibotbaseapi.constants;

/**
 * redis 常量类
 *
 * @author Chengfs on 2023/3/1
 */
public class RedisKeys {

    /**
     * 行政区域缓存
     */
    public static final String REGION_AREA_KEY = "cn.shmedo.monitor.regionArea";

    /**
     * 公司信息缓存<br/>
     * 缓存位于iot-redis，请使用iotRedisTemplate、iotRedisService
     */
    public static final String IOT_COMPANY_INFO_KEY = "cn.shmedo.mdauth.companyInfo";

    /**
     * 物模型缓存<br/>
     * 缓存位于iot-redis，请使用iotRedisTemplate、iotRedisService
     */
    public static final String IOT_MODEL_KEY = "cn.shmedo.iot.model";

    /**
     * 监测类型缓存
     */
    public static final String MONITOR_TYPE_KEY = "cn.shmedo.iot.monitor.monitorType";

    /**
     * 监测类型模板缓存
     */
    public static final String MONITOR_TYPE_TEMPLATE_KEY = "cn.shmedo.iot.monitor.monitorTypeTemplate";

    /**
     * 参数缓存
     */
    public static final String PARAMETER_PREFIX_KEY = "cn.shmedo.iot.monitor.parameter:";

    /**
     * 萤石云Token
     */
    public static final String YS_TOKEN = "cn.shmedo.mdmbse.ysToken";

    /**
     * 文件路径缓存
     * key:value  带过期时间
     */
    public static final String FILE_PATH_KEY_TEMPLATE = "cn.shmedo.mdnet.fileCache:";

    public static final String COMPANY_ID_KEY = "cn.shmedo.mdauth.companyInfo";

    /**
     * 工程类型缓存
     */
    public static final String PROJECT_TYPE_KEY = "cn.shmedo.mdmbse.projectTypeCache";

    /**
     * 工程项目缓存
     */
    public static final String PROJECT_KEY = "cn.shmedo.mdmbse.projectCache";

    /**
     * 大屏产品服务设备统计
     */
    public static final String MONITOR_TYPE_DEVICE_COUNT = "cn.shmedo.monitor.monitorType.deviceCount";

    /**
     * 表单模板缓存
     */
    public static final String FORM_MODEL_KEY = "cn.shmedo.mdmbse.formModel";

    public static final String ASSET_HOUSE_KEY = "cn.shmedo.monitor.assetHouse";

    /**
     * 报警阈值配置 缓存<br/>
     * 类型：分组k-v
     */
    public static final String WARN_THRESHOLD = "cn.shmedo.monitor.warn.threshold:";

    /**
     * 报警触发配置 缓存<br/>
     * 类型：分组+ hash，hKey:{projectID}:{monitorItemID}<br/>
     * 示例：cn.shmedo.iot.monitor.warnTrigger:{platform}
     */
    public static final String WARN_TRIGGER = "cn.shmedo.monitor.warn.global:";

    /**
     * 沉默周期配置 缓存<br/>
     * 类型: 分组
     * 示例: cn.shmedo.iot.monitor.warn.silenceCycle:{thresholdID}
     */
    public static final String WARN_SILENCE_CYCLE = "cn.shmedo.monitor.warn.silenceCycle:";


    /**
     * 设备资产
     */
    public static final String DEVICE_ASSET_KEY = "cn.shmedo.monitor.project.deviceAsset";


    /**
     * 设备数据量
     */
    public static final String DEVICE_DATA_COUNT_KEY = "cn.shmedo.monitor.project.dataCount";
}

    
    