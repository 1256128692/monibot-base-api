package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;

/**
 * 智能设备类型，应用于位置
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-12-14 14:28
 **/
public enum IntelDeviceType4Location {
    IOT((byte) 0, "物联网设备"),
    VIDEO((byte) 1, "视频设备");


    private Byte type;
    private String desc;

    IntelDeviceType4Location(Byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isLegal(Byte type) {
        if (type == null) {
            return false;
        }
        return Arrays.stream(IntelDeviceType4Location.values()).anyMatch(e -> e.getType().equals(type));
    }

    public Byte getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
