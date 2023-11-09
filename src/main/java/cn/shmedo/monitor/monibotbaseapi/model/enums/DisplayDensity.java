package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum DisplayDensity {

    ALL(1, "全部"),
    HOUR(2, "小时"),
    DAY(3, "日"),
    WEEK(4, "周"),
    MONTH(5, "月"),
    YEAR(6, "年");

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
