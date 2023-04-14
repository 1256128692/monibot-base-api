package cn.shmedo.monitor.monibotbaseapi.config;

import java.text.SimpleDateFormat;

public class DefaultConstant {
    public static final String JSON = "application/json;charset=UTF-8";

    public static final String COLON = ":";

    public static final String MD_INFO_BUCKETNAME = "mdnet-normal";

    // 监测业务工程类型
    public static final Integer AUTH_RESOURSE = 9;

    public final static String APP_KEY = "appKey";

    public final static String APP_SECRET = "appSecret";

    public final static SimpleDateFormat SYSTEM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //上海米度测控有限公司ID
    public static final Integer MD_ID = 138;

    public static final String MDNET_SERVICE_NAME = "mdmbase";

    public static final String LIST_PROJECT = "ListProject";
    public static final String MONITOR_TEMPLATE_PARAM_NAME = "nameList";
}
