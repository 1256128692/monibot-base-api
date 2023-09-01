package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum AccessProtocolType {

    YING_SHI((byte) 0, "萤石云协议"),
    GUO_BIAO((byte) 1, "国标协议");

    private final byte value;
    private final String description;

    AccessProtocolType(byte value, String description) {
        this.value = value;
        this.description = description;
    }

    public byte getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isInvalidProtocol(byte value) {
        for (AccessProtocolType protocol : AccessProtocolType.values()) {
            if (protocol.getValue() == value) {
                return false;
            }
        }
        return true;
    }
}
