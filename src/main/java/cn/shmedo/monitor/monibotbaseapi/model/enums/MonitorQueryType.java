package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum MonitorQueryType {

    ENVIRONMENT(0),
    SAFETY(1),
    WORK_STATUS(2),
    FLOOD_CONTROL(3),
    VIDEO(4),

    WATER(5),
    RAIN(6),
    AGRICULTURE(7),
    WORKSITE(8);

    private int value;

    MonitorQueryType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean contains(int value) {
        for (MonitorQueryType type : MonitorQueryType.values()) {
            if (type.getValue() == value) {
                return true;
            }
        }
        return false;
    }
}
