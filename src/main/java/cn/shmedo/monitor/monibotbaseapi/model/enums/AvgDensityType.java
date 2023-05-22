package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum AvgDensityType {

    ALL(0),
    DAILY(1),
    MONTHLY(2),
    YEARLY(3);

    private final int value;

    private AvgDensityType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean isValidDensity(int value) {
        for (AvgDensityType density : AvgDensityType.values()) {
            if (density.getValue() == value) {
                return true;
            }
        }
        return false;
    }

}
