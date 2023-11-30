package cn.shmedo.monitor.monibotbaseapi.model.response.weather;

import lombok.Data;

import java.util.Date;

/**
 * @Author wuxl
 * @Date 2023/11/29 18:17
 * @PackageName:com.xxl.job.executor.data.response.results.weather.hefeng
 * @ClassName: Now
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class Now {
    /**
     * 数据观测时间
     **/
    private Date obsTime;
    /**
     * 温度，默认单位：摄氏度
     **/
    private int temp;
    /**
     * 体感温度，默认单位：摄氏度
     **/
    private int feelsLike;
    /**
     * 天气状况的图标代码
     **/
    private Integer icon;
    /**
     * 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
     **/
    private String text;
    /**
     * 风向360角度
     **/
    private int wind360;
    /**
     * 风向
     **/
    private String windDir;
    /**
     * 风力等级
     **/
    private int windScale;
    /**
     * 风速，公里/小时
     **/
    private int windSpeed;
    /**
     * 相对湿度，百分比数值
     **/
    private int humidity;
    /**
     * 当前小时累计降水量，默认单位：毫米
     **/
    private double precip;
    /**
     * 大气压强，默认单位：百帕
     **/
    private int pressure;
    /**
     * 能见度，默认单位：公里
     **/
    private int vis;
    /**
     * 云量，百分比数值。可能为空
     **/
    private Integer cloud;
    /**
     * 露点温度。可能为空
     **/
    private Integer dew;
}