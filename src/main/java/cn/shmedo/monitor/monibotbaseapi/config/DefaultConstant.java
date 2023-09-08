package cn.shmedo.monitor.monibotbaseapi.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

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

    public static final String LIST_PROJECT = "ListBaseProject";
    public static final String MONITOR_TEMPLATE_PARAM_NAME = "nameList";

    /**
     * 萤石TOKEN过期时间
     */
    public static final int YS_TOKEN_EXPIRE_DAY = 6;


    public static final int YS_DEFAULT_SPEED = 1;

    public static final long NORMAL_FILE_EXPIRE_MILLI = 3600 * 1000;
    public static final List<Integer> MDWT_PROJECT_TYPE_LIST = Arrays.asList(1, 2, 3, 4);


    public static final int ORDER_NOT_STAT = 1;

    public static final String VIDEO_DEVICE_SN = "seqNo";
    public static final String VIDEO_CHANNEL = "channelNo";

    public static final String VIDEO_IMAGECAPTURE = "imageCapture";

    public static final String VIDEO_CAPTUREINTERVAL = "captureInterval";


}
