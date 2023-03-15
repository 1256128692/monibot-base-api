package cn.shmedo.monitor.monibotbaseapi.util;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultJsonConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Created by liudongdong on 2015/4/11.
 */
public class CommonVariable {
    public static final String SENSOR_DATA_QUERY_PATH = "/sensorDataQuery.properties";
    public static final String SENSOR_MAX_MIN_DATA_QUERY_PATH = "/sensorMaxAndMinDataQuery.properties";
    /**
     * 项目数据库的Hibernate配置文件，没有设置connection.url
     */
    public static final String PROJECTS_HIBERNATE_CONFIG_PATH = "/hibernateProj.cfg.xml";
    public static final int DEFAULT_PROJID = -1;
    public static final int DEFAULT_DEVICE_ID = -1;
    public static final Integer DEFAULT_AUTO_INCREMENT_ID = -1;
    public static final int DEFAULT_INT_AUTO_INCREMENT_ID = -1;
    public static final int DEFAULT_WARN_PRODUCE_TYPE = 0;
    public static final String DEFAULT_USER_PASSWORD = "111111";
    //    public static final String BASE_DATETIME = "2015-01-01 00:00:00";
    public static final String BASE_DATETIME = "2023-12-31 00:00:00";
    public static final String NOTHING_TO_RETURN = "";
    public static final String ACCESS_TOKEN_HEADER_NAME = "access_token";
    public static final String ACCESS_TYPE_HEADER_NAME = "access_type";
    public static final String APP_KEY_HEADER_NAME = "app_key";
    public static final String APP_SECRET_HEADER_NAME = "app_secret";
    public static final String JSON_WITH_UTF8 = "application/json;charset=UTF-8";
    public static final String GAUGE_MYSQL_SQL = "select 1";
    public static final int DEFAULT_NORMAL_STATUS = 0;
    public static final int DEFAULT_EXCEPTION_STATUS = 1;
    public static final String MAWAN_REPORT_PREFIX = "SZMWKTJ-DL-RB-";
    public static final String LIMIT_WARN = "临界浸润线值";
    public static final String CONTROL_WARN = "控制浸润线值";
    public static final String RG_ONE_HOUR_WARN = "一小时降雨量报警阈值";
    public static final Double RG_ONE_HOUR_WARN_VALUE = 16D;
    public static final String RG_THREE_HOUR_WARN = "三小时降雨量报警阈值";
    public static final Double RG_THREE_HOUR_WARN_VALUE = 20D;
    public static final String RG_SIX_HOUR_WARN = "六小时降雨量报警阈值";
    public static final Double RG_SIX_HOUR_WARN_VALUE = 25D;
    public static final String RG_TWELVE_HOUR_WARN = "十二小时降雨量报警阈值";
    public static final Double RG_TWELVE_HOUR_WARN_VALUE = 30D;
    public static final String RG_TWENTY_FOUR_HOUR_WARN = "二十四小时降雨量报警阈值";
    public static final Double RG_TWENTY_FOUR_HOUR_WARN_VALUE = 50D;
    public static final String DEFAULT_INSPECTION_MODULE_NAME = "default";
    public static final String DEFAULT_INSPECTION_NAME = "项目巡查表";
    public static final String USED_DEVICE_STATUS = "启用";
    public static final String UNUSED_DEVICE_STATUS = "未启用";
    public static final String IMAGE_FILE_TYPE = "image";
    public static final String VIDEO_FILE_TYPE = "video";
    public static final String BLUE_WARN_LEVEL_NAME = "蓝色预警";
    public static final String YELLOW_WARN_LEVEL_NAME = "黄色预警";
    public static final String ORANGE_WARN_LEVEL_NAME = "橙色预警";
    public static final String RED_WARN_LEVEL_NAME = "红色预警";
    public static final String MSG_TYPE_TEXT = "text";
    public static final String MSG_TYPE_MARKDOWN = "markdown";


    /**
     * 设备DES加密用的KEY
     */
    public static final String DEVICE_SUPER_KEY = "Px3i0jhQ";
    private static ObjectMapper objectMapper = null;
    private static String rootPath;
    /**
     * 是否是授权项目，通过注册码验证
     */
    private static boolean isValid = false;
    public static Integer projNumber;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            DefaultJsonConfig jsonConfig = new DefaultJsonConfig();
            objectMapper = jsonConfig.getContext(null);
        }
        return objectMapper;
    }

    public static ObjectMapper getIgnoreUnknownPropertyObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //要注意格式字母是区分大小写的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        objectMapper.setDateFormat(sdf);
        objectMapper.getDeserializationConfig().with(sdf);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static ObjectMapper getLowerCaseAndIgnoreUnknownPropertyObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        objectMapper.setDateFormat(sdf);
        objectMapper.getDeserializationConfig().with(sdf);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static boolean getIsValid() {
        return isValid;
    }

    public static void setIsValid(boolean isValid) {
        CommonVariable.isValid = isValid;
    }

    public static DateFormat getDefaultDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf;
    }

    public static DateTimeFormatter getDefaultLocalDateTimeFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter;
    }

    public static Timestamp getBaseTimestamp() {
        try {
            return new Timestamp(getDefaultDateFormat().parse(BASE_DATETIME).getTime());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootPath(String rootPath) {
        CommonVariable.rootPath = rootPath;
    }

    public static void setProjNumber(Integer projNumber) {
        CommonVariable.projNumber = projNumber;
    }

}
