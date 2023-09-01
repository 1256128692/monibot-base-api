package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum AccessPlatformType {

    YING_SHI((byte) 0, "萤石云平台"),
    HAI_KANG((byte) 1, "海康平台");

    private final byte value;
    private final String description;

    AccessPlatformType(byte value, String description) {
        this.value = value;
        this.description = description;
    }

    public byte getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isInvalidPlatform(byte value) {
        for (AccessPlatformType platform : AccessPlatformType.values()) {
            if (platform.getValue() == value) {
                return false;
            }
        }
        return true;
    }
}
