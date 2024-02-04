package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.List;

public enum DisplayDensity {

    ALL(1, "all"),
    HOUR(2, "1h"),
    DAY(3, "1d"),
    WEEK(4, "7d"),
    MONTH(5, "30d"),
    YEAR(6, "365d"),

    TWO_HOUR(7, "2h"),

    FOUR_HOUR(8, "4h"),

    SIX_HOUR(9, "6h"),

    TWELVE_HOUR(10, "12h"),

    SPECIAL_DAY(11, "1d")
    ;

    private int value;
    private String name;

    DisplayDensity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static DisplayDensity fromValue(int value) {
        for (DisplayDensity density : DisplayDensity.values()) {
            if (density.value == value) {
                return density;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    /**
     * 是否需要额外进行分组处理<br>
     * 公司使用的InfluxDB版本不支持直接进行自然年/月/周的分组，因此如果需要以上分组则需使用{@code #calculateStatistics}方法进行单独的分组处理
     *
     * @see cn.shmedo.monitor.monibotbaseapi.util.InfluxDBDataUtil#calculateStatistics(List, Integer, Integer, Boolean)
     */
    public static boolean needExtraGrouping(final DisplayDensity displayDensity) {
        return WEEK.equals(displayDensity) || MONTH.equals(displayDensity) || YEAR.equals(displayDensity);
    }
}
