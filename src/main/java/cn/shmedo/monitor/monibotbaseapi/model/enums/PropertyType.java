package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 14:52
 **/

public enum PropertyType {
    Type_Number((byte)1,"数值"),
    Type_String((byte)2,"字符串"),
    Type_Enum((byte)3,"枚举"),
    Type_Date((byte)4,"数值");
    private Byte type;
    private String typeStr;

    PropertyType(Byte type, String typeStr) {
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
}
