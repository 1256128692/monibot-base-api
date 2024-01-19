package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;

/**
 * 项目所属平台的类型的枚举
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 13:49
 **/
public enum PlatformType {
    WATER((byte)1,"水文水利"),
    MINE((byte)2,"矿山"),
    LAND((byte)3,"国土地灾"),
    INFRASTRUCTURE((byte)4,"基建"),
    MDNET((byte) 5, "MD_Net3.0"),;


    private Byte  type;
    private String typeStr;


     PlatformType(Byte type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public Byte getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public static boolean validate(Byte type){
         return Arrays.stream(PlatformType.values()).anyMatch(item -> item.getType().equals(type));
    }

    /**
     * 根据类型获取枚举
     * @param type  类型
     * @return  枚举
     */
    public static PlatformType getPlatformType(Byte type) {
        return Arrays.stream(PlatformType.values())
                .filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    /**
     * 根据类型描述获取枚举
     * @param typeStr  类型描述
     * @return  枚举
     */
    public static PlatformType getByTypeStr(String typeStr) {
        return Arrays.stream(PlatformType.values())
                .filter(item -> item.getTypeStr().equals(typeStr)).findFirst().orElse(null);
    }
}
