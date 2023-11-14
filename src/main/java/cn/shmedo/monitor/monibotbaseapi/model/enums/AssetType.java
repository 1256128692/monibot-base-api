package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;

/**
 * 资产类型
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 16:58
 **/
public enum AssetType {
    DisasterReliefSupplies((byte) 10, "救灾物资"),
    SparePartsAndComponents((byte) 20, "备品备件"),
    ;
    private Byte code;
    private String desc;

    AssetType(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isExist(Byte type) {
        return Arrays.stream(AssetType.values()).anyMatch(e -> e.getCode().equals(type));
    }

    public static String getStrByCode(Byte assetType) {
        if (assetType == null) return null;
        return switch (assetType) {
            case 1 -> "救灾物资";
            case 2 -> "备品备件";
            default -> null;
        };
    }

    public Byte getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
