package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum MonitorClassType {

    ENVIRONMENT(0, "环境监测"),
    SECURITY(1, "安全监测"),
    WORK_STATUS(2, "工情监测"),
    FLOOD_CONTROL(3, "防洪调度指挥监测"),
    VIDEO(4, "视频监测");

    private final int value;
    private final String name;

    MonitorClassType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static MonitorClassType fromInt(int value) {
        for (MonitorClassType type : MonitorClassType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }

    public static boolean isValidValue(int value) {
        for (MonitorClassType type : MonitorClassType.values()) {
            if (type.value == value) {
                return true;
            }
        }
        return false;
    }

    public static String getNameFromInt(int value) {
        MonitorClassType type = fromInt(value);
        return type == null ? null : type.getName();
    }

}
