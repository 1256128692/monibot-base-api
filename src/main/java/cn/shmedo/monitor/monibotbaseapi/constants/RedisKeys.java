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
     * 表单模板缓存
     */
    public static final String FORM_MODEL_KEY = "cn.shmedo.mdmbse.formModel";

    public static final String ASSET_HOUSE_KEY = "cn.shmedo.monitor.assetHouse";
}

    
    