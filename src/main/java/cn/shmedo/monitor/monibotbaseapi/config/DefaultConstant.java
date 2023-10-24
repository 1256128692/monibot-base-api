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

    /** ================================项目表单start================================ **/
    /** 表单复制时，填充后缀 **/
    public static final String PROPERTY_MODEL_COPY_SUFFIX = "_副本";

    /** 默认属性模板组 **/
    public static final int PROPERTY_MODEL_DEFAULT_GROUP = -1;

    /** 工程项目类型 **/
    public static final String REDIS_KEY_MD_AUTH_SERVICE = "cn.shmedo.mdauth.service";
    /** 企业树 **/
    public static final String REDIS_KEY_MD_COMPANY_PARENT = "cn.shmedo.mdauth.company.parent";
    /** ================================项目表单end================================ **/

    /**
     * 平台类型的合集
     */
    public static final List<String> platformTypeList = Arrays.asList("流域平台", "灌区平台", "水库平台", "MDNET", "林业平台");
    /**
     * 资产预警值比较方式的合集
     */
    public static final List<String> assetComparisonList = Arrays.asList(">", "<", "=", ">=", "<=");

    public static final int ORDER_NOT_STAT = 1;

    public static final String VIDEO_DEVICE_SN = "seqNo";
    public static final String VIDEO_CHANNEL = "channelNo";

    public static final String VIDEO_IMAGECAPTURE = "imageCapture";

    public static final String VIDEO_CAPTUREINTERVAL = "captureInterval";

    /**
     * 萤石预置点序号key
     */
    public static final String YS_PRESET_POINT_INDEX_KEY = "index";

    /**
     * 萤石设备异常码(设备不存在)
     */
    public static final String YS_DEVICE_ERROR_CODE_NO_EXIST = "20002";

    /**
     * 萤石接口调用通过msg
     */
    public static final String YS_DEVICE_NORMAL_MSG = "操作成功";

    /**
     * 海康视频参数key<br>
     * 这里仅注释它的含义,具体取值等需在对应接口上查看相应海康接口文档引用。
     */
    public interface HikVideoParamKeys {
        /**
         * 海康能力集key<br>
         * vss 视频能力集; ptz 云台操作能力集
         */
        String HIK_VSS_KEY = "vss";
        String HIK_PTZ_KEY = "ptz";

        /**
         * ws 取流协议
         */
        String HIK_PROTOCOL_WS = "ws";

        /**
         * 取流地址url,5min内有效
         */
        String HIK_STREAM_URL = "url";

        /**
         * 回放uuid,中心存储的视频回放时将被切分成多段，因此有一个uuid字段用于获取下一段回放（在首次调回放取流接口时会获取到这个字段）
         */
        String HIK_PLAYBACK_UUID = "uuid";

        /**
         * 海康设备deviceSerial
         */
        String HIK_DEVICE_SERIAL = "cameraIndexCode";

        /**
         * 码流类型
         */
        String HIK_STREAM_TYPE = "streamType";
        /**
         * 取流协议
         */
        String HIK_STREAM_PROTOCOL = "protocol";
        /**
         * 传输协议
         */
        String HIK_TRANS_MODE_PROTOCOL = "transmode";
        /**
         * 标识扩展内容
         */
        String HIK_EXPAND = "expand";
        /**
         * 输出码流转封装格式
         */
        String HIK_STREAM_FORM = "streamform";
        /**
         * 存储类型
         */
        String HIK_RECORD_LOCATION = "recordLocation";
        /**
         * 回放开始时间
         */
        String HIK_BEGIN_TIME = "beginTime";
        /**
         * 回放结束时间
         */
        String HIK_END_TIME = "endTime";
        /**
         * 录像的锁定类型
         */
        String HIK_LOCK_TYPE = "lockType";
        /**
         * url扩展字段
         */
        String HIK_EURL_EXPAND = "eurlExpand";
        /**
         * 云台动作 '开始'、'停止'
         */
        String HIK_ACTION = "action";
        /**
         * 云台动作,如'左转'等动作
         */
        String HIK_COMMAND = "command";
        /**
         * 云台速度
         */
        String HIK_SPEED = "speed";
        /**
         * 预置点编号
         */
        String HIK_PRESET_INDEX = "presetIndex";
        /**
         * 预置点名称
         */
        String HIK_PRESET_NAME = "presetName";
        /**
         * 通用返参code、msg和data
         */
        String HIK_CODE = "code";
        String HIK_MSG = "msg";
        String HIK_DATA = "data";

        /**
         * 成功返参
         */
        String HIK_SUCCESS_CODE = "0";
    }

    public interface HikVideoConstant {
        /**
         * 海康云台操作默认速度
         */
        Integer HIK_DEFAULT_PTZ_SPEED = 50;

        /**
         * 海康云台操作动作action
         */
        Integer HIK_PTZ_ACTION_START = 0;
        Integer HIK_PTZ_ACTION_END = 1;
    }
}
