package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum RainDensityType {


    ALL(0, ""),
    DAILY(1, "1d"),
    MONTHLY(2, "30d"),
    YEARLY(3, "365d"),

    ONE_HOURS(4, "1h"),

    THREE_HOURS(5, "3h"),

    SIX_HOURS(6, "6h"),

    TWELVE_HOURS(7, "12h"),
    ;

    private final int value;
    private final String str;

    private RainDensityType(int value, String str) {
        this.value = value;
        this.str = str;
    }

    public int getValue() {
        return value;
    }

    public String getStr() {
        return str;
    }

    public static boolean isValidDensity(int value) {
        for (RainDensityType density : RainDensityType.values()) {
            if (density.getValue() == value) {
                return true;
            }
        }
        return false;
    }


    public static String getStringValue(int value) {
        for (RainDensityType density : RainDensityType.values()) {
            if (density.getValue() == value) {
                return density.getStr();
            }
        }
        return ""; // 如果没有匹配的值，则返回空字符串或者你认为合适的默认值
    }

}
