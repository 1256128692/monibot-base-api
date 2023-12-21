package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum StatisticalMethods {

    LATEST(1, "last"),
    AVERAGE(2, "avg"),
    CUMULATIVE(3, "sum"),
    CHANGE(4, "diff"),
    FIRST(5, "first");

    private int value;
    private String name;

    StatisticalMethods(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static StatisticalMethods fromValue(int value) {
        for (StatisticalMethods method : StatisticalMethods.values()) {
            if (method.value == value) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
