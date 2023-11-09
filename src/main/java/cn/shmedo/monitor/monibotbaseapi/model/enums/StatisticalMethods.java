package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum StatisticalMethods {

    LATEST(1, "最新一条"),
    AVERAGE(2, "平均"),
    CUMULATIVE(3, "阶段累积"),
    CHANGE(4, "阶段变化");

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
