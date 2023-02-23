package cn.shmedo.monitor.monibotbaseapi.model.enums;

import io.lettuce.core.StrAlgoArgs;

import java.util.Arrays;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 13:49
 **/
public enum PlatformType {
    Water((byte)1,"水文水利"),
    Mine((byte)2,"矿山"),
    Land((byte)3,"国土地灾"),
    Infrastructure((byte)4,"基建"),
    MDNET((byte)5,"MD_Net3.0");


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
}
