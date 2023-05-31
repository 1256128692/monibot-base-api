package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-21 11:45
 * @desc: 比较区间枚举
 */
public enum CompareInterval {
    //水位-水位
    DIS0(2, "distance", "汛限水位", "超汛限水位", 1),
    DIS1(2, "distance", "旱限水位", "低于旱限水位", -1),
    DIS2(2, "distance", "警戒水位", "超警戒水位", 1),
    DIS3(2, "distance", "保证水位", "超保证水位", 1),

    //雨量-雨量
    RAIN(31, "rainfall", "警戒雨量", "超警戒雨量", 1),

    //雨量-日降雨量
    D_RAIN0(31, "dailyRainfall", "暴雨", "暴雨", 1),
    D_RAIN1(31, "dailyRainfall", "大暴雨", "大暴雨", 1),
    D_RAIN2(31, "dailyRainfall", "特大暴雨", "特大暴雨", 1),

    //水质-PH
    PH0(4, "ph", "大于等于", "PH超标", 1),
    PH1(4, "ph", "小于等于", "PH超标", -1),

    //水质等级
    W_QUALITY0(4, "waterQuality", "劣于I类", "水质等级超标", -1),
    W_QUALITY1(4, "waterQuality", "劣于II类", "水质等级超标", -1),
    W_QUALITY2(4, "waterQuality", "劣于Ⅲ类", "水质等级超标", -1),
    W_QUALITY3(4, "waterQuality", "劣于IV类", "水质等级超标", -1),
    W_QUALITY4(4, "waterQuality", "劣于V类", "水质等级超标", -1),

    //流量-流速
    VELOCITY_FLOW(3, "velocityFlow", "警戒流速", "超警戒流速", 1),

    //流量-流量
    VOLUME_FLOW0(3, "volumeFlow", "警戒流量", "超警戒流量", 1),
    VOLUME_FLOW1(3, "volumeFlow", "最小生态流量", "低于最小生态流量", -1),

    //视频-设备离线
    VIDEO_OFFLINE(31, "offline", "离线", "设备离线", 1);
    private final Integer monitorType;
    private final String fieldToken;
    private final String compareIntervalName;
    private final String desc;
    // 展示处理
    // -1 -> (-∞,value)，如果有其他-1的值小于它 -> (其他-1值, value);
    // 0 -> [0,value),如果有其他0的值小于它 -> (其他0值, value);
    // 1 -> (value,+∞)，如果有其他1的值大于它 -> (value, 其他1值).
    private final Integer showType;

    public Integer getMonitorType() {
        return monitorType;
    }

    public String getFieldToken() {
        return fieldToken;
    }

    public String getCompareIntervalName() {
        return compareIntervalName;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getShowType() {
        return showType;
    }

    CompareInterval(Integer monitorType, String fieldToken, String compareIntervalName, String desc, Integer showType) {
        this.monitorType = monitorType;
        this.fieldToken = fieldToken;
        this.compareIntervalName = compareIntervalName;
        this.desc = desc;
        this.showType = showType;
    }

    public static CompareInterval getValue(Integer monitorType, String fieldToken, String name) {
        if (Objects.nonNull(monitorType) && Objects.nonNull(fieldToken) && Objects.nonNull(name)) {
            for (CompareInterval value : values()) {
                if (value.monitorType.equals(monitorType) && value.fieldToken.equals(fieldToken)
                        && value.compareIntervalName.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }

    public static boolean specialConcat(String compareIntervalName) {
        return PH0.compareIntervalName.equals(compareIntervalName) || PH1.compareIntervalName.equals(compareIntervalName);
    }

    public static boolean notSpecialConcat(String compareIntervalName) {
        return !specialConcat(compareIntervalName);
    }
}
