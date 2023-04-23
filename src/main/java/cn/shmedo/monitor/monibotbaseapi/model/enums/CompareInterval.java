package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-21 11:45
 * @desc: 模板 =
 */
public enum CompareInterval {
    //水位-水位
    V01(2, "distance", "汛限水位", "超汛限水位", 1),
    V02(2, "distance", "旱限水位", "低于旱限水位", -1),
    V03(2, "distance", "警戒水位", "超警戒水位", 1),
    V04(2, "distance", "保证水位", "超保证水位", 1),

    //雨量-雨量
    V05(5, "rainfall", "暴雨", "", 1),
    V06(5, "rainfall", "大暴雨", "", 1),
    V07(5, "rainfall", "特大暴雨", "", 1),

    //雨量-日降雨量
    V08(5, "dailyRainfall", "暴雨", "", 1),
    V09(5, "dailyRainfall", "大暴雨", "", 1),
    V0a(5, "dailyRainfall", "特大暴雨", "", 1),

    //水质-PH
    V0b(4, "ph", "大于等于", "", 1),
    V0c(4, "ph", "小于等于", "", -1),

    //水质等级
    V0d(4, "waterQuality", "劣于I类", "", -1),
    V0e(4, "waterQuality", "劣于II类", "", -1),
    V0f(4, "waterQuality", "劣于Ⅲ类", "", -1),
    V10(4, "waterQuality", "劣于IV类", "", -1),
    V11(4, "waterQuality", "劣于V类", "", -1),

    //流量-流速
    V12(14, "velocityFlow", "警戒流速", "", 1),

    //流量-流量
    V13(14, "volumeFlow", "警戒流量", "", 1),
    V14(14, "volumeFlow", "最小生态流量", "", -1);
    private Integer monitorType;
    private String fieldToken;
    private String compareIntervalName;
    private String desc;
    //展示处理 -1->(-∞,value)，如果有其他-1的值小于它->(其他-1值,value); 0->(0,value); 1->(value,+∞)，如果有其他1的值大于它->(value,其他1值)
    private Integer showType;

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
        return V0b.compareIntervalName.equals(compareIntervalName) || V0c.compareIntervalName.equals(compareIntervalName);
    }

    public static boolean notSpecialConcat(String compareIntervalName) {
        return !specialConcat(compareIntervalName);
    }
}
