package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum DisplayDensity {

    ALL(1, "all"),
    HOUR(2, "1h"),
    DAY(3, "1d"),
    WEEK(4, "1w"),
    MONTH(5, "30d"),
    YEAR(6, "365d"),

    TWO_HOUR(7, "2h"),

    FOUR_HOUR(8, "4h"),

    SIX_HOUR(9, "6h"),

    TWELVE_HOUR(10, "12h");


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
}
