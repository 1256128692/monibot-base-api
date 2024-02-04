package cn.shmedo.monitor.monibotbaseapi.model.response.weather;

import lombok.Data;

import java.util.Date;

/**
 * @Author wuxl
 * @Date 2023/11/29 18:18
 * @PackageName:com.xxl.job.executor.data.response.results.weather.hefeng
 * @ClassName: Day
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class Day {
    /**
     * 预报日期
     **/
    private Date fxDate;
    /**
     * 日出时间，在高纬度地区可能为空
     **/
    private String sunrise;
    /**
     * 日落时间，在高纬度地区可能为空
     **/
    private String sunset;
    /**
     * 当天月升时间，可能为空
     **/
    private String moonrise;
    /**
     * 当天月落时间，可能为空
     **/
    private String moonset;
    /**
     * 月相名称
     **/
    private String moonPhase;
    /**
     * 月相图标代码
     **/
    private Integer moonPhaseIcon;
    /**
     * 预报当天最高温度
     **/
    private int tempMax;
    /**
     * 预报当天最低温度
     **/
    private int tempMin;
    /**
     * 预报白天天气状况的图标代码
     **/
    private int iconDay;
    /**
     * 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
     **/
    private String textDay;
    /**
     * 预报夜间天气状况的图标代码，另请参考天气图标项目
     **/
    private Integer iconNight;
    /**
     * 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
     **/
    private String textNight;
    /**
     * 预报白天风向360角度
     **/
    private int wind360Day;
    /**
     * 预报白天风向
     **/
    private String windDirDay;
    /**
     * 预报白天风力等级
     **/
    private String windScaleDay;
    /**
     * 预报白天风速，公里/小时
     **/
    private int windSpeedDay;
    /**
     * 预报夜间风向360角度
     **/
    private int wind360Night;
    /**
     * 预报夜间当天风向
     **/
    private String windDirNight;
    /**
     * 预报夜间风力等级
     **/
    private String windScaleNight;
    /**
     * 预报夜间风速，公里/小时
     **/
    private int windSpeedNight;
    /**
     * 预报当天总降水量，默认单位：毫米
     **/
    private double precip;
    /**
     * 紫外线强度指数
     **/
    private int uvIndex;
    /**
     * 相对湿度，百分比数值
     **/
    private int humidity;
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
    private int cloud;
}
