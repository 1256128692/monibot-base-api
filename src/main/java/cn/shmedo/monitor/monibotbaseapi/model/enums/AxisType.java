package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum AxisType {
    A(1, "A轴"),
    B(2, "B轴"),
    C(3, "C轴");

    private final int value;
    private final String name;

    private AxisType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
